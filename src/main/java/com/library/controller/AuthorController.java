package com.library.controller;
import com.library.model.AuthorDTO;
import com.library.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public List<AuthorDTO> getAll() {
        return authorService.getAll();
    }

    @GetMapping("/{id}")
    public AuthorDTO getById(@PathVariable Long id) {
        return authorService.getById(id);
    }

    @PostMapping
    public AuthorDTO create(@RequestBody AuthorDTO dto) {
        return authorService.save(dto);
    }

    @PutMapping("/{id}")
    public AuthorDTO update(@PathVariable Long id, @RequestBody AuthorDTO dto) {
        return authorService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        authorService.delete(id);
    }
}