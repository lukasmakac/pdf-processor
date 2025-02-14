package com.dre0059.articleprocessor.repository;

import com.dre0059.articleprocessor.model.DocumentMetadata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;

import java.util.Optional;

// uklada extrahovane data
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByTitleAndAuthorsSurname(String title, String surname);
}
