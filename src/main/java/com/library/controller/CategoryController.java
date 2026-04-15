package com.library.controller;
import com.library.model.CategoryDTO;
import com.library.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDTO> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    public CategoryDTO getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PostMapping
    public CategoryDTO create(@RequestBody CategoryDTO dto) {
        return categoryService.save(dto);
    }

    @PutMapping("/{id}")
    public CategoryDTO update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        return categoryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}