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

    private String firstName;
    private String lastName;

    @ManyToMany(mappedBy = "authors")
    private List<Dokument> documents = new ArrayList<>();

    public Author(){}
    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() { return id; }
    public String getFirstname() { return firstName; }
    public String getLastname() { return lastName; }
    public List<Dokument> getDocuments() { return documents; }

    public void setFirstname(String name) { this.firstName = name; }
    public void setLastname(String surname) { this.lastName = surname; }
    public void setDocuments(List<Dokument> documents) { this.documents = documents; }
}
