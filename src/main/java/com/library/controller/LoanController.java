package com.library.controller;

import com.library.model.Loan;
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
        return loanService.getAllEntityGraph();
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

    @GetMapping("/nplus1")
    public List<LoanDTO> nplus1() {
        return loanService.getAllNPlusOne();
    }
    @GetMapping("/joinfetch")
    public List<LoanDTO> joinFetch() {
        return loanService.getAllJoinFetch();
    }

    @GetMapping("/no-transaction")
    public String noTransaction() {
        loanService.createLoanWithoutTransaction();
        return "done";
    }

    @GetMapping("/with-transaction")
    public String withTransaction() {
        loanService.createLoanWithTransaction();
        return "done";
    }

}

