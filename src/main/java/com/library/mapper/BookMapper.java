package com.library.mapper;
import com.library.model.entity.Author;
import com.library.model.entity.Book;
import com.library.model.dto.BookDTO;
import com.library.model.dto.BookDTOFields;
import com.library.model.entity.Category;
import com.library.model.dto.AuthorDTO4BDTOF;
import com.library.model.dto.CategoryDTO4BDTOF;
import com.library.model.dto.BookDTOFieldsOwner;
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
    public static BookDTOFields toDtoFields(Book book) {
        return BookDTOFields.builder()
                .id(book.getId())
                .title(book.getTitle())
                .publicationYear(book.getPublicationYear())
                .authors(
                        book.getAuthors().stream()
                                .map(AuthorMapper::toDto4bdtof)
                                .toList()
                )
                .categories(
                        book.getCategories().stream()
                                .map(CategoryMapper::toDto4bdtof)
                                .toList()
                )
                .build();
    }

    public static Book toEntityFields(BookDTOFields dto) {
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setPublicationYear(dto.getPublicationYear());

        book.setAuthors(
                dto.getAuthors().stream()
                        .map(AuthorMapper::toEntity4bdtof)
                        .toList()
        );

        book.setCategories(
                dto.getCategories().stream()
                        .map(CategoryMapper::toEntity4bdtof)
                        .toList()
        );

        return book;
    }
    public static BookDTOFieldsOwner toDtoFieldsOwner(Book book) {
        return BookDTOFieldsOwner.builder()
                .id(book.getId())
                .title(book.getTitle())
                .publicationYear(book.getPublicationYear())
                .authors(book.getAuthors().stream()
                        .map(a -> new AuthorDTO4BDTOF(a.getId(), a.getName()))
                        .toList())
                .categories(book.getCategories().stream()
                        .map(c -> new CategoryDTO4BDTOF(c.getId(), c.getName()))
                        .toList())
                .build();
    }
}