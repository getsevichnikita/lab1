package com.library.repository;

import com.library.model.Loan;
import org.springframework.data.jpa.repository.*;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("SELECT l FROM Loan l JOIN FETCH l.reader JOIN FETCH l.book")
    List<Loan> findAllWithJoinFetch();

    @EntityGraph(attributePaths = {"reader", "book"})
    @Query("SELECT l FROM Loan l")
    List<Loan> findAllWithEntityGraph();
}
