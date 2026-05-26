package com.library.controller;
import com.library.exception.ErrorResponse;
import com.library.model.dto.LoanDTO;
import com.library.service.LoanService;
import com.library.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Loan management endpoints")
public class LoanController {

    private final LoanService loanService;
    private final TaskService taskService;

    @Operation(summary = "Get all loans")
    @ApiResponse(responseCode = "200", description = "Loans retrieved successfully")
    @GetMapping
    public List<LoanDTO> getAll(Pageable pageable) {
        return loanService.getAll(pageable);
    }

    @Operation(summary = "Get loan by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan found"),
            @ApiResponse(responseCode = "404", description = "Loan not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @GetMapping("/{id}")
    public LoanDTO getById(@PathVariable Long id) {
        return loanService.getById(id);
    }

    @Operation(summary = "Create new loan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PostMapping
    public LoanDTO create(@Valid @RequestBody LoanDTO dto) {
        return loanService.create(dto);
    }

    @Operation(summary = "Update loan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Loan not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PutMapping("/{id}")
    public LoanDTO update(@Valid @PathVariable Long id, @RequestBody LoanDTO dto) {
        return loanService.update(id, dto);
    }

    @Operation(summary = "Delete loan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Loan not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        loanService.delete(id);
    }

    @Operation(summary = "Bulk create loans without transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loans created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Book or reader not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PostMapping("/bulk/no-tx")
    public List<LoanDTO> createBulkNoTx(
            @Valid @RequestBody List<LoanDTO> dtos
    ) {
        return loanService.createBulkNoTransaction(dtos);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loans created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Book or reader not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @Operation(summary = "Bulk create loans with transaction")
    @PostMapping("/bulk/tx")
    public List<LoanDTO> createBulkTx(
            @Valid @RequestBody List<LoanDTO> dtos
    ) {
        return loanService.createBulkTransaction(dtos);
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Async task started successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Book or reader not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @Operation(
            summary = "Bulk create loans asynchronously"
    )
    @PostMapping("/bulk/async")
    public String createBulkAsync(
            @RequestBody List<LoanDTO> dtos
    ) {

        Long taskId = taskService.createTask();

        loanService.createBulkAsync(taskId, dtos);

        return "Task started: " + taskId;
    }
}

