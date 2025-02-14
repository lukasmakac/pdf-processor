package com.dre0059.articleprocessor.model;

import jakarta.persistence.*;

import javax.print.Doc;
import java.util.List;

@Entity
@Table(name = "references")
public class Reference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // number or letters in reference list
    private String orderNumber;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_fromDocument")
    private Document fromDocument;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_toDocument")
    private Document toDocument;

    public Reference() {}
    public Reference(String orderNumber, Document fromDocument, Document toDocument) {
        this.orderNumber = orderNumber;
        this.fromDocument = fromDocument;
        this.toDocument = toDocument;
    }

    public String getOrderNumber() { return orderNumber; }
    public Document getFromDocument() { return fromDocument; }
    public Document getToDocument() { return toDocument; }
    public Long getId() { return id; }

    public void setFromDocument(Document fromDocument) { this.fromDocument = fromDocument; }
    public void setToDocument(Document toDocument) { this.toDocument = toDocument; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
}
