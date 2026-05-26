package com.library.model.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDTO4BDTOF {
    @Schema(description = "Author identifier", example = "1")
    private Long id;

    @Schema(description = "Author name", example = "T. H. White")
    private String name;

}