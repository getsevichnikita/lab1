package com.library.controller;

import com.library.model.ReaderDTO;
import com.library.mapper.ReaderMapper;
import com.library.model.Loan;
import com.library.service.LoanService;
import com.library.service.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;
    private final LoanService loanService;

    @GetMapping
    public List<ReaderDTO> getAll() {
        return readerService.getAll().stream()
                .map(ReaderMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ReaderDTO getById(@PathVariable Long id) {
        return ReaderMapper.toDto(readerService.getById(id));
    }

    @PostMapping
    public ReaderDTO create(@RequestBody ReaderDTO dto) {
        List<Loan> loans = loanService.getAllByIds(dto.getLoanIds());
        return ReaderMapper.toDto(readerService.save(
                ReaderMapper.toEntity(dto, loans)
        ));
    }

    @PutMapping("/{id}")
    public ReaderDTO update(@PathVariable Long id, @RequestBody ReaderDTO dto) {
        List<Loan> loans = loanService.getAllByIds(dto.getLoanIds());
        return ReaderMapper.toDto(readerService.update(
                id,
                ReaderMapper.toEntity(dto, loans)
        ));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        readerService.delete(id);
    }
}