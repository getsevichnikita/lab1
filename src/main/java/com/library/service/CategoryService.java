package com.library.service;

import com.library.exception.ResourceNotFoundException;
import com.library.mapper.CategoryMapper;
import com.library.model.entity.Book;
import com.library.model.entity.Category;
import com.library.model.dto.CategoryDTO;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private static final String LOG_CATEGORY_NOT_FOUND = "Category not found with id = ";
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public List<CategoryDTO> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    public CategoryDTO getById(Long id) {
        return CategoryMapper.toDto(
                categoryRepository.findById(id) .orElseThrow(() ->
                        new ResourceNotFoundException(
                                LOG_CATEGORY_NOT_FOUND + id
                        )
                )
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
        log.info("Category created with id={}", dto.getId());
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id) .orElseThrow(() ->
                new ResourceNotFoundException(
                        LOG_CATEGORY_NOT_FOUND + id
                )
        );
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
        log.info("Category updated with id={}", dto.getId());
        return CategoryMapper.toDto(
                categoryRepository.save(category)
        );
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                LOG_CATEGORY_NOT_FOUND + id
                        )
                );
            for (Book book : category.getBooks()) {
                book.getCategories().remove(category);
            }

        category.getBooks().clear();
        log.info("Category deleted with id={}", id);
        categoryRepository.delete(category);

    }
}