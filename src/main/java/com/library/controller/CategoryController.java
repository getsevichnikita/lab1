package com.library.controller;

import com.library.model.CategoryDTO;
import com.library.mapper.CategoryMapper;
import com.library.model.Book;
import com.library.service.BookService;
import com.library.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final BookService bookService;

    @GetMapping
    public List<CategoryDTO> getAll() {
        return categoryService.getAll().stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public CategoryDTO getById(@PathVariable Long id) {
        return CategoryMapper.toDto(categoryService.getById(id));
    }

    @PostMapping
    public CategoryDTO create(@RequestBody CategoryDTO dto) {
        List<Book> books = bookService.getAllByIds(dto.getBookIds());
        return CategoryMapper.toDto(categoryService.save(
                CategoryMapper.toEntity(dto, books)
        ));
    }

    @PutMapping("/{id}")
    public CategoryDTO update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        List<Book> books = bookService.getAllByIds(dto.getBookIds());
        return CategoryMapper.toDto(categoryService.update(
                id,
                CategoryMapper.toEntity(dto, books)
        ));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}