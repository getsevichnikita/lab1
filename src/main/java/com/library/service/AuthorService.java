package com.library.service;

import com.library.mapper.AuthorMapper;
import com.library.model.Author;
import com.library.model.AuthorDTO;
import com.library.model.Book;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public List<AuthorDTO> getAll() {
        return authorRepository.findAll().stream()
                .map(AuthorMapper::toDto)
                .toList();
    }

    public AuthorDTO getById(Long id) {
        return AuthorMapper.toDto(
                authorRepository.findById(id).orElseThrow()
        );
    }

    public AuthorDTO save(AuthorDTO dto) {
        List<Book> books = dto.getBookIds() == null
                ? List.of()
                : bookRepository.findAllById(dto.getBookIds());
        Author author = new Author();
        author.setName(dto.getName());
        author.setBooks(new ArrayList<>());
        for (Book book : books) {
            author.getBooks().add(book);
            book.getAuthors().add(author);
        }
        return AuthorMapper.toDto(authorRepository.save(author));
    }

    public AuthorDTO update(Long id, AuthorDTO dto) {
        Author author = authorRepository.findById(id).orElseThrow();
        author.setName(dto.getName());
        for (Book book : author.getBooks()) {
            book.getAuthors().remove(author);
        }
        author.getBooks().clear();
        if (dto.getBookIds() != null) {
            List<Book> books = bookRepository.findAllById(dto.getBookIds());
            for (Book book : books) {
                author.getBooks().add(book);
                book.getAuthors().add(author);
            }
        }
        return AuthorMapper.toDto(authorRepository.save(author));
    }
    public void delete(Long id) {
        authorRepository.deleteById(id);
    }
}