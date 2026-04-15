package com.library.mapper;
import com.library.model.Category;
import com.library.model.Author;
import com.library.model.Book;
import com.library.model.BookDTO;
import java.util.List;
import org.springframework.web.bind.annotation.*;
@Component
public class BookMapper {

    public BookDTO toDto(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setPublicationYear(book.getPublicationYear());

        dto.setAuthorIds(
                book.getAuthors() == null
                        ? List.of()
                        : book.getAuthors().stream().map(Author::getId).toList()
        );

        dto.setCategoryIds(
                book.getCategories() == null
                        ? List.of()
                        : book.getCategories().stream().map(Category::getId).toList()
        );

        return dto;
    }
}