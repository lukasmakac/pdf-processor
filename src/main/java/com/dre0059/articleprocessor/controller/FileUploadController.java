package com.dre0059.articleprocessor.controller;

import com.dre0059.articleprocessor.GrobidClient;
import com.dre0059.articleprocessor.model.DocumentMetadata;
import com.dre0059.articleprocessor.service.MetadataParser;
import com.dre0059.articleprocessor.repository.DocumentRepository;
import com.dre0059.articleprocessor.repository.ReferenceRepository;
import com.dre0059.articleprocessor.service.TEIparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO :
//  1. ✅ nefunguje mi správne uloženie článku, pokiaľ už článok v DBS je, aktuálne mi vyhodí len ERROR že nemožno správne spracovať
//  2. ✅ !!! uloženie referencií do databázy
//  3. prepojím referenciu s uloženými článkami ???
//  4. viac spraviť program USER-FRIENDLY - výpis že spracovávam document, výpis že dokument už je uložený, výpis že dokument sa uložil a vypíšem metadata pre overenie
//  5. nesprávne vyťahovanie referencií - referencie ktoré sa odkazujú na nejaký web, nie sú spracované

@Controller
@RequestMapping("/api/grobid")
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    private final GrobidClient grobidClient;
    private final DocumentRepository metadataRepository;
    private final ReferenceRepository referenceRepository;

    public FileUploadController(GrobidClient grobidClient, DocumentRepository metadataRepository, ReferenceRepository referenceRepository) {
        this.grobidClient = grobidClient;
        this.metadataRepository = metadataRepository;
        this.referenceRepository = referenceRepository;
    }

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        return "upload";  // vracia upload.html
    }

    @PostMapping("/upload")
    @ResponseBody
    public Mono<ResponseEntity<Map<String, String>>> handleFileUpload(@RequestParam("file") MultipartFile file) {
        logger.info("Received file: {}", file.getOriginalFilename());

        return Mono.fromCallable(() -> {
            Path tempFile = Files.createTempFile("upload-", ".pdf");
            file.transferTo(tempFile.toFile());
            return tempFile.toFile();
        }).flatMap(pdfFile -> {
            Mono<String> metadataMono = grobidClient.processHeader(pdfFile);
            Mono<String> referencesMono = grobidClient.processReferences(pdfFile);

            return Mono.zip(metadataMono, referencesMono)
                    .flatMap(result -> {
                        String metadataJson = result.getT1();
                        String referencesXml = result.getT2();

                        String title = MetadataParser.extractTitle(metadataJson);
                        List<String> authors = MetadataParser.extractAuthors(metadataJson);

                        return Mono.justOrEmpty(metadataRepository.findByTitle(title))
                                .map(existing -> {
                                    logger.warn("Article with title '{}' already exists!", title);
                                    return ResponseEntity.status(HttpStatus.CONFLICT)
                                            .body(Map.of("error", "Article is already in database."));
                                })
                                .switchIfEmpty(Mono.fromCallable(() -> {
                                    DocumentMetadata doc = new DocumentMetadata(title, authors);
                                    metadataRepository.save(doc);

                                    // Spracovanie referencií cez TEIparser
                                    TEIparser teiParser = new TEIparser(referenceRepository);
                                    teiParser.parseAndSaveToDB(referencesXml, doc);

                                    Map<String, String> response = new HashMap<>();
                                    response.put("metadata", metadataJson);
                                    response.put("references", referencesXml);

                                    return ResponseEntity.ok(response);
                                }));
                    })
                    .onErrorResume(e -> {
                        logger.error("Error processing PDF", e);
                        return Mono.just(ResponseEntity.internalServerError().body(Map.of("error", "Failed to process PDF")));
                    });
        });
    }


}



