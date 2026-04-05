package com.library.controller;

import com.library.model.Book;
import com.library.model.BookDTO;
import com.library.mapper.BookMapper;
import com.library.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public BookDTO getBookById(@PathVariable Long id) {
        return BookMapper.toDto(service.getBookById(id));
    }

    @GetMapping
    public List<BookDTO> getAllBooks() {
        return service.getAllBooks().stream()
                .map(BookMapper::toDto)
                .toList();
    }

    @PostMapping
    public BookDTO createBook(@RequestBody BookDTO dto) {
        return BookMapper.toDto(service.createBook(dto));
    }

    @PutMapping("/{id}")
    public BookDTO updateBook(@PathVariable Long id, @RequestBody BookDTO dto) {
        return BookMapper.toDto(service.updateBook(id, dto));
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        service.deleteBook(id);
    }
}

