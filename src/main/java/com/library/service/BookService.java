package com.library.service;

import com.library.cache.BookSearchKey;
import com.library.cache.HashMapBSK;
import com.library.exception.ResourceNotFoundException;
import com.library.model.dto.BookDTOFields;
import com.library.model.dto.BookDTOFieldsOwner;
import com.library.model.entity.BookPDF;
import com.library.repository.BookPDFRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {
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

        BookPDF pdf = bookPDFRepository.findByBookId(id)
                .orElse(null);

        if (pdf != null) {
            try {
                Path path = Paths.get(pdf.getFilePath());
                Files.deleteIfExists(path);
            } catch (IOException e) {
                log.warn("Failed to delete file: {}", pdf.getFilePath());
            }

            bookPDFRepository.delete(pdf);
        }

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
            String uploadDir = "uploads/";
            File dir = new File(uploadDir);

            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    throw new RuntimeException("Failed to create upload directory");
                }
            }

            String safeFilename = extractSafeFilename(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + "_" + safeFilename;

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            Path path = uploadPath.resolve(fileName).normalize();

            if (!path.startsWith(uploadPath)) {
                throw new SecurityException("Invalid file path");
            }

            Files.write(path, file.getBytes());

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
            pdf.setFilePath(path.toString());
            pdf.setFileName(safeFilename);
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

        String uploadDir = "uploads/";
        String safeFilename = extractSafeFilename(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + "_" + safeFilename;

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        Path path = uploadPath.resolve(fileName).normalize();

        if (!path.startsWith(uploadPath)) {
            throw new SecurityException("Invalid file path");
        }

        try {
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }

        pdf.setBook(book);
        pdf.setFilePath(path.toString());
        pdf.setFileName(safeFilename);
        pdf.setUploadedAt(LocalDateTime.now());
        bookPDFRepository.save(pdf);
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

    private void invalidateCache() {
        cache.clear();
    }
}