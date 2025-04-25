package com.dre0059.articleprocessor.controller;

import com.dre0059.articleprocessor.model.Dokument;
import com.dre0059.articleprocessor.repository.DocumentRepository;
import com.dre0059.articleprocessor.repository.ReferenceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StatisticsController {

    private final DocumentRepository documentRepository;
    private final ReferenceRepository referenceRepository;

    public StatisticsController(DocumentRepository documentRepository,
                                ReferenceRepository referenceRepository) {
        this.documentRepository = documentRepository;
        this.referenceRepository = referenceRepository;
    }

    @GetMapping("/statistics")
    public String statistics(
            @RequestParam(value = "category", required = false, defaultValue = "") String category,
            Model model
    ) {
        // 1) Všetky dokumenty
        List<Dokument> documents = documentRepository.findAll();

        // 2) Status count
        Map<String, Long> statusCount = documents.stream()
                .collect(Collectors.groupingBy(Dokument::getStatus, Collectors.counting()));

        // 3) Category count (len tie, ktoré majú kategóriu)
        Map<String, Long> categoryCount = documents.stream()
                .filter(d -> d.getCategory() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getCategory().getName(),
                        Collectors.counting()
                ));

        // 4) Zoznam všetkých kategórií (poradie podľa vstupu do mapy)
        List<String> categories = new ArrayList<>(categoryCount.keySet());

        // 5) Vyberieme "selectedCategory":
        //    - ak prišlo z parametra, použijeme ho
        //    - inak vezmeme prvú kategóriu zo zoznamu (ak existuje)
        String selectedCategory = category.trim().isEmpty()
                ? (categories.isEmpty() ? "" : categories.get(0))
                : category.trim();

        // 6) Spočítame referencie podľa rokov pre vybranú kategóriu
        Map<Integer, Long> referenceCounts = new LinkedHashMap<>();
        if (!selectedCategory.isEmpty()) {
            List<Object[]> raw = referenceRepository
                    .countReferencesByYearForCategory(selectedCategory);
            // raw: [ [year, count], [year, count], ... ] už zoradené podľa roku v JPQL
            raw.forEach(record -> {
                Integer year = (Integer) record[0];
                Long count = (Long) record[1];
                referenceCounts.put(year, count);
            });
        }

        // 7) Pridáme všetko do modelu
        model.addAttribute("statusCount", statusCount);
        model.addAttribute("categoryCount", categoryCount);
        model.addAttribute("referenceCounts", referenceCounts);
        model.addAttribute("selectedCategory", selectedCategory);

        return "statistics";
    }
}
