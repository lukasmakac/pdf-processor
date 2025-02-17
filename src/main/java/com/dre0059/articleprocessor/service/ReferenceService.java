package com.dre0059.articleprocessor.service;

import com.dre0059.articleprocessor.model.*;
import com.dre0059.articleprocessor.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReferenceService {
    @Autowired
    private ReferenceRepository referenceRepository;

    @Transactional
    public Reference saveReference(Reference reference) {
        return referenceRepository.save(reference);
    }
}
