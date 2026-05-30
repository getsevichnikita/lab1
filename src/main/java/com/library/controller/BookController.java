package com.library.controller;
import com.library.exception.ErrorResponse;
import com.library.exception.ResourceNotFoundException;
import com.library.model.dto.BookDTO;
import com.library.model.dto.BookDTOFields;
import com.library.model.dto.BookDTOFieldsOwner;
import com.library.model.entity.BookPDF;
import com.library.repository.BookPDFRepository;
import com.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Book management endpoints")
public class BookController {
    private final BookService bookService;
    private final BookPDFRepository bookPDFRepository;

    @Operation(summary = "Create new book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PostMapping
    public BookDTO create(@Valid @RequestBody BookDTO dto) {
        return bookService.create(dto);
    }

    @Operation(summary = "Get all books")
    @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    @GetMapping
    public List<BookDTOFieldsOwner> getAll(Pageable pageable) {
        return bookService.getAll(pageable);
    }

    @Operation(summary = "Get book by id")    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @GetMapping("/{id}")
    public BookDTO getById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @Operation(summary = "Update book")    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PutMapping("/{id}")
    public BookDTO update(@PathVariable Long id, @Valid @RequestBody BookDTO dto) {
        return bookService.update(id, dto);
    }

    @Operation(summary = "Delete book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }

    @Operation(summary = "Search books by author using JPQL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @GetMapping("/search/jpql")
    public List<BookDTOFields> searchByAuthorJPQL(
            @RequestParam String author,
            Pageable pageable
    ) {
        return bookService.searchByAuthorJPQL(author, pageable);
    }

    @Operation(summary = "Search books by author using native query")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @GetMapping("/search/native")
    public List<BookDTOFields> searchByAuthorNative(
            @RequestParam String author,
            Pageable pageable
    ) {
        return bookService.searchByAuthorNative(author, pageable);
    }

    @PostMapping("/upload")
    public BookDTO uploadBook(
            @RequestPart("book") BookDTOFieldsOwner bookDto,
            @RequestPart("file") MultipartFile file) {
        return bookService.uploadBook(bookDto, file);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> getPdf(@PathVariable Long id) {
        BookPDF pdf = bookPDFRepository.findByBookId(id)
                .orElseThrow(() -> new ResourceNotFoundException("PDF not found"));

        byte[] data = pdf.getFileData();
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + pdf.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @PutMapping("/{id}/pdf")
    public ResponseEntity<String> updatePdf(@PathVariable Long id,
                                            @RequestParam("file") MultipartFile file) {
        bookService.updatePdf(id, file);
        return ResponseEntity.ok("PDF updated");
    }
}
