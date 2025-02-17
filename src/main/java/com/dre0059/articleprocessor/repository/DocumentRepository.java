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

    @Query("SELECT COUNT(d) > 0 FROM Dokument d JOIN d.authors a WHERE d.title = :title AND a IN :authors")
    boolean existsByTitleAndAuthorsIn(@Param("title") String title, @Param("authors") List<Author> authors);
}
