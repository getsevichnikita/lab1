package com.library.service;
import com.library.exception.InvalidLoanDatesException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.LoanDTO;
import com.library.mapper.LoanMapper;
import com.library.model.Reader;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final ReaderRepository readerRepository;
    private final BookRepository bookRepository;

    public List<LoanDTO> getAll(Pageable pageable) {
        return loanRepository.findAll(pageable)
                .stream()
                .map(LoanMapper::toDto)
                .toList();
    }

    public LoanDTO getById(Long id) {
        return LoanMapper.toDto(
                loanRepository.findById(id) .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Loan not found with id = " + id
                        )
                )
        );
    }

    public LoanDTO create(LoanDTO dto) {

        if (dto.getReturnDate().isBefore(dto.getIssueDate())) {
            throw new InvalidLoanDatesException(
                    "Return date cannot be before issue date"
            );
        }
        Reader reader = readerRepository.findById(dto.getReaderId()).orElseThrow();
        Book book = bookRepository.findById(dto.getBookId()).orElseThrow();

        Loan loan = LoanMapper.toEntity(dto, reader, book);
        log.info("Loan created with id={}", dto.getId());
        return LoanMapper.toDto(loanRepository.save(loan));
    }

    public LoanDTO update(Long id, LoanDTO dto) {
        if (dto.getReturnDate().isBefore(dto.getIssueDate())) {
            throw new InvalidLoanDatesException(
                    "Return date cannot be before issue date"
            );
        }
        Loan loan = loanRepository.findById(id) .orElseThrow(() ->
                new ResourceNotFoundException(
                        "Loan not found with id = " + id
                )
        );

        Reader reader = readerRepository.findById(dto.getReaderId()).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Reader not found with id = " + id
                )
        );
        Book book = bookRepository.findById(dto.getBookId()).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Book not found with id = " + id
                )
        );

        loan.setReader(reader);
        loan.setBook(book);
        loan.setIssueDate(dto.getIssueDate());
        loan.setReturnDate(dto.getReturnDate());
        log.info("Loan updated with id={}", dto.getId());
        return LoanMapper.toDto(loanRepository.save(loan));
    }

    public void delete(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Loan not found with id = " + id
                        )
                );

        loanRepository.delete(loan);

        log.info("Loan deleted with id={}", id);
    }
}

