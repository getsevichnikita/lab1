package com.library.service;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.LoanDTO;
import com.library.mapper.LoanMapper;
import com.library.model.Reader;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.ReaderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final ReaderRepository readerRepository;
    private final BookRepository repository;
    public LoanDTO getById(Long id) {
        return LoanMapper.toDto(
                loanRepository.findById(id).orElseThrow()
        );
    }

    public LoanDTO create(Loan loan) {
        return LoanMapper.toDto(
                loanRepository.save(loan)
        );
    }

    public LoanDTO update(Long id, Loan updated) {
        Loan loan = loanRepository.findById(id).orElseThrow();

        loan.setReader(updated.getReader());
        loan.setBook(updated.getBook());
        loan.setIssueDate(updated.getIssueDate());
        loan.setReturnDate(updated.getReturnDate());

        return LoanMapper.toDto(
                loanRepository.save(loan)
        );
    }

    public void delete(Long id) {
        loanRepository.deleteById(id);
    }

    public List<LoanDTO> getAllNPlusOne() {
        return loanRepository.findAll().stream()
                .map(LoanMapper::toDto)
                .toList();
    }

    public List<LoanDTO> getAllJoinFetch() {
        return loanRepository.findAllWithJoinFetch().stream()
                .map(LoanMapper::toDto)
                .toList();
    }
    public List<LoanDTO> getAllEntityGraph() {
        return loanRepository.findAllWithEntityGraph().stream()
                .map(LoanMapper::toDto)
                .toList();
    }
    public void createLoanWithoutTransaction() {
        Reader reader = new Reader();
        reader.setName("Test Reader");
        reader = readerRepository.save(reader);

        Book book = repository.findById(1L)
                .orElseThrow();

        Loan loan = new Loan();
        loan.setReader(reader);
        loan.setBook(book);

        loanRepository.save(loan);

        if (true) {
            throw new RuntimeException("Ошибка после сохранения!");
        }
    }

    @Transactional
    public void createLoanWithTransaction() {

        Reader reader = new Reader();
        reader.setName("Transactional Reader");
        reader = readerRepository.save(reader);

        Book book = repository.findById(1L)
                .orElseThrow();

        Loan loan = new Loan();
        loan.setReader(reader);
        loan.setBook(book);

        loanRepository.save(loan);

        if (true) {
            throw new RuntimeException("Ошибка внутри транзакции!");
        }
    }
}

