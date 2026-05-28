package com.library.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data transfer object for book (IDs)")
public class BookDTOFields {
    @Schema(description = "Book identifier", example = "1")
    private Long id;

    @NotBlank(message = "Book title must not be blank")
    @Size(max = 255, message = "Title is too long")
    @Schema(description = "Book title", example = "War and Peace")
    private String title;

    @NotBlank(message = "Book title must not be blank")
    @Min(value = 0, message = "This number can't be a year of AD era")
    @Max(value = 2027, message = "This year is not achieved yet")
    @Schema(description = "Publication year", example = "1869")
    private int publicationYear;

    @NotBlank(message = "Book title must not be blank")
    @Schema(description = "List of authors")
    private List<AuthorDTO4BDTOF> authors;

    @NotBlank(message = "Book title must not be blank")
    private List<CategoryDTO4BDTOF> categories;
}
