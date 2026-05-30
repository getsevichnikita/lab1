package com.library.repository;

import com.library.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


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
    @Query("SELECT COUNT(b) FROM Book b " +
            "WHERE b.title = :title " +
            "AND b.publicationYear = :year " +
            "AND b.id IN (SELECT b2.id FROM Book b2 " +
            "             JOIN b2.authors a WHERE a.id IN :authorIds " +
            "             GROUP BY b2.id HAVING COUNT(a) = :authorCount)")
    long countByTitleAndYearAndAuthors(
            @Param("title") String title,
            @Param("year") int year,
            @Param("authorIds") List<Long> authorIds,
            @Param("authorCount") long authorCount
    );
}