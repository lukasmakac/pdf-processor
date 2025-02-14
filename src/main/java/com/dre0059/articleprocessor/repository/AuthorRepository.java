package com.dre0059.articleprocessor.repository;

import com.dre0059.articleprocessor.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByFullname(String name, String surname);
}
