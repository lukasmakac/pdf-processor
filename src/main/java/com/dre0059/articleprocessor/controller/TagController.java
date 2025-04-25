package com.dre0059.articleprocessor.controller;

import com.dre0059.articleprocessor.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TagController {
    //@Autowired
    private final TagRepository tagRepository;

   public TagController(TagRepository tagRepository) {
       this.tagRepository = tagRepository;
   }

    @GetMapping("/tags")
    public List<Map<String, String>> getTags(@RequestParam("term") String term) {
        return tagRepository.findByTitleContainingIgnoreCase(term).stream()
                .map(tag -> Map.of("id", tag.getTitle(), "text", tag.getTitle())) // použijeme title ako ID pre select2
                .collect(Collectors.toList());

    }
}
