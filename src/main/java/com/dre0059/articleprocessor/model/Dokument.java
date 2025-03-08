package com.dre0059.articleprocessor.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Cascade;


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

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Lob
    @Column
    private byte[] abstractText;

    @Lob
    @Column
    private byte[] content;

    private String status; // if the value is PDF - the whole document was uploaded, otherwise the document was just mentioned in references
    private String publisher;
    private String target; // http link


    @OneToMany(mappedBy = "fromDocument", cascade = CascadeType.ALL)
    private List<Reference> references = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "document_author",
            joinColumns = @JoinColumn(name = "ID_document"),
            inverseJoinColumns = @JoinColumn(name = "ID_author")
    )

    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Author> authors = new ArrayList<>();

    public Dokument() {}

    public Dokument(String title, Integer year, String doi,String publisher, String status) {
        this.title = title;
        this.publicationYear = year;
        this.doi = doi;
        this.publisher = publisher;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Integer getYear() { return publicationYear; }
    public String getDoi() { return doi; }
    //public String getAbstractText() { return abstractText; }
    public String getStatus() { return status; }
    public String getPublisher() { return publisher; }
    public List<Reference> getReferences() { return references; }
    public List<Author> getAuthors() { return authors; }
    public byte[] getAbstractText() { return abstractText; }
    public byte[] getContent() { return content; }

    public void setAuthors(List<Author> authors) { this.authors = authors; }
    public void setTitle(String title) { this.title = title; }
    public void setTarget(String target) { this.target = target; }
    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }
    public void setDoi(String doi) {
        this.doi = doi;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public void setReferences(List<Reference> references) {
        this.references = references;
    }
    public void setCategory(Category category) { this.category = category;}
    public void setAbstractText(byte[] abstractText) { this.abstractText = abstractText; }
    public void setContent(byte[] content) { this.content = content; }

    public String getTarget() {
        return target;
    }
}
