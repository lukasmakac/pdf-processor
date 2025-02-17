package com.dre0059.articleprocessor.repository;

import com.dre0059.articleprocessor.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT a FROM Author a WHERE a.lastName = :lastName AND SUBSTRING(a.firstName, 1, 1) = SUBSTRING(:firstName, 1, 1)")
    Optional<Author> findByFullName(@Param("lastName") String lastName, @Param("firstName") String firstName);

}
