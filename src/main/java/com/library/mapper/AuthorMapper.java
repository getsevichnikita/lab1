package com.library.mapper;
import com.library.model.Author;
import com.library.model.AuthorDTO;
import com.library.model.Book;

import java.util.List;

public class AuthorMapper {

    public static AuthorDTO toDto(Author author) {
        if (author == null) return null;

        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());

        dto.setBookIds(
                author.getBooks()
                        .stream()
                        .map(Book::getId)
                        .toList()
        );

        return dto;
    }

    public static Author toEntity(AuthorDTO dto, List<Book> books) {
        if (dto == null) return null;

        Author author = new Author();
        author.setId(dto.getId());
        author.setName(dto.getName());
        author.setBooks(books);

        return author;
    }
}