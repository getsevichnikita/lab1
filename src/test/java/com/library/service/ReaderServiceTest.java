package com.library.service;

import com.library.exception.ResourceNotFoundException;
import com.library.model.Loan;
import com.library.model.Reader;
import com.library.model.ReaderDTO;
import com.library.model.ReaderDTONplus1;
import com.library.repository.LoanRepository;
import com.library.repository.ReaderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReaderServiceTest {

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private ReaderService readerService;

    @Test
    void getAll_shouldReturnReaders() {

        Reader reader = new Reader();
        reader.setId(1L);
        reader.setName("Nikita");

        Page<Reader> page =
                new PageImpl<>(List.of(reader));

        when(readerRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        List<ReaderDTONplus1> result =
                readerService.getAll(PageRequest.of(0, 10));

        assertEquals(1, result.size());
        assertEquals("Nikita", result.getFirst().getName());

        verify(readerRepository)
                .findAll(any(Pageable.class));
    }

    @Test
    void getById_shouldReturnReader() {

        Reader reader = new Reader();
        reader.setId(1L);
        reader.setName("Nikita");

        when(readerRepository.findById(1L))
                .thenReturn(Optional.of(reader));

        ReaderDTO result =
                readerService.getById(1L);

        assertEquals("Nikita", result.getName());

        verify(readerRepository).findById(1L);
    }

    @Test
    void getById_shouldThrowResourceNotFoundException() {

        when(readerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> readerService.getById(1L)
        );

        verify(readerRepository).findById(1L);
    }

    @Test
    void save_shouldCreateReaderWithoutLoans() {

        ReaderDTO dto = new ReaderDTO();
        dto.setName("Nikita");

        Reader reader = new Reader();
        reader.setId(1L);
        reader.setName("Nikita");

        when(readerRepository.save(any(Reader.class)))
                .thenReturn(reader);

        ReaderDTO result =
                readerService.save(dto);

        assertEquals("Nikita", result.getName());

        verify(readerRepository)
                .save(any(Reader.class));
    }

    @Test
    void save_shouldCreateReaderWithLoans() {

        Loan loan = new Loan();
        loan.setId(1L);

        ReaderDTO dto = new ReaderDTO();
        dto.setName("Nikita");
        dto.setLoanIds(List.of(1L));

        Reader reader = new Reader();
        reader.setId(1L);
        reader.setName("Nikita");

        when(loanRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(loan));

        when(readerRepository.save(any(Reader.class)))
                .thenReturn(reader);

        ReaderDTO result =
                readerService.save(dto);

        assertEquals("Nikita", result.getName());

        verify(loanRepository)
                .findAllById(List.of(1L));

        verify(readerRepository)
                .save(any(Reader.class));
    }

    @Test
    void update_shouldUpdateReader() {

        Reader reader = new Reader();
        reader.setId(1L);
        reader.setName("Old");

        ReaderDTO dto = new ReaderDTO();
        dto.setName("New");

        when(readerRepository.findById(1L))
                .thenReturn(Optional.of(reader));

        when(readerRepository.save(any(Reader.class)))
                .thenReturn(reader);

        ReaderDTO result =
                readerService.update(1L, dto);

        assertEquals("New", result.getName());

        verify(readerRepository).findById(1L);

        verify(readerRepository)
                .save(any(Reader.class));
    }

    @Test
    void update_shouldUpdateReaderWithLoans() {

        Reader reader = new Reader();
        reader.setId(1L);
        reader.setName("Old");

        Loan loan = new Loan();
        loan.setId(1L);

        ReaderDTO dto = new ReaderDTO();
        dto.setName("New");
        dto.setLoanIds(List.of(1L));

        when(readerRepository.findById(1L))
                .thenReturn(Optional.of(reader));

        when(loanRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(loan));

        when(readerRepository.save(any(Reader.class)))
                .thenReturn(reader);

        ReaderDTO result =
                readerService.update(1L, dto);

        assertEquals("New", result.getName());

        verify(loanRepository)
                .findAllById(List.of(1L));

        verify(readerRepository)
                .save(any(Reader.class));
    }

    @Test
    void update_shouldThrowResourceNotFoundException() {

        ReaderDTO dto = new ReaderDTO();
        dto.setName("New");

        when(readerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> readerService.update(1L, dto)
        );

        verify(readerRepository).findById(1L);
    }

    @Test
    void delete_shouldDeleteReader() {

        Reader reader = new Reader();
        reader.setId(1L);

        when(readerRepository.findById(1L))
                .thenReturn(Optional.of(reader));

        readerService.delete(1L);

        verify(readerRepository).findById(1L);
        verify(readerRepository).delete(reader);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException() {

        when(readerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> readerService.delete(1L)
        );

        verify(readerRepository).findById(1L);
    }

    @Test
    void getAllEntityGraph_shouldReturnReaders() {

        Reader reader = new Reader();
        reader.setId(1L);
        reader.setName("Nikita");

        when(readerRepository.findAllWithLoans(
                any(Pageable.class)
        )).thenReturn(List.of(reader));

        List<ReaderDTONplus1> result =
                readerService.getAllEntityGraph(
                        PageRequest.of(0, 10)
                );

        assertEquals(1, result.size());
        assertEquals("Nikita", result.getFirst().getName());

        verify(readerRepository)
                .findAllWithLoans(any(Pageable.class));
    }

    @Test
    void assignLoansNoTransaction_shouldAssignLoans() {

        Reader reader = new Reader();
        reader.setId(1L);

        Loan loan1 = new Loan();
        loan1.setId(1L);

        Loan loan2 = new Loan();
        loan2.setId(2L);

        when(readerRepository.findById(1L))
                .thenReturn(Optional.of(reader));

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan1));

        when(loanRepository.findById(2L))
                .thenReturn(Optional.of(loan2));

        readerService.assignLoansNoTransaction(
                1L,
                List.of(1L, 2L)
        );

        assertEquals(reader, loan1.getReader());
        assertEquals(reader, loan2.getReader());

        verify(loanRepository, times(2))
                .save(any(Loan.class));
    }

    @Test
    void assignLoansNoTransaction_shouldThrowReaderNotFoundException() {

        List<Long> loanIds = List.of(1L);

        when(readerRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> readerService.assignLoansNoTransaction(
                        1L,
                        loanIds
                )
        );

        assertEquals(
                "Reader not found with id = 1",
                ex.getMessage()
        );
    }

    @Test
    void assignLoansNoTransaction_shouldThrowLoanNotFoundException() {

        List<Long> loanIds = List.of(1L);

        Reader reader = new Reader();
        reader.setId(1L);

        when(readerRepository.findById(1L))
                .thenReturn(Optional.of(reader));

        when(loanRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> readerService.assignLoansNoTransaction(
                        1L,
                        loanIds
                )
        );

        assertEquals(
                "Loan not found with id = 1",
                ex.getMessage()
        );

        verify(loanRepository).findById(1L);
    }

    @Test
    void assignLoansTransaction_shouldAssignLoans() {

        Reader reader = new Reader();
        reader.setId(1L);

        Loan loan = new Loan();
        loan.setId(1L);

        when(readerRepository.findById(1L))
                .thenReturn(Optional.of(reader));

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        readerService.assignLoansTransaction(
                1L,
                List.of(1L)
        );

        assertEquals(reader, loan.getReader());

        verify(loanRepository)
                .save(any(Loan.class));
    }
}