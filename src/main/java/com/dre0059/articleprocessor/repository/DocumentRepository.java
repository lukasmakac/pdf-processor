package com.dre0059.articleprocessor.repository;

import com.dre0059.articleprocessor.model.*;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// uklada extrahovane data
@Repository
public interface DocumentRepository extends JpaRepository<Dokument, Long> {

    @EntityGraph(attributePaths = {"tags"})
    Optional<Dokument> findWithTagsById(Long id);

    @Query(
            "SELECT COUNT(d) > 0 " +
            "FROM Dokument d " +
            "JOIN d.authors a " +
            "WHERE d.title = :title " +
            "AND a.lastName IN :lastNames"
    )
    boolean existsByTitleAndAuthorsIn(@Param("title") String title, @Param("lastNames") List<String> lastNames);

    @Query(
        "SELECT COUNT(d) > 0 " +
            "FROM Dokument d " +
            "JOIN d.authors a " +
            "WHERE d.title = :title " +
            "AND d.status = 'PDF' " +
            "AND a.lastName IN :lastNames"
    )
    boolean existsPDFByTitleAndAuthorsIn(@Param("title") String title, @Param("lastNames") List<String> lastNames);

    @Query(
            "SELECT d FROM Dokument d " +
                    "JOIN d.authors a " +
                    "WHERE d.title = :title " +
                    "AND a.lastName IN :lastNames"
    )
    Optional<Dokument> findByTitleAndAuthorsIn(@Param("title") String title, @Param("lastNames") List<String> lastNames);


    @Query(
        """
        SELECT r.toDocument FROM Dokument d
        JOIN d.references r
        WHERE d.id = :id
        """
    )
    List<Dokument> getReferencedDocumentsById(@Param("id") Long fromDocumentId);


}
