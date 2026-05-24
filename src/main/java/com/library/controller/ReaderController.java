package com.library.controller;
import com.library.model.ReaderDTO;
import com.library.model.ReaderDTONplus1;
import com.library.service.ReaderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;

    @PatchMapping("/assign/no-tx")
    public void assignNoTx(@Valid @RequestBody ReaderDTO dto) {
        readerService.assignLoansNoTransaction(
                dto.getId(),
                dto.getLoanIds()
        );
    }

    @PatchMapping("/assign/tx")
    public void assignTx(@Valid @RequestBody ReaderDTO dto) {
        readerService.assignLoansTransaction(
                dto.getId(),
                dto.getLoanIds()
        );
    }
    @GetMapping("/entity-graph")
    public List<ReaderDTONplus1> getAllEntityGraph(Pageable pageable) {
        return readerService.getAllEntityGraph(pageable);
    }

    @GetMapping("/nplus1")
    public List<ReaderDTONplus1> getAll(Pageable pageable) {
        return readerService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public ReaderDTO getById(@PathVariable Long id) {
        return readerService.getById(id);
    }

    @PostMapping
    public ReaderDTO create(@Valid @RequestBody ReaderDTO dto) {
        return readerService.save(dto);
    }

    @PutMapping("/{id}")
    public ReaderDTO update(@Valid @PathVariable Long id, @RequestBody ReaderDTO dto) {
        return readerService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        readerService.delete(id);
    }
}