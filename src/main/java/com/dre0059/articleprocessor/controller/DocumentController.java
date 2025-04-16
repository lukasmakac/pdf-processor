package com.dre0059.articleprocessor.controller;

import com.dre0059.articleprocessor.dto.DocumentDto;
import com.dre0059.articleprocessor.dto.SimpleDocumentDto;
import com.dre0059.articleprocessor.service.DocumentService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class DocumentController {

  private final DocumentService documentService;

  public DocumentController(DocumentService documentService) {
    this.documentService = documentService;
  }

  @GetMapping("/api/documents/{id}")
  public ResponseEntity<DocumentDto> getDocumentById(@PathVariable Long id) {
    return ResponseEntity.ok(documentService.getDocumentById(id));
  }

  @GetMapping(
    value = "/api/documents/{id}/content",
    produces = MediaType.APPLICATION_PDF_VALUE
  )
  public @ResponseBody byte[] getDocumentContentById(@PathVariable Long id) {
    return documentService.getDocumentContentById(id).getContent();
  }

  @GetMapping("/api/documents/{id}/references")
  public ResponseEntity<List<SimpleDocumentDto>> getReferencesFromDocument(@PathVariable Long id) {
    return ResponseEntity.ok(documentService.getReferencedDocumentsById(id));
  }

  @GetMapping("/view/{id}")
  public String viewDocument(Model model, @PathVariable("id") Long id) {
    var references = documentService.getReferencedDocumentsById(id);

    if (documentService.getDocumentById(id) == null) {
      throw new IllegalArgumentException("Document with ID " + id + " not found.");
    }

    model.addAttribute("documentId", id);
    model.addAttribute("references", references);
    model.addAttribute("docTitle", documentService.getDocumentById(id).getTitle());


    return "view-pdf";
  }

  @GetMapping("/view")
  public String viewAllDocuments(Model model) {
    var documents = documentService.getAllDocuments();

    model.addAttribute("documents", documents);

    return "view-all";
  }
}
