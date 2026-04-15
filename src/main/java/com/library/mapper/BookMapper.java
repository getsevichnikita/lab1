package com.library.mapper;

import com.library.model.Author;
import com.library.model.Book;
import com.library.model.BookDTO;
import com.library.model.Category;

import java.util.List;

public class BookMapper {

    private BookMapper() {}

    public static BookDTO toDto(Book book) {
        if (book == null) return null;

        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setPublicationYear(book.getPublicationYear());

        dto.setAuthorIds(
                book.getAuthors() == null
                        ? List.of()
                        : book.getAuthors()
                        .stream()
                        .map(Author::getId)
                        .toList()
        );

        dto.setCategoryIds(
                book.getCategories() == null
                        ? List.of()
                        : book.getCategories()
                        .stream()
                        .map(Category::getId)
                        .toList()
        );

        return dto;
    }
}