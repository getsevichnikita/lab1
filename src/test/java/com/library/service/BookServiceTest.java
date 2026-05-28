package com.library.service;

import com.library.exception.ResourceNotFoundException;
import com.library.model.dto.AuthorDTO4BDTOF;
import com.library.model.dto.BookDTOFieldsOwner;
import com.library.model.dto.CategoryDTO4BDTOF;
import com.library.model.entity.Author;
import com.library.model.entity.Book;
import com.library.model.dto.BookDTO;
import com.library.model.dto.BookDTOFields;
import com.library.model.entity.BookPDF;
import com.library.model.entity.Category;
import com.library.repository.AuthorRepository;
import com.library.repository.BookPDFRepository;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookPDFRepository bookPDFRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void create_shouldSaveBook() {

        BookDTO dto = new BookDTO();
        dto.setTitle("1984");
        dto.setPublicationYear(1949);
        dto.setAuthorIds(List.of(1L));
        dto.setCategoryIds(List.of(1L));

        Author author = new Author();
        Category category = new Category();

        when(authorRepository.findAllById(any()))
                .thenReturn(List.of(author));

        when(categoryRepository.findAllById(any()))
                .thenReturn(List.of(category));

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("1984");

        when(bookRepository.save(any(Book.class)))
                .thenReturn(savedBook);

        BookDTO result = bookService.create(dto);

        assertEquals("1984", result.getTitle());
    }

    @Test
    void getAll_shouldReturnBooks() {

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book");

        when(bookRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(book)));

        List<BookDTOFieldsOwner> result =
                bookService.getAll(PageRequest.of(0, 10));

        assertEquals(1, result.size());
        assertEquals("Book", result.getFirst().getTitle());
    }

    @Test
    void getById_shouldReturnBook() {

        Book book = new Book();
        book.setId(1L);
        book.setTitle("1984");

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        BookDTO result = bookService.getById(1L);

        assertEquals("1984", result.getTitle());

        verify(bookRepository).findById(1L);
    }

    @Test
    void getById_shouldThrowResourceNotFoundException() {

        when(bookRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> bookService.getById(1L)
                );

        assertEquals(
                "Book not found with id = 1",
                ex.getMessage()
        );
    }

    @Test
    void update_shouldUpdateBook() {

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Old title");

        BookDTO dto = new BookDTO();
        dto.setTitle("New title");
        dto.setPublicationYear(2025);

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(existingBook));

        when(bookRepository.save(any(Book.class)))
                .thenReturn(existingBook);

        BookDTO result = bookService.update(1L, dto);

        assertEquals("New title", result.getTitle());
        assertEquals(2025, result.getPublicationYear());

        verify(bookRepository).save(existingBook);
    }

    @Test
    void update_shouldThrowResourceNotFoundException() {

        BookDTO dto = new BookDTO();

        when(bookRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.update(1L, dto)
        );
    }

    @Test
    void delete_shouldDeleteBook() {

        Book book = new Book();
        book.setId(1L);

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        bookService.delete(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException() {

        when(bookRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.delete(1L)
        );
    }

    @Test
    void searchByAuthorJPQL_shouldReturnBooks() {

        Book book = new Book();
        book.setId(1L);
        book.setTitle("1984");

        when(bookRepository.findByAuthorNameJPQL(
                eq("Orwell"),
                any()
        )).thenReturn(new PageImpl<>(List.of(book)));

        List<BookDTOFields> result =
                bookService.searchByAuthorJPQL(
                        "Orwell",
                        PageRequest.of(0, 10)
                );

        assertEquals(1, result.size());

        verify(bookRepository).findByAuthorNameJPQL(
                eq("Orwell"),
                any()
        );
    }

    @Test
    void searchByAuthorJPQL_shouldUseCache() {

        Book book = new Book();
        book.setId(1L);
        book.setTitle("1984");

        when(bookRepository.findByAuthorNameJPQL(
                eq("Orwell"),
                any()
        )).thenReturn(new PageImpl<>(List.of(book)));

        bookService.searchByAuthorJPQL(
                "Orwell",
                PageRequest.of(0, 10)
        );

        bookService.searchByAuthorJPQL(
                "Orwell",
                PageRequest.of(0, 10)
        );

        verify(bookRepository, times(1))
                .findByAuthorNameJPQL(
                        eq("Orwell"),
                        any()
                );
    }

    @Test
    void searchByAuthorNative_shouldReturnBooks() {

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Native SQL Book");

        when(bookRepository.findByAuthorNameNative(
                eq("Orwell"),
                any()
        )).thenReturn(new PageImpl<>(List.of(book)));

        List<BookDTOFields> result =
                bookService.searchByAuthorNative(
                        "Orwell",
                        PageRequest.of(0, 10)
                );

        assertEquals(1, result.size());

        verify(bookRepository).findByAuthorNameNative(
                eq("Orwell"),
                any()
        );
    }

    @Test
    void searchByAuthorNative_shouldUseCache() {

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Native SQL Book");

        when(bookRepository.findByAuthorNameNative(
                eq("Orwell"),
                any()
        )).thenReturn(new PageImpl<>(List.of(book)));

        bookService.searchByAuthorNative(
                "Orwell",
                PageRequest.of(0, 10)
        );

        bookService.searchByAuthorNative(
                "Orwell",
                PageRequest.of(0, 10)
        );

        verify(bookRepository, times(1))
                .findByAuthorNameNative(
                        eq("Orwell"),
                        any()
                );
    }
    @Test
    void update_shouldInvalidateCache() {

        Book book = new Book();
        book.setId(1L);
        book.setTitle("1984");

        when(bookRepository.findByAuthorNameJPQL(
                eq("Orwell"),
                any()
        )).thenReturn(new PageImpl<>(List.of(book)));

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(bookRepository.save(any(Book.class)))
                .thenReturn(book);

        bookService.searchByAuthorJPQL(
                "Orwell",
                PageRequest.of(0, 10)
        );

        bookService.searchByAuthorJPQL(
                "Orwell",
                PageRequest.of(0, 10)
        );

        BookDTO dto = new BookDTO();
        dto.setTitle("New title");
        dto.setPublicationYear(2025);

        bookService.update(1L, dto);

        bookService.searchByAuthorJPQL(
                "Orwell",
                PageRequest.of(0, 10)
        );

        verify(bookRepository, times(2))
                .findByAuthorNameJPQL(
                        eq("Orwell"),
                        any()
                );
    }
    @Test
    void uploadBook_shouldUploadBookAndPdf() {

        BookDTOFieldsOwner dto = BookDTOFieldsOwner.builder()
                .title("Test Book")
                .publicationYear(2025)
                .ownerId(1L)
                .authors(List.of(
                        AuthorDTO4BDTOF.builder()
                                .name("Author")
                                .build()
                ))
                .categories(List.of(
                        CategoryDTO4BDTOF.builder()
                                .name("Category")
                                .build()
                ))
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "pdf-content".getBytes()
        );

        Author author = new Author();
        author.setId(1L);
        author.setName("Author");

        when(authorRepository.findByName("Author"))
                .thenReturn(Optional.of(author));

        Category category = new Category();
        category.setId(1L);
        category.setName("Category");

        when(categoryRepository.findByName("Category"))
                .thenReturn(Optional.of(category));

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("Test Book");
        savedBook.setPublicationYear(2025);
        savedBook.setAuthors(List.of(author));
        savedBook.setCategories(List.of(category));

        when(bookRepository.save(any(Book.class)))
                .thenReturn(savedBook);

        BookDTO result = bookService.uploadBook(dto, file);

        assertNotNull(result);

        assertEquals("Test Book", result.getTitle());

        verify(bookRepository).save(any(Book.class));

        verify(bookPDFRepository).save(any(BookPDF.class));
    }

}