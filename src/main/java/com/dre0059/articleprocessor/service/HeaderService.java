package com.dre0059.articleprocessor.service;

import com.dre0059.articleprocessor.model.Author;
import com.dre0059.articleprocessor.model.Dokument;
import com.dre0059.articleprocessor.repository.AuthorRepository;
import com.dre0059.articleprocessor.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO :
//  1. VALIDATE author based on surname and first INITIAL of the firstName.
//      SOLUTION : change keys of the map on surname and first initial and compare it with surname and first initial of author
//  2.

@Service
public class HeaderService {

    private final DocumentRepository documentRepository;
    private final AuthorRepository authorRepository;
    private final ReferenceService referenceService;

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
    public HeaderService(DocumentRepository documentRepository, AuthorRepository authorRepository, ReferenceService referenceService) {
        this.documentRepository = documentRepository;
        this.authorRepository = authorRepository;
        this.referenceService = referenceService;
    }

    public void processHeader(String header){
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

        // tu dostávam error :
        boolean headerDuplicity = documentRepository.existsByTitleAndAuthorsIn(title, authorLastNames);

        // check duplicity of the document
        if(headerDuplicity){
            System.out.println("Document with this title and authors already exist");
            return;
        }

        List<Author> savedAuthors = authorRepository.saveAll(authorList);
        Dokument dokument = new Dokument(title, year, doi, pages, publisher);

        dokument.setAuthors(savedAuthors);
        this.documentRepository.save(dokument);

        // set the document, which has the list of references
        referenceService.setFromDocument(dokument);

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
            String lastName = nameParts[1].trim();
            if(nameParts.length > 2){
                // have two names
                firstName = nameParts[0].trim() + " " + nameParts[2].trim();
            } else {
                firstName = nameParts[0].trim();
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
