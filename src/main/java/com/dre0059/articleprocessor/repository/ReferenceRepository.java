package com.dre0059.articleprocessor.repository;

import com.dre0059.articleprocessor.model.Reference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReferenceRepository extends JpaRepository<Reference, Long> {

    @Query("""
       SELECT r.toDocument.publicationYear, COUNT(r)
       FROM Reference r
       WHERE r.fromDocument.category.name = :category
       GROUP BY r.toDocument.publicationYear
       ORDER BY r.toDocument.publicationYear
    """)
    List<Object[]> countReferencesByYearForCategory(@Param("category") String category);


}
