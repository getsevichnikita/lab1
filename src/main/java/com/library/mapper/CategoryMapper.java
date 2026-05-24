package com.library.mapper;

import com.library.model.Book;
import com.library.model.Category;
import com.library.model.CategoryDTO;
import com.library.model.CategoryDTO4BDTOF;

import java.util.List;
public class CategoryMapper {
    private CategoryMapper() {}

    public static CategoryDTO toDto(Category category) {
        if (category == null) return null;

        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());

        dto.setBookIds(
                category.getBooks() == null
                        ? List.of()
                        : category.getBooks()
                        .stream()
                        .map(Book::getId)
                        .toList()
        );

        return dto;
    }
    public static CategoryDTO4BDTOF toDto4bdtof(Category category) {
        return new CategoryDTO4BDTOF(
                category.getId(),
                category.getName()
                        );
    }

    public static Category toEntity4bdtof(CategoryDTO4BDTOF dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        return category;
    }

}