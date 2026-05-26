package com.library.service;

import com.library.cache.BookSearchKey;
import com.library.cache.HashMapBSK;
import com.library.exception.ResourceNotFoundException;
import com.library.model.dto.BookDTOFields;
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
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {
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

    public List<BookDTO> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .stream()
                .map(BookMapper::toDto)
                .toList();
    }

    public BookDTO getById(Long id) {
        return BookMapper.toDto(
                bookRepository.findById(id) .orElseThrow(() ->
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

    private void invalidateCache() {
        cache.clear();
    }
}