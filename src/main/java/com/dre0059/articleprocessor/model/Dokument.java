package com.dre0059.articleprocessor.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "documents")
public class Dokument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "publication_year")
    private Integer publicationYear;
    private String doi;

    //@Column(name = "abstractText", columnDefinition = "TEXT")
    //private String abstractText;

    private Integer pages;
    private String publisher;

    @OneToMany(mappedBy = "fromDocument", cascade = CascadeType.ALL)
    private List<Reference> references = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "document_author",
            joinColumns = @JoinColumn(name = "ID_document"),
            inverseJoinColumns = @JoinColumn(name = "ID_author")
    )

    private List<Author> authors = new ArrayList<>();

    public Dokument() {}

    public Dokument(String title, Integer year, String doi, /*String abstractText,*/ Integer pages, String publisher) {
        this.title = title;
        this.publicationYear = year;
        this.doi = doi;
        //this.abstractText = abstractText;
        this.pages = pages;
        this.publisher = publisher;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Integer getYear() { return publicationYear; }
    public String getDoi() { return doi; }
    //public String getAbstractText() { return abstractText; }
    public Integer getPages() { return pages; }
    public String getPublisher() { return publisher; }
    public List<Reference> getReferences() { return references; }
    public List<Author> getAuthors() { return authors; }


    public void setAuthors(List<Author> authors) { this.authors = authors; }
    public void setTitle(String title) { this.title = title; }
}
