package com.library.controller;
import com.library.model.LoanDTO;
import com.library.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping
    public List<LoanDTO> getAll() {
        return loanService.getAll();
    }

    @GetMapping("/{id}")
    public LoanDTO getById(@PathVariable Long id) {
        return loanService.getById(id);
    }

    @PostMapping
    public LoanDTO create(@RequestBody LoanDTO dto) {
        return loanService.create(dto);
    }

    @PutMapping("/{id}")
    public LoanDTO update(@PathVariable Long id, @RequestBody LoanDTO dto) {
        return loanService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        loanService.delete(id);
    }

}

