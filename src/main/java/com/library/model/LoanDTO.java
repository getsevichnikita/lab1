package com.library.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {

    private Long id;
    private Long readerId;
    private Long bookId;
    private LocalDate issueDate;
    private LocalDate returnDate;
}