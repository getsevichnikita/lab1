package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public Book getBookById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    public Book createBook(Book book) {
        return repository.save(book);
    }

    public Book updateBook(Long id, Book newBook) {
        Book book = getBookById(id);
        book.setTitle(newBook.getTitle());
        book.setPublicationYear(newBook.getPublicationYear());
        book.setAuthors(newBook.getAuthors());
        return repository.save(book);
    }

    public void deleteBook(Long  id) {
        repository.deleteById(id);
    }
}

