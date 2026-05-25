package com.library.service;

import com.library.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import com.library.mapper.ReaderMapper;
import com.library.model.Loan;
import com.library.model.Reader;
import com.library.model.ReaderDTO;
import com.library.model.ReaderDTONplus1;
import com.library.repository.LoanRepository;
import com.library.repository.ReaderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class ReaderService {

    private final ReaderRepository readerRepository;
    private final LoanRepository loanRepository;

    public List<ReaderDTONplus1> getAll(Pageable pageable) {
        return readerRepository.findAll(pageable)
                .stream()
                .map(ReaderMapper::toDtoNplus1)
                .toList();
    }


    public ReaderDTO getById(Long id) {
        return ReaderMapper.toDto(
                readerRepository.findById(id).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reader not found with id = " + id
                        )
                )
        );
    }

    public ReaderDTO save(ReaderDTO dto) {
        List<Loan> loans = dto.getLoanIds() == null
                ? List.of()
                : loanRepository.findAllById(dto.getLoanIds());

        Reader reader = ReaderMapper.toEntity(dto, loans);
        log.info("Reader created with id={}", dto.getId());
        return ReaderMapper.toDto(
                readerRepository.save(reader)
        );
    }

    public ReaderDTO update(Long id, ReaderDTO dto) {
        Reader reader = readerRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Reader not found with id = " + id
                )
        );
        reader.setName(dto.getName());
        if (dto.getLoanIds() != null) {
            reader.setLoans(loanRepository.findAllById(dto.getLoanIds()));
        }
        log.info("Reader updated with id={}", dto.getId());
        return ReaderMapper.toDto(
                readerRepository.save(reader)
        );
    }
    public void delete(Long id) {

        Reader reader = readerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reader not found with id = " + id
                        )
                );

        readerRepository.delete(reader);

        log.info("Reader deleted with id={}", id);
    }

    public List<ReaderDTONplus1> getAllEntityGraph(Pageable pageable) {
        return readerRepository.findAllWithLoans(pageable)
                .stream()
                .map(ReaderMapper::toDtoNplus1)
                .toList();
    }
    public void assignLoansNoTransaction(Long readerId, List<Long> loanIds) {
            Reader reader = readerRepository.findById(readerId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Reader not found with id = " + readerId
                            )
                    );
            for (Long loanId : loanIds) {
                Loan loan = loanRepository.findById(loanId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Loan not found with id = " + loanId
                                )
                        );                loan.setReader(reader);
                loanRepository.save(loan);
            }
        }
    @Transactional
    public void assignLoansTransaction(Long readerId, List<Long> loanIds) {
        assignLoansNoTransaction(readerId, loanIds);
    }
}