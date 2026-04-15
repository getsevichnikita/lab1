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
    public void updateReadersNoTransaction(List<Long> readerIds) {

        for (Long id : readerIds) {

            Reader reader = readerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Reader not found: " + id));

            reader.setName(reader.getName() + " UPDATED");

            readerRepository.save(reader);

        }
    }
    @Transactional
    public void updateReadersTransaction(List<Long> readerIds) {
            updateReadersNoTransaction(readerIds);

    }
}