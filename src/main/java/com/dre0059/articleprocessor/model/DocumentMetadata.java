package com.dre0059.articleprocessor.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity // DBS table
@Table (name = "DOCUMENT_METADATA", uniqueConstraints = @UniqueConstraint(columnNames = "title"))
public class DocumentMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID is generated automatically
    private Long id;

    private String title;

    @ElementCollection  // pomocna tabulka authors
    private List<String> authors = new ArrayList<>();

    // needed for Hibernate for right instances in DBS
    public DocumentMetadata() {}

    public DocumentMetadata(String title, List<String> authors) {
        this.title = title;
        this.authors = authors;
    }

    public Long getId(){
        return id;
    }
    public String getTitle(){
        return title;
    }
    public List<String> getAuthors(){
        return authors;
    }
}
