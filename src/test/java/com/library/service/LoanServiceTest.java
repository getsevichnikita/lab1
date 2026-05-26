package com.library.service;

import com.library.exception.InvalidLoanDatesException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.LoanDTO;
import com.library.model.Reader;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.ReaderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private LoanService loanService;

    @Test
    void getAll_shouldReturnLoans() {

        Loan loan = new Loan();
        loan.setId(1L);

        Page<Loan> page = new PageImpl<>(List.of(loan));

        when(loanRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        List<LoanDTO> result =
                loanService.getAll(PageRequest.of(0, 10));

        assertEquals(1, result.size());

        verify(loanRepository)
                .findAll(any(Pageable.class));
    }

    @Test
    void getById_shouldReturnLoan() {

        Loan loan = new Loan();
        loan.setId(1L);

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        LoanDTO result = loanService.getById(1L);

        assertEquals(1L, result.getId());

        verify(loanRepository).findById(1L);
    }

    @Test
    void getById_shouldThrowResourceNotFoundException() {

        when(loanRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> loanService.getById(1L)
        );

        verify(loanRepository).findById(1L);
    }

    @Test
    void create_shouldCreateLoan() {

        Reader reader = new Reader();
        reader.setId(1L);

        Book book = new Book();
        book.setId(1L);

        Loan loan = new Loan();
        loan.setId(1L);

        LoanDTO dto = new LoanDTO();
        dto.setReaderId(1L);
        dto.setBookId(1L);
        dto.setIssueDate(LocalDate.now());
        dto.setReturnDate(LocalDate.now().plusDays(7));

        when(readerRepository.findById(1L))
                .thenReturn(Optional.of(reader));

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(loanRepository.save(any(Loan.class)))
                .thenReturn(loan);

        LoanDTO result = loanService.create(dto);

        assertEquals(1L, result.getId());

        verify(readerRepository).findById(1L);
        verify(bookRepository).findById(1L);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void create_shouldThrowInvalidLoanDatesException() {

        LoanDTO dto = new LoanDTO();
        dto.setIssueDate(LocalDate.now());
        dto.setReturnDate(LocalDate.now().minusDays(1));

        assertThrows(
                InvalidLoanDatesException.class,
                () -> loanService.create(dto)
        );

        verifyNoInteractions(readerRepository);
        verifyNoInteractions(bookRepository);
    }

    @Test
    void create_shouldThrowReaderNotFoundException() {

        LoanDTO dto = new LoanDTO();
        dto.setReaderId(1L);
        dto.setBookId(1L);
        dto.setIssueDate(LocalDate.now());
        dto.setReturnDate(LocalDate.now().plusDays(7));

        when(readerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> loanService.create(dto)
        );

        verify(readerRepository).findById(1L);
    }

    @Test
    void create_shouldThrowBookNotFoundException() {

        Reader reader = new Reader();
        reader.setId(1L);

        LoanDTO dto = new LoanDTO();
        dto.setReaderId(1L);
        dto.setBookId(1L);
        dto.setIssueDate(LocalDate.now());
        dto.setReturnDate(LocalDate.now().plusDays(7));

        when(readerRepository.findById(1L))
                .thenReturn(Optional.of(reader));

        when(bookRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> loanService.create(dto)
        );

        verify(bookRepository).findById(1L);
    }

    @Test
    void update_shouldUpdateLoan() {

        Loan loan = new Loan();
        loan.setId(1L);

        Reader reader = new Reader();
        reader.setId(1L);

        Book book = new Book();
        book.setId(1L);

        LoanDTO dto = new LoanDTO();
        dto.setReaderId(1L);
        dto.setBookId(1L);
        dto.setIssueDate(LocalDate.now());
        dto.setReturnDate(LocalDate.now().plusDays(7));

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        when(readerRepository.findById(1L))
                .thenReturn(Optional.of(reader));

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(loanRepository.save(any(Loan.class)))
                .thenReturn(loan);

        LoanDTO result =
                loanService.update(1L, dto);

        assertEquals(1L, result.getId());

        verify(loanRepository).findById(1L);
        verify(readerRepository).findById(1L);
        verify(bookRepository).findById(1L);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void update_shouldThrowInvalidLoanDatesException() {

        LoanDTO dto = new LoanDTO();
        dto.setIssueDate(LocalDate.now());
        dto.setReturnDate(LocalDate.now().minusDays(1));

        assertThrows(
                InvalidLoanDatesException.class,
                () -> loanService.update(1L, dto)
        );
    }

    @Test
    void update_shouldThrowLoanNotFoundException() {

        LoanDTO dto = new LoanDTO();
        dto.setIssueDate(LocalDate.now());
        dto.setReturnDate(LocalDate.now().plusDays(7));

        when(loanRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> loanService.update(1L, dto)
        );

        verify(loanRepository).findById(1L);
    }

    @Test
    void update_shouldThrowReaderNotFoundException() {

        Loan loan = new Loan();
        loan.setId(1L);

        LoanDTO dto = new LoanDTO();
        dto.setReaderId(1L);
        dto.setBookId(1L);
        dto.setIssueDate(LocalDate.now());
        dto.setReturnDate(LocalDate.now().plusDays(7));

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        when(readerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> loanService.update(1L, dto)
        );
    }

    @Test
    void update_shouldThrowBookNotFoundException() {

        Loan loan = new Loan();
        loan.setId(1L);

        Reader reader = new Reader();
        reader.setId(1L);

        LoanDTO dto = new LoanDTO();
        dto.setReaderId(1L);
        dto.setBookId(1L);
        dto.setIssueDate(LocalDate.now());
        dto.setReturnDate(LocalDate.now().plusDays(7));

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        when(readerRepository.findById(1L))
                .thenReturn(Optional.of(reader));

        when(bookRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> loanService.update(1L, dto)
        );
    }

    @Test
    void delete_shouldDeleteLoan() {

        Loan loan = new Loan();
        loan.setId(1L);

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        loanService.delete(1L);

        verify(loanRepository).findById(1L);
        verify(loanRepository).delete(loan);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException() {

        when(loanRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> loanService.delete(1L)
        );

        verify(loanRepository).findById(1L);
    }

    @Test
    void createBulkNoTransaction_shouldCreateAllLoans() {

        Reader reader = new Reader();
        reader.setId(1L);

        Book book = new Book();
        book.setId(1L);

        Loan loan = new Loan();
        loan.setId(1L);

        LoanDTO dto1 = new LoanDTO();
        dto1.setReaderId(1L);
        dto1.setBookId(1L);
        dto1.setIssueDate(LocalDate.now());
        dto1.setReturnDate(LocalDate.now().plusDays(7));

        LoanDTO dto2 = new LoanDTO();
        dto2.setReaderId(1L);
        dto2.setBookId(1L);
        dto2.setIssueDate(LocalDate.now());
        dto2.setReturnDate(LocalDate.now().plusDays(10));

        when(readerRepository.findById(1L))
                .thenReturn(Optional.of(reader));

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(loanRepository.save(any(Loan.class)))
                .thenReturn(loan);

        List<LoanDTO> result =
                loanService.createBulkNoTransaction(
                        List.of(dto1, dto2)
                );

        assertEquals(2, result.size());

        verify(loanRepository, times(2))
                .save(any(Loan.class));
    }

    @Test
    void createBulkTransaction_shouldCreateAllLoans() {

        Reader reader = new Reader();
        reader.setId(1L);

        Book book = new Book();
        book.setId(1L);

        Loan loan = new Loan();
        loan.setId(1L);

        LoanDTO dto = new LoanDTO();
        dto.setReaderId(1L);
        dto.setBookId(1L);
        dto.setIssueDate(LocalDate.now());
        dto.setReturnDate(LocalDate.now().plusDays(7));

        when(readerRepository.findById(1L))
                .thenReturn(Optional.of(reader));

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(loanRepository.save(any(Loan.class)))
                .thenReturn(loan);

        List<LoanDTO> result =
                loanService.createBulkTransaction(
                        List.of(dto)
                );

        assertEquals(1, result.size());

        verify(loanRepository).save(any(Loan.class));
    }
}