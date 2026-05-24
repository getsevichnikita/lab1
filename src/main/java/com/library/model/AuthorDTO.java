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
public class AuthorDTO {
    private Long id;
    @NotBlank(message = "Author name must not be blank")
    @Size(max = 100, message = "Author name is too long")
    private String name;

    private List<Long> bookIds;
}