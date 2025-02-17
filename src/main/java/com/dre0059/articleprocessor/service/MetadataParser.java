package com.dre0059.articleprocessor.service;

import com.dre0059.articleprocessor.model.*;
import com.dre0059.articleprocessor.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MetadataParser {

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    public MetadataParser(DocumentRepository documentRepository, AuthorRepository authorRepository) {
        this.documentRepository = documentRepository;
        this.authorRepository = authorRepository;
    }

    public Dokument parseBibTeX(String bibtexString) {
        // Regular expression pre získanie hodnôt z BibTeX formátu
        Pattern pattern = Pattern.compile("@.*?\\{.*?,\\s*author\\s*=\\s*\\{(.*?)\\},\\s*title\\s*=\\s*\\{(.*?)\\},\\s*doi\\s*=\\s*\\{(.*?)\\},\\s*abstract\\s*=\\s*\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(bibtexString);

        if (matcher.find()) {
            String authorsRaw = matcher.group(1);
            String title = matcher.group(2);
            String doi = matcher.group(3);
            String abstractText = matcher.group(4);

            List<Author> authors = parseAuthors(authorsRaw);

            Dokument document = new Dokument(title, null, doi, null, null);
            document.setAuthors(authors);

            //documentRepository.save(document);

            for (Author author : authors) {
                authorRepository.save(author);
            }

            return document;
        }
        return null;
    }

    private List<Author> parseAuthors(String authorsRaw) {
        List<Author> authors = new ArrayList<>();
        String[] authorNames = authorsRaw.split(" and ");
        for (String fullName : authorNames) {
            String[] nameParts = fullName.trim().split("\\s+", 2);
            if (nameParts.length == 2) {
                authors.add(new Author(nameParts[1], nameParts[0])); // Priezvisko, Meno
            } else {
                authors.add(new Author(nameParts[0], "")); // Ak meno nemá priezvisko
            }
        }
        return authors;
    }
}
