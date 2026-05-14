package com.library.mapper;
import com.library.model.AuthorDTO4BDTOF;
import com.library.model.CategoryDTO4BDTOF;
import com.library.model.CategoryDTO;
import com.library.model.AuthorDTO;
import com.library.model.Author;
import com.library.model.Book;
import com.library.model.BookDTO;
import com.library.model.BookDTOFields;
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
    public static BookDTOFields toDtoFields(Book book) {
        return BookDTOFields.builder()
                .id(book.getId())
                .title(book.getTitle())
                .publicationYear(book.getPublicationYear())
                .authors(
                        book.getAuthors().stream()
                                .map(a -> new AuthorDTO4BDTOF(a.getId(), a.getName()))
                                .toList()
                )
                .categories(
                        book.getCategories().stream()
                                .map(c -> new CategoryDTO4BDTOF(c.getId(), c.getName()))
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
                        .map(a -> {
                            Author author = new Author();
                            author.setId(a.getId());
                            author.setName(a.getName());
                            return author;
                        })
                        .toList()
        );

        book.setCategories(
                dto.getCategories().stream()
                        .map(c -> {
                            Category category = new Category();
                            category.setId(c.getId());
                            category.setName(c.getName());
                            return category;
                        })
                        .toList()
        );

        return book;
    }
}