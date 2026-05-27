package com.library.controller;

import com.library.exception.ErrorResponse;
import com.library.model.dto.LoginRequest;
import com.library.model.dto.ReaderDTO;
import com.library.model.dto.ReaderDTONplus1;
import com.library.service.ReaderService;
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
@RequestMapping("/readers")
@RequiredArgsConstructor
@Tag(name = "Readers", description = "Reader management endpoints")
public class ReaderController {
    private final ReaderService readerService;
    @GetMapping("/by-name")
    public ReaderDTO getByName(@RequestParam String name) {
        return readerService.findByName(name);
    }
    @Operation(summary = "Assign loans to the reader (non-transactional)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loans assigned"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Reader or loan not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PatchMapping("/assign/no-tx")
    public void assignNoTx(@Valid @RequestBody ReaderDTO dto) {
        readerService.assignLoansNoTransaction(
                dto.getId(),
                dto.getLoanIds()
        );
    }

    @Operation(summary = "Assign loans to the reader (transactional)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loans assigned"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Reader or loan not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PatchMapping("/assign/tx")
    public void assignTx(@Valid @RequestBody ReaderDTO dto) {
        readerService.assignLoansTransaction(
                dto.getId(),
                dto.getLoanIds()
        );
    }

    @Operation(summary = "Get all readers (entity graph)")
    @ApiResponse(responseCode = "200", description = "Readers found")
    @GetMapping("/entity-graph")
    public List<ReaderDTONplus1> getAllEntityGraph(Pageable pageable) {
        return readerService.getAllEntityGraph(pageable);
    }

    @Operation(summary = "Get all readers (N+1)")
    @ApiResponse(responseCode = "200", description = "Readers found")
    @GetMapping("/nplus1")
    public List<ReaderDTONplus1> getAll(Pageable pageable) {
        return readerService.getAll(pageable);
    }

    @Operation(summary = "Get reader by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reader found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reader not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ReaderDTO getById(@PathVariable Long id) {
        return readerService.getById(id);
    }

    @Operation(summary = "Create new reader")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reader created"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PostMapping
    public ReaderDTO create(@Valid @RequestBody ReaderDTO dto) {
        return readerService.save(dto);
    }

    @Operation(summary = "Update reader")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reader updated"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Reader not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PutMapping("/{id}")
    public ReaderDTO update(
            @PathVariable Long id,
            @Valid @RequestBody ReaderDTO dto
    ) {
        return readerService.update(id, dto);
    }

    @Operation(summary = "Delete reader")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reader deleted"),
            @ApiResponse(responseCode = "404", description = "Reader not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        readerService.delete(id);
    }
}