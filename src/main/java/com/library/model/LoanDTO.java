package com.library.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {

    private Long id;
    private Long readerId;
    @NotNull(message = "BookId must not be null")
    private Long bookId;
    @NotNull(message = "Issue date must not be null")
    private LocalDate issueDate;
    @NotNull(message = "Return date must not be null")
    private LocalDate returnDate;
}