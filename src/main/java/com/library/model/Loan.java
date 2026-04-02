package com.library.model;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Reader reader;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    private LocalDate issueDate;
    private LocalDate returnDate;
}