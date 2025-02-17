package com.dre0059.articleprocessor.controller;

import com.dre0059.articleprocessor.GrobidClient;
import com.dre0059.articleprocessor.service.HeaderService;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private final GrobidClient grobidClient;
    private final HeaderService headerService;

    //private final DocumentRepository metadataRepository;
    //private final ReferenceRepository referenceRepository;
    //private final MetadataParser metadataParser;
    //private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    public FileUploadController(GrobidClient grobidClient, HeaderService headerService/*, DocumentRepository metadataRepository, ReferenceRepository referenceRepository, MetadataParser metadataParser*/) {
        this.grobidClient = grobidClient;
        this.headerService = headerService;
      //  this.metadataRepository = metadataRepository;
        //this.referenceRepository = referenceRepository;
        //this.metadataParser = metadataParser;
    }

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        return "upload";  // vracia upload.html
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded!");
        }

        System.out.println("Received file: " + file.getOriginalFilename());

        try {
            // Vytvorenie dočasného súboru
            File tmpFile = File.createTempFile("article-", ".pdf");

            // save data from file to tmpFile
            try(FileOutputStream stream = new FileOutputStream(tmpFile)) {
                stream.write(file.getBytes());
             } catch (IOException e) {
                return ResponseEntity.internalServerError().body("FAILURE - cannot process file : " + e.getMessage());
            }

            String header = grobidClient.processHeader(tmpFile);
            String references = grobidClient.processReferences(tmpFile);

            headerService.processHeader(header);

            System.out.println(header);
            //System.out.println(references);

            tmpFile.delete();

            return ResponseEntity.ok(header);


        } catch (IOException e) {
            System.out.println("Chyba pri vytváraní dočasného súboru" + e);
            return ResponseEntity.status(500).body("Chyba pri vytváraní dočasného súboru.");
        }
    }


}



