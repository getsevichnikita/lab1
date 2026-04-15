package com.library.repository;

import com.library.model.Reader;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
        @EntityGraph(attributePaths = "loans")
        @Query("SELECT r FROM Reader r")
        List<Reader> findAllWithLoans();
}