package com.dre0059.articleprocessor.controller;

import com.dre0059.articleprocessor.model.Dokument;
import com.dre0059.articleprocessor.repository.DocumentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StatisticsController {

    private final DocumentRepository documentRepository;

    public StatisticsController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        List<Dokument> documents = documentRepository.findAll();

        // Status count
        Map<String, Long> statusCount = documents.stream()
                .collect(Collectors.groupingBy(Dokument::getStatus, Collectors.counting()));

        // odfiltruj PDF ktoré nemajú kategoriu
        Map<String, Long> categoryCount = documents.stream()
                .filter(doc -> doc.getCategory() != null) // 💡 Tu je fix
                .collect(Collectors.groupingBy(
                        doc -> doc.getCategory().getName(),
                        Collectors.counting()
                ));

        model.addAttribute("statusCount", statusCount);
        model.addAttribute("categoryCount", categoryCount);

        return "statistics";
    }
}
