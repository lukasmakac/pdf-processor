package com.dre0059.articleprocessor.service;

import com.dre0059.articleprocessor.dto.DocumentContentDto;
import com.dre0059.articleprocessor.dto.DocumentDto;
import com.dre0059.articleprocessor.dto.SimpleDocumentDto;
import com.dre0059.articleprocessor.mapper.DocumentMapper;
import com.dre0059.articleprocessor.model.Author;
import com.dre0059.articleprocessor.model.Category;
import com.dre0059.articleprocessor.model.Dokument;
import com.dre0059.articleprocessor.repository.AuthorRepository;
import com.dre0059.articleprocessor.repository.CategoryRepository;
import com.dre0059.articleprocessor.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

  private final DocumentMapper documentMapper;
  private final DocumentRepository documentRepository;
  private final AuthorRepository authorRepository;
  private final CategoryRepository categoryRepository;

  public DocumentService(DocumentMapper documentMapper, DocumentRepository documentRepository,
      AuthorRepository authorRepository, CategoryRepository categoryRepository) {
    this.documentMapper = documentMapper;
    this.documentRepository = documentRepository;
    this.authorRepository = authorRepository;
    this.categoryRepository = categoryRepository;
  }

  @Transactional
  public DocumentDto getDocumentById(Long documentId) {
    //Dokument dokument = documentRepository.findById(documentId).orElse(null);
    Dokument dokument = documentRepository.findWithTagsById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Dokument not found with id: " + documentId));

    // Použitie mappera na konverziu dokumentu do DTO
    DocumentDto documentDto = documentMapper.toDocumentDto(dokument);

    return documentDto;
  }


  @Transactional
  public DocumentContentDto getDocumentContentById(Long documentId) {
    return documentMapper.toDocumentContentDto(documentRepository.findById(documentId).orElse(null));
  }

  @Transactional
  public List<SimpleDocumentDto> getReferencedDocumentsById(Long documentId) {
    return documentMapper.toSimpleDocumentList(documentRepository.getReferencedDocumentsById(documentId));
  }

  @Transactional
  public List<SimpleDocumentDto> getAllDocuments() {
    return documentMapper.toSimpleDocumentList(documentRepository.findAll());
  }

}
