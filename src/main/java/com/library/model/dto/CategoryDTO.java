package com.library.model.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CategoryDTO {
    @Schema(description = "Category identifier", example = "1")
    private Long id;
    @NotBlank(message = "Category name must not be blank")
    @Size(max = 100)
    @Schema(description = "Category name", example = "Novel")
    private String name;

    @Schema(description = "List of book ids", example = "[1,2]")
    private List<Long> bookIds;
}