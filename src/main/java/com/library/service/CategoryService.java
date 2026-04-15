package com.library.service;

import com.library.mapper.CategoryMapper;
import com.library.model.Book;
import com.library.model.Category;
import com.library.model.CategoryDTO;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public List<CategoryDTO> getAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    public CategoryDTO getById(Long id) {
        return CategoryMapper.toDto(
                categoryRepository.findById(id).orElseThrow()
        );
    }

    public CategoryDTO save(CategoryDTO dto) {
        List<Book> books = dto.getBookIds() == null
                ? List.of()
                : bookRepository.findAllById(dto.getBookIds());
        Category category = new Category();
        category.setName(dto.getName());
        category.setBooks(new ArrayList<>());
        for (Book book : books) {
            category.getBooks().add(book);
            book.getCategories().add(category);
        }
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id).orElseThrow();
        category.setName(dto.getName());
        for (Book book : category.getBooks()) {
            book.getCategories().remove(category);
        }

        category.getBooks().clear();

        if (dto.getBookIds() != null) {
            List<Book> books = bookRepository.findAllById(dto.getBookIds());

            for (Book book : books) {
                category.getBooks().add(book);
                book.getCategories().add(category);
            }
        }

        return CategoryMapper.toDto(
                categoryRepository.save(category)
        );
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}