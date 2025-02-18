package com.dre0059.articleprocessor.repository;

import com.dre0059.articleprocessor.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    /*@Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Author a WHERE a.lastName = :lastName AND a.firstName = :firstName")
    Optional<Author> findByLastNameAndInitial(@Param("lastName") String lastName, @Param("firstName") String firstName);
    */

    Author findByLastNameAndFirstName(String lastName, String firstName);
}

