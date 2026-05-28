package com.library.repository;

import com.library.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("""
    SELECT DISTINCT b
    FROM Book b
    JOIN b.authors a
    WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))
    """)
    Page<Book> findByAuthorNameJPQL(
            @Param("authorName") String authorName,
            Pageable pageable
    );

    @Query(value = """
    SELECT DISTINCT b.*
    FROM book b
    JOIN book_author ba ON b.id = ba.book_id
    JOIN author a ON ba.author_id = a.id
    WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))
    """,
            countQuery = """
    SELECT COUNT(DISTINCT b.id)
    FROM book b
    JOIN book_author ba ON b.id = ba.book_id
    JOIN author a ON ba.author_id = a.id
    WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))
    """,
            nativeQuery = true)
    Page<Book> findByAuthorNameNative(
            @Param("authorName") String authorName,
            Pageable pageable
    );

}