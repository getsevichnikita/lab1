package com.library.mapper;
import com.library.model.Author;
import com.library.model.Book;
import com.library.model.BookDTO;

import java.util.stream.Collectors;

public class BookMapper {

    private BookMapper() {}

    public static BookDTO toDto(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .publicationYear(book.getPublicationYear())
                .author(
                        book.getAuthors() == null ? "" :
                                book.getAuthors().stream()
                                        .map(Author::getName)
                                        .collect(Collectors.joining(", "))
                )
                .build();
    }
}