package com.dre0059.articleprocessor.controller;

import com.dre0059.articleprocessor.GrobidClient;
import com.dre0059.articleprocessor.service.HeaderService;

import com.dre0059.articleprocessor.service.ReferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller
@RequestMapping("/api/grobid")
public class FileUploadController {
    private final GrobidClient grobidClient;
    private final HeaderService headerService;
    private final ReferenceService referenceService;

    public FileUploadController(GrobidClient grobidClient, HeaderService headerService, ReferenceService referenceService) {
        this.grobidClient = grobidClient;
        this.headerService = headerService;
        this.referenceService = referenceService;
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
            referenceService.extractReferences(references);

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



