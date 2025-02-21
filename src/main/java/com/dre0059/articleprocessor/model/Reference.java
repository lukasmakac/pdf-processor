package com.dre0059.articleprocessor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "references")
public class Reference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // number or letters in reference list
    private String orderNumber;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fromDocument")
    private Dokument fromDocument;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "toDocument")
    private Dokument toDocument;

    public Reference() {}
    public Reference(/*String orderNumber,*/ Dokument fromDocument, Dokument toDocument) {
        //this.orderNumber = orderNumber;
        this.fromDocument = fromDocument;
        this.toDocument = toDocument;
    }

    public String getOrderNumber() { return orderNumber; }
    public Dokument getFromDocument() { return fromDocument; }
    public Dokument getToDocument() { return toDocument; }
    public Long getId() { return id; }

    public void setFromDocument(Dokument fromDocument) { this.fromDocument = fromDocument; }
    public void setToDocument(Dokument toDocument) { this.toDocument = toDocument; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
}
