package com.library.service;

import com.library.exception.ResourceNotFoundException;
import com.library.model.entity.Author;
import com.library.model.dto.AuthorDTO;
import com.library.model.entity.Book;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void getAll_shouldReturnAuthors() {

        Author author = new Author();
        author.setId(1L);
        author.setName("George Orwell");

        when(authorRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(author)));

        List<AuthorDTO> result =
                authorService.getAll(PageRequest.of(0, 10));

        assertEquals(1, result.size());
        assertEquals(
                "George Orwell",
                result.getFirst().getName()
        );
    }

    @Test
    void getById_shouldReturnAuthor() {

        Author author = new Author();
        author.setId(1L);
        author.setName("George Orwell");

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        AuthorDTO result = authorService.getById(1L);

        assertEquals("George Orwell", result.getName());

        verify(authorRepository).findById(1L);
    }

    @Test
    void getById_shouldThrowResourceNotFoundException() {

        when(authorRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> authorService.getById(1L)
                );

        assertEquals(
                "Author not found with id = 1",
                ex.getMessage()
        );
    }

    @Test
    void save_shouldSaveAuthor() {

        Book book = new Book();
        book.setId(1L);
        book.setAuthors(new ArrayList<>());

        Author savedAuthor = new Author();
        savedAuthor.setId(1L);
        savedAuthor.setName("George Orwell");

        AuthorDTO dto = new AuthorDTO();
        dto.setName("George Orwell");
        dto.setBookIds(List.of(1L));

        when(bookRepository.findAllById(dto.getBookIds()))
                .thenReturn(List.of(book));

        when(authorRepository.save(any(Author.class)))
                .thenReturn(savedAuthor);

        AuthorDTO result = authorService.save(dto);

        assertNotNull(result);
        assertEquals("George Orwell", result.getName());

        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void update_shouldUpdateAuthor() {

        Author author = new Author();
        author.setId(1L);
        author.setName("Old name");
        author.setBooks(new ArrayList<>());

        Book book = new Book();
        book.setId(1L);
        book.setAuthors(new ArrayList<>());

        AuthorDTO dto = new AuthorDTO();
        dto.setName("New name");
        dto.setBookIds(List.of(1L));

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        when(bookRepository.findAllById(dto.getBookIds()))
                .thenReturn(List.of(book));

        when(authorRepository.save(any(Author.class)))
                .thenReturn(author);

        AuthorDTO result = authorService.update(1L, dto);

        assertEquals("New name", result.getName());

        verify(authorRepository).save(author);
    }

    @Test
    void update_shouldThrowResourceNotFoundException() {

        AuthorDTO dto = new AuthorDTO();

        when(authorRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.update(1L, dto)
        );
    }

    @Test
    void delete_shouldDeleteAuthor() {

        Author author = new Author();
        author.setId(1L);
        author.setBooks(new ArrayList<>());

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        authorService.delete(1L);

        verify(authorRepository).delete(author);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException() {

        when(authorRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> authorService.delete(1L)
        );
    }
}