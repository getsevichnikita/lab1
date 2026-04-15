package com.library.mapper;

import com.library.model.Book;
import com.library.model.LoanDTO;
import com.library.model.Loan;
import com.library.model.Reader;

public class LoanMapper {

    private LoanMapper() {}

    public static LoanDTO toDto(Loan loan) {
        if (loan == null) return null;

        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());

        dto.setReaderId(
                loan.getReader() != null ? loan.getReader().getId() : null
        );

        dto.setBookId(
                loan.getBook() != null ? loan.getBook().getId() : null
        );

        dto.setIssueDate(loan.getIssueDate());
        dto.setReturnDate(loan.getReturnDate());

        return dto;
    }

    public static Loan toEntity(LoanDTO dto, Reader reader, Book book) {
        if (dto == null) return null;

        Loan loan = new Loan();
        loan.setId(dto.getId());
        loan.setReader(reader);
        loan.setBook(book);
        loan.setIssueDate(dto.getIssueDate());
        loan.setReturnDate(dto.getReturnDate());

        return loan;
    }
}