package com.library.service;

import com.library.cache.BookSearchKey;
import com.library.cache.HashMapBSK;
import com.library.exception.ResourceNotFoundException;
import com.library.model.dto.BookDTOFields;
import com.library.model.dto.BookDTOFieldsOwner;
import com.library.model.entity.BookPDF;
import com.library.repository.BookPDFRepository;
import com.library.repository.LoanRepository;
import lombok.extern.slf4j.Slf4j;
import com.library.mapper.BookMapper;
import com.library.model.entity.Author;
import com.library.model.entity.Book;
import com.library.model.dto.BookDTO;
import com.library.model.entity.Category;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {
    private final LoanRepository loanRepository;
    private final BookPDFRepository bookPDFRepository;
    private static final String LOG_BOOK_NOT_FOUND = "Book not found with id = ";
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final HashMapBSK<BookSearchKey, List<BookDTOFields>> cache = new HashMapBSK<>();

    public BookDTO create(BookDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setPublicationYear(dto.getPublicationYear());

        List<Author> authors = dto.getAuthorIds() == null
                ? List.of()
                : authorRepository.findAllById(dto.getAuthorIds());

        List<Category> categories = dto.getCategoryIds() == null
                ? List.of()
                : categoryRepository.findAllById(dto.getCategoryIds());

        book.setAuthors(authors);
        book.setCategories(categories);

        Book savedBook = bookRepository.save(book);

        invalidateCache();
        log.info("Book created with id={}", dto.getId());
        return BookMapper.toDto(savedBook);
    }

    @Transactional
    public List<BookDTOFieldsOwner> getAll(Pageable pageable) {
        List<Book> books = bookRepository.findAll(pageable).getContent();

        return books.stream().map(book -> {
            BookDTOFieldsOwner dto = BookMapper.toDtoFieldsOwner(book);

            bookPDFRepository.findByBookId(book.getId()).ifPresent(pdf ->
                    dto.setOwnerId(pdf.getOwnerId())
            );

            return dto;
        }).toList();
    }

    public BookDTO getById(Long id) {
        return BookMapper.toDto(
                bookRepository.findById(id).orElseThrow(() ->
                        new ResourceNotFoundException(
                                LOG_BOOK_NOT_FOUND + id
                        )
                )
        );
    }

    public BookDTO update(Long id, BookDTO dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                LOG_BOOK_NOT_FOUND + id
                        )
                );

        book.setTitle(dto.getTitle());
        book.setPublicationYear(dto.getPublicationYear());

        if (dto.getAuthorIds() != null) {
            book.setAuthors(authorRepository.findAllById(dto.getAuthorIds()));
        }

        if (dto.getCategoryIds() != null) {
            book.setCategories(categoryRepository.findAllById(dto.getCategoryIds()));
        }

        Book updatedBook = bookRepository.save(book);

        invalidateCache();
        log.info("Updating book with id={}", id);
        return BookMapper.toDto(updatedBook);
    }

    public void delete(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                LOG_BOOK_NOT_FOUND + id
                        )
                );

        bookPDFRepository.findByBookId(id)
                .ifPresent(bookPDFRepository::delete);

        for (Author author : new ArrayList<>(book.getAuthors())) {
            author.getBooks().remove(book);
        }
        book.getAuthors().clear();

        for (Category category : new ArrayList<>(book.getCategories())) {
            category.getBooks().remove(book);
        }
        book.getCategories().clear();

        log.info("Deleting book with id={}", id);
        bookRepository.delete(book);

        invalidateCache();
    }

    public List<BookDTOFields> searchByAuthorJPQL(
            String author,
            Pageable pageable
    ) {
        BookSearchKey key = new BookSearchKey(
                author,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        List<BookDTOFields> cached = cache.get(key);

        if (cached != null) {
            return cached;
        }

        List<BookDTOFields> result = bookRepository
                .findByAuthorNameJPQL(author, pageable)
                .stream()
                .map(BookMapper::toDtoFields)
                .toList();

        cache.put(key, result);
        return result;
    }


    public List<BookDTOFields> searchByAuthorNative(
            String author,
            Pageable pageable
    ) {
        BookSearchKey key = new BookSearchKey(
                author,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        List<BookDTOFields> cached = cache.get(key);

        if (cached != null) {
            return cached;
        }

        List<BookDTOFields> result = bookRepository
                .findByAuthorNameNative(author, pageable)
                .stream()
                .map(BookMapper::toDtoFields)
                .toList();

        cache.put(key, result);
        return result;
    }

    public BookDTO uploadBook(BookDTOFieldsOwner dto, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("PDF file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new RuntimeException("Only PDF allowed");
        }

        try {
            String safeFilename = extractSafeFilename(file.getOriginalFilename());

            Book book = new Book();
            book.setTitle(dto.getTitle());
            book.setPublicationYear(dto.getPublicationYear());

            List<Author> authors = dto.getAuthors().stream()
                    .map(a -> {
                        Optional<Author> existing = authorRepository.findByName(a.getName());
                        return existing.orElseGet(() -> {
                            Author author = new Author();
                            author.setName(a.getName());
                            return authorRepository.save(author);
                        });
                    })
                    .toList();

            book.setAuthors(authors);

            List<Category> categories = dto.getCategories().stream()
                    .map(c -> {
                        Optional<Category> existing = categoryRepository.findByName(c.getName());
                        return existing.orElseGet(() -> {
                            Category category = new Category();
                            category.setName(c.getName());
                            return categoryRepository.save(category);
                        });
                    })
                    .toList();

            book.setCategories(categories);

            Book savedBook = bookRepository.save(book);

            BookPDF pdf = new BookPDF();
            pdf.setBook(savedBook);
            pdf.setFileName(safeFilename);
            pdf.setFileData(file.getBytes());
            pdf.setUploadedAt(LocalDateTime.now());
            pdf.setOwnerId(dto.getOwnerId());
            bookPDFRepository.save(pdf);

            return BookMapper.toDto(savedBook);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public void updatePdf(Long bookId, MultipartFile file) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        BookPDF pdf = bookPDFRepository.findByBookId(bookId)
                .orElse(new BookPDF());

        String safeFilename = extractSafeFilename(file.getOriginalFilename());

        try {
            pdf.setBook(book);
            pdf.setFileData(file.getBytes());
            pdf.setFileName(safeFilename);
            pdf.setUploadedAt(LocalDateTime.now());
            bookPDFRepository.save(pdf);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    private String extractSafeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "file.pdf";
        }

        String name = new File(originalFilename).getName();

        if (name.isEmpty()) {
            return "file.pdf";
        }

        return name;
    }

    public Map<String, Object> getBookStats(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        List<Book> allBooks = bookRepository.findAll();

        BookPDF currentPdf = bookPDFRepository.findByBookId(bookId).orElse(null);
        byte[] currentPdfData = currentPdf != null ? currentPdf.getFileData() : null;

        Set<Long> currentAuthorIds = book.getAuthors().stream()
                .map(Author::getId)
                .collect(Collectors.toSet());

        Set<Long> currentCategoryIds = book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        List<Book> sameBooks = allBooks.stream()
                .filter(b -> b.getTitle().equals(book.getTitle()))
                .filter(b -> b.getPublicationYear() == book.getPublicationYear())
                .filter(b -> {
                    Set<Long> otherAuthorIds = b.getAuthors().stream()
                            .map(Author::getId)
                            .collect(Collectors.toSet());
                    return otherAuthorIds.equals(currentAuthorIds);
                })
                .filter(b -> {
                    Set<Long> otherCategoryIds = b.getCategories().stream()
                            .map(Category::getId)
                            .collect(Collectors.toSet());
                    return otherCategoryIds.equals(currentCategoryIds);
                })
                .filter(b -> {
                    BookPDF otherPdf = bookPDFRepository.findByBookId(b.getId()).orElse(null);
                    byte[] otherPdfData = otherPdf != null ? otherPdf.getFileData() : null;
                    return Arrays.equals(currentPdfData, otherPdfData);
                })
                .toList();

        long totalCopies = sameBooks.size();
        long borrowedCopies = sameBooks.stream()
                .mapToLong(b -> loanRepository.countActiveByBookId(b.getId()))
                .sum();
        long availableCopies = totalCopies - borrowedCopies;

        return Map.of(
                "totalCopies", totalCopies,
                "borrowedCopies", borrowedCopies,
                "availableCopies", availableCopies
        );
    }

    @Transactional(readOnly = true)
    public byte[] getBookCover(Long bookId) {
        BookPDF pdf = bookPDFRepository.findByBookId(bookId).orElse(null);
        if (pdf == null || pdf.getFileData() == null) {
            return new byte[0];
        }

        try (PDDocument document = Loader.loadPDF(pdf.getFileData())) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, 72); // 0 = первая страница, 72 DPI

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Failed to extract cover from PDF", e);
            return new byte[0];
        }
    }

    private void invalidateCache() {
        cache.clear();
    }
}