package com.library.controller;

import com.library.model.Reader;
import com.library.service.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;

    @GetMapping
    public List<Reader> getAll() {
        return readerService.getAll();
    }

    @GetMapping("/{id}")
    public Reader getById(@PathVariable Long id) {
        return readerService.getById(id);
    }

    @PostMapping
    public Reader create(@RequestBody Reader reader) {
        return readerService.save(reader);
    }

    @PutMapping("/{id}")
    public Reader update(@PathVariable Long id, @RequestBody Reader reader) {
        return readerService.update(id, reader);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        readerService.delete(id);
    }
}