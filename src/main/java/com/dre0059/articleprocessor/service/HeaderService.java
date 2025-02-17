package com.dre0059.articleprocessor.service;

import com.dre0059.articleprocessor.model.Author;
import com.dre0059.articleprocessor.model.Dokument;
import com.dre0059.articleprocessor.repository.AuthorRepository;
import com.dre0059.articleprocessor.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HeaderService {

    private final DocumentRepository documentRepository;
    private final AuthorRepository authorRepository;

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
    public HeaderService(DocumentRepository documentRepository, AuthorRepository authorRepository) {
        this.documentRepository = documentRepository;
        this.authorRepository = authorRepository;
    }

    public void processHeader(String header){
        this.title = this.parseHeaderFields(header, "title");
        this.doi = this.parseHeaderFields(header, "doi");
        this.abstractText = this.parseHeaderFields(header, "abstract");
        this.publisher = this.parseHeaderFields(header, "publisher");

        if(this.parseHeaderFields(header, "year").equals("Not found")){
            this.year = 0;
        }
        if(this.parseHeaderFields(header, "pages").equals("Not found")){
            this.pages = 0;
        }

        this.author = this.parseHeaderFields(header, "author");
        if(!this.author.equals("Not found")){
            authorList = this.saveAuthorNameAndSurname(this.author);
        }

        authorRepository.saveAll(authorList);

        // check duplicity of the document
        if(documentRepository.existsByTitleAndAuthorsIn(title, authorList)){
            System.out.println("Document with this title and authors already exist");
            return;
        }


        Dokument dokument = new Dokument(title, year, doi, pages, publisher);
        dokument.setAuthors(authorList);

        this.documentRepository.save(dokument);
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
        // "and" divide our authors
        String[] authorNames = author.split(" and ");

        List<Author> authors = new ArrayList<>();

        System.out.println("Author is : \n" + author);

        for(String fullName : authorNames){
            String[] nameParts = fullName.split(",");

            String firstName;
            String lastName = nameParts[1];
            if(nameParts.length > 2){
                // have two names
                firstName = nameParts[0] + " " + nameParts[2];
            } else {
                firstName = nameParts[0];
            }

            // check if author already exists
            Optional<Author> existingAuthor = authorRepository.findByFullName(lastName, firstName);
            if (existingAuthor.isPresent()) {
                authors.add(existingAuthor.get());
            } else {
                Author newAuthor = new Author(lastName, firstName);
                authors.add(newAuthor);
            }

            //authors.add(new Author(lastName, firstName));
        }

        return authors;
    }
}
