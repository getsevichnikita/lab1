package com.library.model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO4BDTOF {
    @Schema(description = "Category identifier", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Novel")
    private String name;
}
