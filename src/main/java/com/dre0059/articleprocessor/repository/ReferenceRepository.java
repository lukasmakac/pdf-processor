package com.dre0059.articleprocessor.repository;

import com.dre0059.articleprocessor.model.Reference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferenceRepository extends JpaRepository<Reference, Long> {
}
