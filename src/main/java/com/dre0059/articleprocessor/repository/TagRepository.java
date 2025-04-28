package com.dre0059.articleprocessor.repository;

import com.dre0059.articleprocessor.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByTitle(String title);

    List<Tag> findByTitleContainingIgnoreCase(String term); // musí vracať List<Tag>
    Optional<Tag> findByTitleIgnoreCase(String title);

}
