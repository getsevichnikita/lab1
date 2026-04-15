package com.library.mapper;
import com.library.model.Author;
import com.library.model.AuthorDTO;
import com.library.model.Book;
import java.util.List;

public class AuthorMapper {
    private AuthorMapper() {}

    public static AuthorDTO toDto(Author author) {
        if (author == null) return null;
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setBookIds(
                author.getBooks() == null
                        ? List.of()
                        : author.getBooks()
                        .stream()
                        .map(Book::getId)
                        .toList()
        );
        return dto;
    }
}