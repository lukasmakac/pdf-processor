package com.dre0059.articleprocessor.service;

import com.dre0059.articleprocessor.repository.*;
import com.dre0059.articleprocessor.model.*;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private AuthorRepository authorRepository;

    @Transactional
    public Dokument saveDocument(Dokument document) {
       Dokument dok = new Dokument();
       return dok;
    }
}
