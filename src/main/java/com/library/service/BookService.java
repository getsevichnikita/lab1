package com.library.service;
import com.library.cache.BookSearchKey;
import lombok.extern.slf4j.Slf4j;
import com.library.mapper.BookMapper;
import com.library.model.Author;
import com.library.model.Book;
import com.library.model.BookDTO;
import com.library.model.Category;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final HashMap<BookSearchKey, List<BookDTO>> cache = new HashMap<>();

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

        return BookMapper.toDto(savedBook);
    }

    public List<BookDTO> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .stream()
                .map(BookMapper::toDto)
                .toList();
    }

    public BookDTO getById(Long id) {
        return BookMapper.toDto(
                bookRepository.findById(id).orElseThrow()
        );
    }

    public BookDTO update(Long id, BookDTO dto) {

        Book book = bookRepository.findById(id)
                .orElseThrow();

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

        return BookMapper.toDto(updatedBook);
    }

    public void delete(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        for (Author author : new ArrayList<>(book.getAuthors())) {
            author.getBooks().remove(book);
        }
        book.getAuthors().clear();
        for (Category category : new ArrayList<>(book.getCategories())) {
            category.getBooks().remove(book);
        }
        book.getCategories().clear();
        bookRepository.delete(book);
        invalidateCache();
    }

    public List<BookDTO> searchByAuthorJPQL(
            String author,
            Pageable pageable
    ) {

        BookSearchKey key = new BookSearchKey(
                author,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        List<BookDTO> result = bookRepository
                .findByAuthorNameJPQL(author, pageable)
                .stream()
                .map(BookMapper::toDto)
                .toList();

        cache.put(key, result);

        return result;
    }

    public List<BookDTO> searchByAuthorNative(
            String author,
            Pageable pageable
    ) {

        BookSearchKey key = new BookSearchKey(
                author,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        if (cache.containsKey(key)) {
            log.info("FROM CACHE");
            return cache.get(key);
        }

        List<BookDTO> result = bookRepository
                .findByAuthorNameNative(author, pageable)
                .stream()
                .map(BookMapper::toDto)
                .toList();

        cache.put(key, result);
        log.info("FROM DATABASE");
        return result;
    }

    private void invalidateCache() {
        cache.clear();
    }
}