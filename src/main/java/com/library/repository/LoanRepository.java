package com.library.repository;

import com.library.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    @Query("select l from Loan l where l.reader.id = :readerId")
    Page<Loan> findByReaderId(@Param("readerId") Long readerId, Pageable pageable);

    boolean existsByBookIdAndReturnDateAfter(
            Long bookId,
            LocalDate date
    );
}
