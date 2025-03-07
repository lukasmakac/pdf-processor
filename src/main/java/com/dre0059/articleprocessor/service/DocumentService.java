package com.dre0059.articleprocessor.service;

<<<<<<< HEAD
=======
import com.dre0059.articleprocessor.dto.DocumentDto;
import com.dre0059.articleprocessor.dto.SimpleDocumentDto;
import com.dre0059.articleprocessor.mapper.DocumentMapper;
>>>>>>> 9e1c76c (Categories of PDF initialized)
import com.dre0059.articleprocessor.repository.*;
import com.dre0059.articleprocessor.model.*;

import jakarta.transaction.Transactional;
<<<<<<< HEAD
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
=======
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

  private final DocumentMapper documentMapper;
  private final DocumentRepository documentRepository;
  private final AuthorRepository authorRepository;

  public DocumentService(DocumentMapper documentMapper, DocumentRepository documentRepository,
      AuthorRepository authorRepository) {
    this.documentMapper = documentMapper;
    this.documentRepository = documentRepository;
    this.authorRepository = authorRepository;
  }

  @Transactional
  public DocumentDto getDocumentById(Long id) {
    return documentMapper.toDocumentDto(documentRepository.findById(id).orElse(null));
  }

  @Transactional
  public List<SimpleDocumentDto> getDocumentReferences(Long id) {
    return documentMapper.toSimpleDocumentList(documentRepository.getReferencedDocumentsById(id));
  }

  @Transactional
  public List<SimpleDocumentDto> getAllReferences() {
    return documentMapper.toSimpleDocumentList(documentRepository.findAll());
  }

  @Transactional
  public Dokument saveDocument(Dokument document) {
    Dokument dok = new Dokument();
    return dok;
  }
>>>>>>> 9e1c76c (Categories of PDF initialized)
}
