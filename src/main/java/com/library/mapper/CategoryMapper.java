package com.library.mapper;

import com.library.model.Book;
import com.library.model.Category;
import com.library.model.CategoryDTO;

import java.util.List;

public class CategoryMapper {
    private CategoryMapper() {}
    public static CategoryDTO toDto(Category category) {
        if (category == null) return null;

        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());

        dto.setBookIds(
                category.getBooks()
                        .stream()
                        .map(Book::getId)
                        .toList()
        );

        return dto;
    }

    public static Category toEntity(CategoryDTO dto, List<Book> books) {
        if (dto == null) return null;

        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setBooks(books);

        return category;
    }
}