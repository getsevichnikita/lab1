package com.library.repository;

import com.library.model.entity.Reader;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
        @EntityGraph(attributePaths = "loans")
        @Query("SELECT r FROM Reader r")
        List<Reader> findAllWithLoans(Pageable pageable);

    Optional<Reader> findByName(String name);
}