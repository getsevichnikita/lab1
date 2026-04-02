package com.library.mapper;

import com.library.model.LoanDTO;
import com.library.model.Loan;

public class LoanMapper {

    private LoanMapper() {}

    public static LoanDTO toDto(Loan loan) {
        return new LoanDTO(
                loan.getId(),
                loan.getReader().getName(),
                loan.getBook().getTitle(),
                loan.getIssueDate(),
                loan.getReturnDate()
        );
    }
}