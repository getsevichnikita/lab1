package com.library.model;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO4BDTOF {
    private Long id;
    private String name;

    private List<Long> bookIds;
}
