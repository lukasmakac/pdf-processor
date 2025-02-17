package com.dre0059.articleprocessor.repository;

import com.dre0059.articleprocessor.model.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// uklada extrahovane data
@Repository
public interface DocumentRepository extends JpaRepository<Dokument, Long> {
    //Optional<Dokument> findByTitleAndAuthorsSurname(String title, String surname);
}
