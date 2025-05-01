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

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

@Setter
@Getter
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

    @OneToMany(mappedBy = "toDocument", cascade = CascadeType.ALL)
    private List<Reference> backReferences = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "document_author",
            joinColumns = @JoinColumn(name = "ID_document"),
            inverseJoinColumns = @JoinColumn(name = "ID_author")
    )
    private List<Author> authors = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "document_tag",
            joinColumns = @JoinColumn(name = "document_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();



    public Dokument() {}

    public Dokument(String title, Integer year, String doi,String publisher, String status) {
        this.title = title;
        this.publicationYear = year;
        this.doi = doi;
        this.publisher = publisher;
        this.status = status;
    }

    public Integer getYear(){
        return publicationYear;
    }
}
