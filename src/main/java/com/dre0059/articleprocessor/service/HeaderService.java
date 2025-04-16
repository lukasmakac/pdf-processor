package com.dre0059.articleprocessor.service;

import com.dre0059.articleprocessor.dto.CategoryDto;
import com.dre0059.articleprocessor.model.Author;
import com.dre0059.articleprocessor.model.Category;
import com.dre0059.articleprocessor.model.Dokument;
import com.dre0059.articleprocessor.repository.AuthorRepository;
import com.dre0059.articleprocessor.repository.CategoryRepository;
import com.dre0059.articleprocessor.repository.DocumentRepository;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// TODO :
//  1. VALIDATE author based on surname and first INITIAL of the firstName.
//      SOLUTION : change keys of the map on surname and first initial and compare it with surname and first initial of author
//  2. dve mená autora nesprávne ukladá (priezivsko neuloží, zistiť teda formát aby sa správne ukladalo)
//  3. ukladá viac krát meno toho istého autora, zistiť prečo !!!

@Service
public class HeaderService {

    private final DocumentRepository documentRepository;
    private final AuthorRepository authorRepository;
    private final ReferenceService referenceService;
    private final CategoryRepository categoryRepository;

    //public Dokument(String title, Integer year, String doi, String abstractText, Integer pages, String publisher) {

    private String title;
    private Integer year;
    private String doi;
    private String abstractText;
    private Integer pages;
    private String publisher;
    private List<Author> authorList = new ArrayList<>();

    private String author;

    @Autowired
    public HeaderService(DocumentRepository documentRepository, AuthorRepository authorRepository, ReferenceService referenceService, CategoryRepository categoryRepository) {
        this.documentRepository = documentRepository;
        this.authorRepository = authorRepository;
        this.referenceService = referenceService;
        this.categoryRepository = categoryRepository;
    }

    public Dokument processHeader(String header, String categoryId, File pdfFile) {
        this.title = this.parseHeaderFields(header, "title");

        if(!this.parseHeaderFields(header, "doi").equals("Not found")){
            this.doi = this.parseHeaderFields(header, "doi");
        }
        if(!this.parseHeaderFields(header, "abstract").equals("Not found")){
            this.abstractText = this.parseHeaderFields(header, "abstract");
        }
        if(!this.parseHeaderFields(header, "publisher").equals("Not found")){
            this.publisher = this.parseHeaderFields(header, "publisher");
        }

        if(!this.parseHeaderFields(header, "year").equals("Not found")){
            String yearString = this.parseHeaderFields(header, "year");
            this.year = Integer.parseInt(yearString);
        }
        if(!this.parseHeaderFields(header, "pages").equals("Not found")){
            String pagesString = this.parseHeaderFields(header, "pages");
            this.pages = Integer.parseInt(pagesString);
        }
        this.author = this.parseHeaderFields(header, "author");
        if(!this.author.equals("Not found")){
            authorList = this.saveAuthorNameAndSurname(this.author);
        }

        List<String> authorLastNames= authorList.stream().map(Author::getLastname).toList();
        System.out.println("Author list before checking duplicity: " + authorList);
        System.out.println("Author last names before checking duplicity: " + authorLastNames);

        // check duplicity of the document
        if(documentRepository.existsByTitleAndAuthorsIn(title, authorLastNames)){
            System.out.println("Document with this title and authors already exist");
            return null;
        }

        Dokument dok = new Dokument(title, year, doi, publisher, "PDF");

        List<Author> savedAuthors = authorRepository.saveAll(authorList);
        Dokument dokument = new Dokument(title, year, doi, publisher, "PDF");
        Category category = categoryRepository.getReferenceById(categoryId);

        dokument.setAuthors(savedAuthors);

        System.out.println("Category: " + category);
        dokument.setCategory(category);

        try {
            dokument.setContent(FileUtils.readFileToByteArray(pdfFile));
        } catch (IOException e) {
          System.err.println("Nepodarilo sa ulozit obsah suboru");
        }

        Dokument saved = this.documentRepository.save(dokument); // output : Optional.empty

        // set the document, which has the list of references
        referenceService.setFromDocument(saved);

        return saved;
    }

    private String parseHeaderFields(String header, String field){
        String regex = field + "\\s*=\\s*\\{([^}]*)\\}";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(header);

        if(matcher.find()){
            return matcher.group(1).trim();
        } else
            return "Not found"; // should replace for NULL ?

    }

    private List<Author> saveAuthorNameAndSurname(String author){
        // "and" divides our authors
        String[] authorNames = author.split(" and ");
        List<Author> authors = new ArrayList<>();
        List<Author> databaseAuthors = authorRepository.findAll();

        Map<String, Author> authorMap = new HashMap<>();
        for(Author existingAuthor : databaseAuthors){
            String key = existingAuthor.getLastname().toLowerCase() + "," + existingAuthor.getFirstname().toLowerCase();
            authorMap.put(key, existingAuthor);
        }

        for(String fullName : authorNames){
            String[] nameParts = fullName.split(",");

            String firstName;
            String lastName = nameParts[0].trim();
            if(nameParts.length > 2){
                // have two names
                firstName = nameParts[1].trim() + " " + nameParts[2].trim();
            } else {
                firstName = nameParts[1].trim();
            }

            String authorKey = lastName.toLowerCase() + "," + firstName.toLowerCase();

            if(authorMap.containsKey(authorKey)){
                authors.add(authorMap.get(authorKey));
                System.out.println("This author already exists in the database : " + authorKey);
            } else {
                Author newAuthor = new Author(firstName, lastName);
                authors.add(newAuthor);
                authorMap.put(authorKey, newAuthor);
            }
        }

        return authorRepository.saveAll(authors);
    }
}
