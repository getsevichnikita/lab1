package com.library.controller;

import com.library.model.AuthorDTO;
import com.library.mapper.AuthorMapper;
import com.library.model.Book;
import com.library.service.AuthorService;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;
    private final BookService bookService;

    @GetMapping
    public List<AuthorDTO> getAll() {
        return authorService.getAll().stream()
                .map(AuthorMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public AuthorDTO getById(@PathVariable Long id) {
        return AuthorMapper.toDto(authorService.getById(id));
    }

    @PostMapping
    public AuthorDTO create(@RequestBody AuthorDTO dto) {
        List<Book> books = bookService.getAllByIds(dto.getBookIds());
        return AuthorMapper.toDto(authorService.save(
                AuthorMapper.toEntity(dto, books)
        ));
    }

    @PutMapping("/{id}")
    public AuthorDTO update(@PathVariable Long id, @RequestBody AuthorDTO dto) {
        List<Book> books = bookService.getAllByIds(dto.getBookIds());
        return AuthorMapper.toDto(authorService.update(
                id,
                AuthorMapper.toEntity(dto, books)
        ));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        authorService.delete(id);
    }
}