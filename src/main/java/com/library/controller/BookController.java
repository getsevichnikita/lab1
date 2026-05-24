package com.library.controller;
import com.library.model.BookDTO;
import com.library.model.BookDTOFields;
import com.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    @PostMapping
    public BookDTO create(@Valid @RequestBody BookDTO dto) {
        return bookService.create(dto);
    }

    @GetMapping
    public List<BookDTO> getAll(Pageable pageable) {
        return bookService.getAll(pageable);
    }
    @GetMapping("/{id}")
    public BookDTO getById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @PutMapping("/{id}")
    public BookDTO update(@PathVariable Long id, @Valid @RequestBody BookDTO dto) {
        return bookService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }

    @GetMapping("/search/jpql")
    public List<BookDTOFields> searchByAuthorJPQL(
            @RequestParam String author,
            Pageable pageable
    ) {
        return bookService.searchByAuthorJPQL(author, pageable);
    }

    @GetMapping("/search/native")
    public List<BookDTOFields> searchByAuthorNative(
            @RequestParam String author,
            Pageable pageable
    ) {
        return bookService.searchByAuthorNative(author, pageable);
    }

}