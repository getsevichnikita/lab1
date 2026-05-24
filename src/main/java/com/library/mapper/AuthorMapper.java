package com.library.mapper;
import com.library.model.Author;
import com.library.model.AuthorDTO;
import com.library.model.AuthorDTO4BDTOF;
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
    public static AuthorDTO4BDTOF toDto4bdtof(Author author) {
        return new AuthorDTO4BDTOF(
                author.getId(),
                author.getName()
        );
    }

    public static Author toEntity4bdtof(AuthorDTO4BDTOF dto) {
        Author author = new Author();
        author.setId(dto.getId());
        author.setName(dto.getName());
        return author;
    }
}