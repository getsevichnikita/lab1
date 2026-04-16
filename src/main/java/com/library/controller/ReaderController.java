package com.library.controller;
import com.library.model.ReaderDTO;
import com.library.service.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;

    @PostMapping("/assign/no-tx")
    public void assignNoTx(@RequestBody ReaderDTO dto) {
        readerService.assignLoansNoTransaction(
                dto.getId(),
                dto.getLoanIds()
        );
    }

    @PostMapping("/assign/tx")
    public void assignTx(@RequestBody ReaderDTO dto) {
        readerService.assignLoansTransaction(
                dto.getId(),
                dto.getLoanIds()
        );
    }
    @GetMapping("/entity-graph")
    public List<ReaderDTO> getAllEntityGraph() {
        return readerService.getAllEntityGraph();
    }

    @GetMapping("/nplus1")
    public List<ReaderDTO> getAll() {
        return readerService.getAll();
    }

    @GetMapping("/{id}")
    public ReaderDTO getById(@PathVariable Long id) {
        return readerService.getById(id);
    }

    @PostMapping
    public ReaderDTO create(@RequestBody ReaderDTO dto) {
        return readerService.save(dto);
    }

    @PutMapping("/{id}")
    public ReaderDTO update(@PathVariable Long id, @RequestBody ReaderDTO dto) {
        return readerService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        readerService.delete(id);
    }
}