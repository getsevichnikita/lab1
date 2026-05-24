package com.library.model;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {

    private Long id;

    @NotBlank(message = "Book title must not be blank")
    @Size(max = 255, message = "Title is too long")
    private String title;

    @Min(value = 0, message = "This number can't be a year of AD era")
    @Max(value = 2027, message = "This year is not achieved yet")
    private int publicationYear;

    private List<Long> authorIds;
    private List<Long> categoryIds;
}