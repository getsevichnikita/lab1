package com.library.service;

import com.library.mapper.ReaderMapper;
import com.library.model.Loan;
import com.library.model.Reader;
import com.library.model.ReaderDTO;
import com.library.repository.LoanRepository;
import com.library.repository.ReaderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReaderService {

    private final ReaderRepository readerRepository;
    private final LoanRepository loanRepository;

    public List<ReaderDTO> getAll() {
        return readerRepository.findAll().stream()
                .map(ReaderMapper::toDto)
                .toList();
    }

    public ReaderDTO getById(Long id) {
        return ReaderMapper.toDto(
                readerRepository.findById(id).orElseThrow()
        );
    }

    public ReaderDTO save(ReaderDTO dto) {

        List<Loan> loans = dto.getLoanIds() == null
                ? List.of()
                : loanRepository.findAllById(dto.getLoanIds());

        Reader reader = ReaderMapper.toEntity(dto, loans);

        return ReaderMapper.toDto(
                readerRepository.save(reader)
        );
    }

    public ReaderDTO update(Long id, ReaderDTO dto) {
        Reader reader = readerRepository.findById(id).orElseThrow();
        reader.setName(dto.getName());
        if (dto.getLoanIds() != null) {
            reader.setLoans(loanRepository.findAllById(dto.getLoanIds()));
        }
        return ReaderMapper.toDto(
                readerRepository.save(reader)
        );
    }
    public void delete(Long id) {
        readerRepository.deleteById(id);
    }

    public List<ReaderDTO> getAllEntityGraph() {
        return readerRepository.findAllWithLoans().stream()
                .map(ReaderMapper::toDto)
                .toList();
    }
    public void assignLoansNoTransaction(Long readerId, List<Long> loanIds) {
            Reader reader = readerRepository.findById(readerId)
                    .orElseThrow(() -> new RuntimeException("Reader not found"));
            for (Long loanId : loanIds) {
                Loan loan = loanRepository.findById(loanId)
                        .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));
                loan.setReader(reader);
                loanRepository.save(loan);
            }
        }
    @Transactional
    public void assignLoansTransaction(Long readerId, List<Long> loanIds) {
        assignLoansNoTransaction(readerId, loanIds);
    }
}