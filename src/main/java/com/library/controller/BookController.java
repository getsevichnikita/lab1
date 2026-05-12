package com.library.controller;
import com.library.model.BookDTO;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    @PostMapping
    public BookDTO create(@RequestBody BookDTO dto) {
        return bookService.create(dto);
    }
    @GetMapping
    public List<BookDTO> getAll() {
        return bookService.getAll();
    }
    @GetMapping("/{id}")
    public BookDTO getById(@PathVariable Long id) {
        return bookService.getById(id);
    }
    @PutMapping("/{id}")
    public BookDTO update(@PathVariable Long id, @RequestBody BookDTO dto) {
        return bookService.update(id, dto);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }
    @GetMapping("/search/jpql")
    public Page<BookDTO> searchByAuthorJPQL(
            @RequestParam String author,
            Pageable pageable
    ) {
        return bookService.searchByAuthorJPQL(author, pageable);
    }
    @GetMapping("/search/native")
    public Page<BookDTO> searchByAuthorNative(
            @RequestParam String author,
            Pageable pageable
    ) {
        return bookService.searchByAuthorNative(author, pageable);
    }
}