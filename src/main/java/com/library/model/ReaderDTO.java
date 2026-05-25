package com.library.model;

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
public class ReaderDTO {
    @Schema(description = "Reader identifier", example = "1")
    private Long id;
    @NotBlank(message = "Reader name must not be blank")
    @Size(max = 100)
    @Schema(description = "Reader name", example = "1")
    private String name;
    @Schema(description = "List of loan ids", example = "[1,2]")
    private List<Long> loanIds;
}
