package com.library.service;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.LoanDTO;
import com.library.mapper.LoanMapper;
import com.library.model.Reader;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final ReaderRepository readerRepository;
    private final BookRepository bookRepository;

    public List<LoanDTO> getAll() {
        return loanRepository.findAll().stream()
                .map(LoanMapper::toDto)
                .toList();
    }

    public LoanDTO getById(Long id) {
        return LoanMapper.toDto(
                loanRepository.findById(id).orElseThrow()
        );
    }

    public LoanDTO create(LoanDTO dto) {

        Reader reader = readerRepository.findById(dto.getReaderId()).orElseThrow();
        Book book = bookRepository.findById(dto.getBookId()).orElseThrow();

        Loan loan = LoanMapper.toEntity(dto, reader, book);

        return LoanMapper.toDto(loanRepository.save(loan));
    }

    public LoanDTO update(Long id, LoanDTO dto) {

        Loan loan = loanRepository.findById(id).orElseThrow();

        Reader reader = readerRepository.findById(dto.getReaderId()).orElseThrow();
        Book book = bookRepository.findById(dto.getBookId()).orElseThrow();

        loan.setReader(reader);
        loan.setBook(book);
        loan.setIssueDate(dto.getIssueDate());
        loan.setReturnDate(dto.getReturnDate());

        return LoanMapper.toDto(loanRepository.save(loan));
    }

    public void delete(Long id) {
        loanRepository.deleteById(id);
    }
}

