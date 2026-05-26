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
public class AuthorDTO {
    @Schema(description = "Author identifier", example = "1")
    private Long id;

    @NotBlank(message = "Author name must not be blank")
    @Size(max = 100, message = "Author name is too long")
    @Schema(description = "Author name", example = "T. H. White")
    private String name;

    @Schema(description = "List of book ids", example = "[1,2]")
    private List<Long> bookIds;
}