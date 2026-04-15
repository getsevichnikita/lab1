package com.library.model;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {

    private Long id;
    private String title;
    private int publicationYear;

    private List<Long> authorIds;
    private List<Long> categoryIds;
}