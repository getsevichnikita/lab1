package com.library.mapper;

import com.library.model.Book;
import com.library.model.LoanDTO;
import com.library.model.Loan;
import com.library.model.Reader;

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