package com.dre0059.articleprocessor.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;

    @ManyToMany(mappedBy = "authors")
    private List<Dokument> documents = new ArrayList<Dokument>();

    public Author(){}
    public Author(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public List<Dokument> getDocuments() { return documents; }

    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setDocuments(List<Dokument> documents) { this.documents = documents; }
}
