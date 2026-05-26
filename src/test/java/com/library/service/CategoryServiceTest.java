package com.library.service;

import com.library.exception.ResourceNotFoundException;
import com.library.model.Book;
import com.library.model.Category;
import com.library.model.CategoryDTO;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAll_shouldReturnCategories() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Fantasy");

        Page<Category> page = new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        List<CategoryDTO> result =
                categoryService.getAll(PageRequest.of(0, 10));

        assertEquals(1, result.size());
        assertEquals("Fantasy", result.getFirst().getName());

        verify(categoryRepository)
                .findAll(any(Pageable.class));
    }

    @Test
    void getById_shouldReturnCategory() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Fantasy");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        CategoryDTO result = categoryService.getById(1L);

        assertEquals("Fantasy", result.getName());

        verify(categoryRepository).findById(1L);
    }

    @Test
    void getById_shouldThrowResourceNotFoundException() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getById(1L)
        );

        verify(categoryRepository).findById(1L);
    }

    @Test
    void save_shouldCreateCategoryWithoutBooks() {

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Fantasy");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Fantasy");

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(savedCategory);

        CategoryDTO result = categoryService.save(dto);

        assertEquals("Fantasy", result.getName());

        verify(categoryRepository)
                .save(any(Category.class));
    }

    @Test
    void save_shouldCreateCategoryWithBooks() {

        Book book = new Book();
        book.setId(1L);
        book.setCategories(new java.util.ArrayList<>());

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Fantasy");
        dto.setBookIds(List.of(1L));

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Fantasy");

        when(bookRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(book));

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(savedCategory);

        CategoryDTO result = categoryService.save(dto);

        assertEquals("Fantasy", result.getName());

        verify(bookRepository).findAllById(List.of(1L));
        verify(categoryRepository)
                .save(any(Category.class));
    }

    @Test
    void update_shouldUpdateCategory() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Old");
        category.setBooks(new java.util.ArrayList<>());

        CategoryDTO dto = new CategoryDTO();
        dto.setName("New");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);

        CategoryDTO result =
                categoryService.update(1L, dto);

        assertEquals("New", result.getName());

        verify(categoryRepository).findById(1L);
        verify(categoryRepository)
                .save(any(Category.class));
    }

    @Test
    void update_shouldUpdateCategoryWithBooks() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Old");
        category.setBooks(new java.util.ArrayList<>());

        Book book = new Book();
        book.setId(1L);
        book.setCategories(new java.util.ArrayList<>());

        CategoryDTO dto = new CategoryDTO();
        dto.setName("New");
        dto.setBookIds(List.of(1L));

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(bookRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(book));

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);

        CategoryDTO result =
                categoryService.update(1L, dto);

        assertEquals("New", result.getName());

        verify(bookRepository).findAllById(List.of(1L));
        verify(categoryRepository)
                .save(any(Category.class));
    }

    @Test
    void update_shouldThrowResourceNotFoundException() {

        CategoryDTO dto = new CategoryDTO();
        dto.setName("New");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.update(1L, dto)
        );

        verify(categoryRepository).findById(1L);
    }

    @Test
    void delete_shouldDeleteCategory() {

        Category category = new Category();
        category.setId(1L);
        category.setBooks(new java.util.ArrayList<>());

        Book book = new Book();
        book.setId(1L);
        book.setCategories(new java.util.ArrayList<>());

        category.getBooks().add(book);
        book.getCategories().add(category);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.delete(1L);

        assertTrue(category.getBooks().isEmpty());

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).delete(category);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.delete(1L)
        );

        verify(categoryRepository).findById(1L);
    }
}