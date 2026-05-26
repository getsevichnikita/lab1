package com.library.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    @Schema(description = "Loan identifier", example = "1")
    private Long id;
    @Schema(description = "Loan's reader identifier", example = "1")
    private Long readerId;
    @NotNull(message = "BookId must not be null")
    @Schema(description = "Loan's book identifier", example = "1")
    private Long bookId;
    @NotNull(message = "Issue date must not be null")
    @Schema(description = "Loan's issue date", example = "2026-04-07")
    private LocalDate issueDate;
    @NotNull(message = "Return date must not be null")
    @Schema(description = "Loan's return date", example = "2026-05-07")
    private LocalDate returnDate;
}