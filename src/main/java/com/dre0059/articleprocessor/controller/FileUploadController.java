package com.dre0059.articleprocessor.controller;

import com.dre0059.articleprocessor.GrobidClient;
<<<<<<< HEAD
=======
import com.dre0059.articleprocessor.model.Category;
import com.dre0059.articleprocessor.repository.CategoryRepository;
>>>>>>> 9e1c76c (Categories of PDF initialized)
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
<<<<<<< HEAD
=======
import java.util.List;
import java.util.Optional;
>>>>>>> 9e1c76c (Categories of PDF initialized)

@Controller
@RequestMapping("/api/grobid")
public class FileUploadController {
    private final GrobidClient grobidClient;
    private final HeaderService headerService;
    private final ReferenceService referenceService;
<<<<<<< HEAD

    public FileUploadController(GrobidClient grobidClient, HeaderService headerService, ReferenceService referenceService) {
        this.grobidClient = grobidClient;
        this.headerService = headerService;
        this.referenceService = referenceService;
=======
    private final CategoryRepository categoryRepository;

    public FileUploadController(GrobidClient grobidClient, HeaderService headerService, ReferenceService referenceService, CategoryRepository categoryRepository) {
        this.grobidClient = grobidClient;
        this.headerService = headerService;
        this.referenceService = referenceService;
        this.categoryRepository = categoryRepository;
>>>>>>> 9e1c76c (Categories of PDF initialized)
    }

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
<<<<<<< HEAD
=======
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
>>>>>>> 9e1c76c (Categories of PDF initialized)
        return "upload";  // vracia upload.html
    }

    @PostMapping("/upload")
    @ResponseBody
<<<<<<< HEAD
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
=======
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("category") String category) {
>>>>>>> 9e1c76c (Categories of PDF initialized)
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded!");
        }

        System.out.println("Received file: " + file.getOriginalFilename());
<<<<<<< HEAD
=======
        System.out.println("Received category: " + category);
>>>>>>> 9e1c76c (Categories of PDF initialized)

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

<<<<<<< HEAD
            headerService.processHeader(header);
            referenceService.extractReferences(references);

            //System.out.println(header);
            System.out.println(references);
=======
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


>>>>>>> 9e1c76c (Categories of PDF initialized)

            tmpFile.delete();

            return ResponseEntity.ok(header);


        } catch (IOException e) {
            System.out.println("Chyba pri vytváraní dočasného súboru" + e);
            return ResponseEntity.status(500).body("Chyba pri vytváraní dočasného súboru.");
        }
    }


}



