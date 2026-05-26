package com.library.repository;

import com.library.model.entity.Loan;
import org.springframework.data.jpa.repository.*;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
