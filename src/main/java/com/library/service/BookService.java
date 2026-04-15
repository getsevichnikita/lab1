package com.library.service;

import com.library.mapper.BookMapper;
import com.library.model.Author;
import com.library.model.Book;
import com.library.model.BookDTO;
import com.library.model.Category;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

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

        return BookMapper.toDto(bookRepository.save(book));
    }

    public List<BookDTO> getAll() {
        return bookRepository.findAll()
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

        return BookMapper.toDto(bookRepository.save(book));
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}