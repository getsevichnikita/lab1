package com.library.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data transfer object for book (IDs)")
public class BookDTO {
    @Schema(description = "Book identifier", example = "1")
    private Long id;

    @NotBlank(message = "Book title must not be blank")
    @Size(max = 255, message = "Title is too long")
    @Schema(description = "Book title", example = "War and Peace")
    private String title;

    @NotNull(message = "Book publication year must not be blank")
    @Min(value = 0, message = "This number can't be a year of AD era")
    @Max(value = 2027, message = "This year is not achieved yet")
    @Schema(description = "Publication year", example = "1869")
    private int publicationYear;

    @Schema(description = "List of author ids", example = "[1,2]")
    private List<Long> authorIds;

    @Schema(description = "List of category ids", example = "[3,4]")
    private List<Long> categoryIds;
}