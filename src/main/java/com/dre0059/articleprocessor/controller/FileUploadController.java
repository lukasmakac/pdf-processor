package com.dre0059.articleprocessor.controller;

import com.dre0059.articleprocessor.GrobidClient;
import com.dre0059.articleprocessor.model.Category;
import com.dre0059.articleprocessor.repository.CategoryRepository;
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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/grobid")
public class FileUploadController {
    private final GrobidClient grobidClient;
    private final HeaderService headerService;
    private final ReferenceService referenceService;
    private final CategoryRepository categoryRepository;

    public FileUploadController(GrobidClient grobidClient, HeaderService headerService, ReferenceService referenceService, CategoryRepository categoryRepository) {
        this.grobidClient = grobidClient;
        this.headerService = headerService;
        this.referenceService = referenceService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "upload";  // vracia upload.html
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("category") String category) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded!");
        }

        System.out.println("Received file: " + file.getOriginalFilename());
        System.out.println("Received category: " + category);

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

            String categoryId = category.substring(0, 3);
            Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

            System.out.println("ID category is : " + categoryId);
            System.out.println("Optional category is : " + categoryOptional);
            if (category.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid category ID!");
            }

            headerService.processHeader(header, categoryOptional);
            referenceService.extractReferences(references);

            //System.out.println(header);
            //System.out.println(references);



            tmpFile.delete();

            return ResponseEntity.ok(header);


        } catch (IOException e) {
            System.out.println("Chyba pri vytváraní dočasného súboru" + e);
            return ResponseEntity.status(500).body("Chyba pri vytváraní dočasného súboru.");
        }
    }


}



