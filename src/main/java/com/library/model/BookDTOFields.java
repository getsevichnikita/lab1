package com.library.model;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTOFields {

    private Long id;
    private String title;
    private int publicationYear;

    private List<AuthorDTO4BDTOF> authors;
    private List<CategoryDTO4BDTOF> categories;
}
