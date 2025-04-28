package com.dre0059.articleprocessor.controller;

import com.dre0059.articleprocessor.GrobidClient;
import com.dre0059.articleprocessor.model.Dokument;
import com.dre0059.articleprocessor.model.Tag;
import com.dre0059.articleprocessor.repository.TagRepository;
import com.dre0059.articleprocessor.service.CategoryService;
import com.dre0059.articleprocessor.service.HeaderService;
import com.dre0059.articleprocessor.service.ReferenceService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping(value = {"/upload","/"})
    public String showUploadForm(Model model) {
        var categories = categoryService.getAll();
        model.addAttribute("categories", categories);
        return "upload";  // vracia upload.html
    }

    @PostMapping("/api/upload")
    @ResponseBody
    public ResponseEntity<?> handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("categoryId") String categoryId,
            @RequestParam("tags") List<String> tags) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded!");
        }

        try {
            System.out.println("Processing file " + file.getOriginalFilename());
            File tmpFile = File.createTempFile("article-", ".pdf");

            // save data from file to tmpFile
            try(FileOutputStream stream = new FileOutputStream(tmpFile)) {
                stream.write(file.getBytes());
             } catch (IOException e) {
                return ResponseEntity.internalServerError().body("FAILURE - cannot process file : " + e.getMessage());
            }
            //System.out.println("File written to temporary location. ");

            String header = grobidClient.processHeader(tmpFile);
            //System.out.println("GROBID Header processed: " + header);

            String references = grobidClient.processReferences(tmpFile);
            //System.out.println("GROBID Reference processed: " + references);

            Dokument savedDocument = headerService.processHeader(header, categoryId, tags, tmpFile);
            //System.out.println("Header saved to database.");

            referenceService.extractReferences(references);
            //System.out.println("References extracted..");

            tmpFile.delete();

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedDocument.getId());
            response.put("message", "Upload successful");

            return ResponseEntity.ok(response);


        } catch (IOException e) {
            return ResponseEntity.status(500).body("Chyba pri vytváraní dočasného súboru.");
        }
    }


}



