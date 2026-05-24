package com.library.model;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CategoryDTO {
    private Long id;
    @NotBlank(message = "Category name must not be blank")
    @Size(max = 100)
    private String name;

    private List<Long> bookIds;
}