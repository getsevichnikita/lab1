package com.library.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReaderDTONplus1 {
    @Schema(description = "Reader identifier", example = "1")
    private Long id;
    @Schema(description = "Reader name", example = "1")
    private String name;
    @Schema(description = "List of loans")
    private List<LoanDTOFields> loans;
}
