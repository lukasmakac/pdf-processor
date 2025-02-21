package com.dre0059.articleprocessor.repository;

import com.dre0059.articleprocessor.model.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// uklada extrahovane data
@Repository
public interface DocumentRepository extends JpaRepository<Dokument, Long> {

    @Query(
            "SELECT COUNT(d) > 0 " +
            "FROM Dokument d " +
            "JOIN d.authors a " +
            "WHERE d.title = :title " +
            "AND a.lastName IN :lastNames"
    )
    boolean existsByTitleAndAuthorsIn(@Param("title") String title, @Param("lastNames") List<String> lastNames);


    @Query(
            "SELECT d FROM Dokument d " +
                    "JOIN d.authors a " +
                    "WHERE d.title = :title " +
                    "AND a.lastName IN :lastNames"
    )
    Optional<Dokument> findByTitleAndAuthorsIn(@Param("title") String title, @Param("lastNames") List<String> lastNames);

}

/*
    // save only if all authors are the same
    @Query("""
        SELECT COUNT(d) > 0
        FROM Dokument d
        WHERE d.title = :title
        AND SIZE(d.authors) = :authorCount
        AND EXISTS (
            SELECT 1 FROM Dokument d2 JOIN d2.authors a2
            WHERE d2.id = d.id AND a2 IN :authors
        )
        """)
    boolean existsByTitleAndAuthors(@Param("title") String title, @Param("authors") List<Author> authors, @Param("authorCount") int authorCount);

 */