package com.library.mapper;
import com.library.model.Book;
import com.library.model.BookDTO;
import org.springframework.stereotype.Component;
@Component
public class BookMapper {
    private BookMapper() {
    }
    public static BookDTO toDto(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPublicationYear(book.getPublicationYear());
        return dto;
    }
}