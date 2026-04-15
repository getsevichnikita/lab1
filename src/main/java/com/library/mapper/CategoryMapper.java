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
                category.getBooks() == null
                        ? List.of()
                        : category.getBooks()
                        .stream()
                        .map(Book::getId)
                        .toList()
        );

        return dto;
    }

}