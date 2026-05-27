package com.library.service;
import com.library.exception.InvalidLoanDatesException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.entity.Book;
import com.library.model.entity.Loan;
import com.library.model.dto.LoanDTO;
import com.library.mapper.LoanMapper;
import com.library.model.entity.Reader;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {
    private static final String LOG_LOAN_NOT_FOUND = "Loan not found with id = " ;
    private final LoanRepository loanRepository;
    private final ReaderRepository readerRepository;
    private final BookRepository bookRepository;
    private final TaskService taskService;

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
                                LOG_LOAN_NOT_FOUND + id
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
        Reader reader = readerRepository.findById(dto.getReaderId()).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Reader not found with id = " + dto.getReaderId()
                )
        );
        Book book = bookRepository.findById(dto.getBookId()).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Book not found with id = " + dto.getBookId()
                )
        );

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
                        LOG_LOAN_NOT_FOUND + id
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
                                LOG_LOAN_NOT_FOUND + id
                        )
                );

        loanRepository.delete(loan);

        log.info("Loan deleted with id={}", id);
    }
    public List<LoanDTO> createBulkNoTransaction(List<LoanDTO> dtos) {
        return dtos.stream()
                .map(this::create)
                .toList();
    }

    @Transactional
    public List<LoanDTO> createBulkTransaction(List<LoanDTO> dtos) {
        return createBulkNoTransaction(dtos);
    }

    @Async
    public CompletableFuture<Void> createBulkAsync(

            Long taskId,
            List<LoanDTO> dtos
    ) {

        try {

            Thread.sleep(20000);

            createBulkTransaction(dtos);

            taskService.markDone(taskId);

            log.info(
                    "Async bulk operation completed successfully. taskId={}",
                    taskId
            );

        } catch (InterruptedException ex) {

            Thread.currentThread().interrupt();

            log.error(
                    "Async bulk operation was interrupted. taskId={}",
                    taskId,
                    ex
            );

            taskService.markFailed(
                    taskId,
                    "Task execution was interrupted"
            );

        } catch (
                InvalidLoanDatesException |
                ResourceNotFoundException ex
        ) {

            log.error(
                    "Async bulk operation failed. taskId={}, error={}",
                    taskId,
                    ex.getMessage(),
                    ex
            );

            taskService.markFailed(
                    taskId,
                    ex.getMessage()
            );

        } catch (Exception ex) {

            log.error(
                    "Unexpected async bulk operation error. taskId={}",
                    taskId,
                    ex
            );

            taskService.markFailed(
                    taskId,
                    "Unexpected server error"
            );
        }

        return CompletableFuture.completedFuture(null);
    }
}



