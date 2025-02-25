package com.dre0059.articleprocessor.controller;

import com.dre0059.articleprocessor.dto.DocumentDto;
import com.dre0059.articleprocessor.dto.SimpleDocumentDto;
import com.dre0059.articleprocessor.service.DocumentService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller("api/document")
public class DocumentController {

  private final DocumentService documentService;


  public DocumentController(DocumentService documentService) {
    this.documentService = documentService;
  }

  @GetMapping("/{id}")
  public DocumentDto getDocumentById(@PathVariable Long id) {
    return documentService.getDocumentById(id);
  }

  @GetMapping("/references/{id}")
  public List<SimpleDocumentDto> getReferencesFromDocument(@PathVariable Long id) {
    return documentService.getDocumentReferences(id);
  }

  @GetMapping("/view/{id}")
  public String viewPdf(Model model, @PathVariable("id") Long id) {
    var references = documentService.getDocumentReferences(id);

    model.addAttribute("references", references);

    return "view-pdf";
  }
}
