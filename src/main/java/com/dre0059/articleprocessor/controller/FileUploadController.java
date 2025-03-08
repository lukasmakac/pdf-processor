package com.dre0059.articleprocessor.controller;

import com.dre0059.articleprocessor.GrobidClient;
import com.dre0059.articleprocessor.service.CategoryService;
import com.dre0059.articleprocessor.service.HeaderService;
import com.dre0059.articleprocessor.service.ReferenceService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping
public class FileUploadController {
    private final GrobidClient grobidClient;
    private final HeaderService headerService;
    private final ReferenceService referenceService;
    private final CategoryService categoryService;

    public FileUploadController(GrobidClient grobidClient, HeaderService headerService, ReferenceService referenceService, CategoryService categoryService) {
        this.grobidClient = grobidClient;
        this.headerService = headerService;
        this.referenceService = referenceService;
        this.categoryService = categoryService;
    }

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        var categories = categoryService.getAll();
        model.addAttribute("categories", categories);
        return "upload";  // vracia upload.html
    }

    @PostMapping("/api/upload")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("categoryId") String categoryId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded!");
        }

        try {
            File tmpFile = File.createTempFile("article-", ".pdf");

            // save data from file to tmpFile
            try(FileOutputStream stream = new FileOutputStream(tmpFile)) {
                stream.write(file.getBytes());
             } catch (IOException e) {
                return ResponseEntity.internalServerError().body("FAILURE - cannot process file : " + e.getMessage());
            }

            String header = grobidClient.processHeader(tmpFile);
            String references = grobidClient.processReferences(tmpFile);

            headerService.processHeader(header, categoryId, tmpFile);
            referenceService.extractReferences(references);

            tmpFile.delete();

            return ResponseEntity.ok(header);


        } catch (IOException e) {
            System.out.println("Chyba pri vytváraní dočasného súboru" + e);
            return ResponseEntity.status(500).body("Chyba pri vytváraní dočasného súboru.");
        }
    }


}



