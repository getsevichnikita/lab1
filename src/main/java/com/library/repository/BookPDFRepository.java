package com.library.repository;

import com.library.model.entity.BookPDF;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookPDFRepository
        extends JpaRepository<BookPDF, Long> {
    Optional<BookPDF> findByBookId(Long bookId);
}