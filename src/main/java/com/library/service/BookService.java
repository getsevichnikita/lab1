package com.library.service;
import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public Optional<Book> getBookById(Long id) {
        return repository.findById(id);
    }

    public List<Book> getBooksFiltered(String author, String title, Integer year) {
        return repository.getBooksFiltered(author, title, year);
    }
}

