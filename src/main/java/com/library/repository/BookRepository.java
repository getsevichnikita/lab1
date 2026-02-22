package com.library.repository;

import com.library.model.Book;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {

    private final List<Book> books = List.of(
            new Book(1L, "Book1", "Author1", 2020),
            new Book(2L, "Book2", "Author2", 2021),
            new Book(3L, "Book3", "Author3", 2019)
    );

    public Optional<Book> findById(Long id) {
        return books.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst();
    }

    public List<Book> getBooksFiltered(String author, String title, Integer publicationYear) {
        return books.stream()
                .filter(book -> author == null || author.isEmpty() || book.getAuthor().equalsIgnoreCase(author))
                .filter(book -> title == null || title.isEmpty() || book.getTitle().equalsIgnoreCase(title))
                .filter(book -> publicationYear == null || book.getPublicationYear() == publicationYear)
                .toList();
    }
}
