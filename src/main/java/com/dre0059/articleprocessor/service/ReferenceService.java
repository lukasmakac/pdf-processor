package com.dre0059.articleprocessor.service;

import com.dre0059.articleprocessor.model.*;
import com.dre0059.articleprocessor.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.*;

// TODO :
//  1. uložiť prepojenie toDocument a fromDocument do tabuľky referencie
//  2. vytiahnuť orderNumber z referencie (toto riešiť cez GROBID)
//  3. aktuálne sa mi toDocument ukladá vždy ako nový.. ja ho potrebujem vyhľadať a na základe toho uložiť alebo prepojiť
//  4. uložiť záznam do tabuľky references
//  5. ak už bolo PDF raz uložené, uloží sa mi "null" článok, prepojený s autormi - VYRIESIT


@Service
public class ReferenceService {

    private final DocumentRepository documentRepository;
    private final AuthorRepository authorRepository;
    private final ReferenceRepository referenceRepository;

    private Dokument fromDocument;
    private Dokument toDocument;

    @Autowired
    public ReferenceService(DocumentRepository documentRepository, AuthorRepository authorRepository, ReferenceRepository referenceRepository) {
        this.documentRepository = documentRepository;
        this.authorRepository = authorRepository;
        this.referenceRepository = referenceRepository;
    }

    public void setFromDocument(Dokument fromDocument) {
        this.fromDocument = fromDocument;
        System.out.println("From document: " + fromDocument.getTitle());
    }

    public void setToDocument(Dokument doc){
        this.toDocument = doc;
    }

    public void extractReferences(String xmlTeiReferences) {
        List<Author> databaseAuthors = this.authorRepository.findAll();
        Map<String, Author> authorMap = new HashMap<>();

        for (Author author : databaseAuthors) {
            String key = author.getLastname().toLowerCase() + "," + author.getFirstname().toLowerCase();
            authorMap.put(key, author);
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            InputSource inputSource = new InputSource(new StringReader(xmlTeiReferences));
            Document doc = builder.parse(inputSource);

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(new TEINamespaceContext());

            NodeList biblNodes = (NodeList) xpath.evaluate("//tei:biblStruct", doc, XPathConstants.NODESET);

            // for each reference
            for (int i = 0; i < biblNodes.getLength(); i++) {
                Node biblNode = biblNodes.item(i);
                Dokument referencedDocument = new Dokument();

                // Extract title - toDocument
                String title = xpath.evaluate(".//tei:title[@level='m' or @level='a']", biblNode);
                referencedDocument.setTitle(title);

                // Extract year of publication
                String yearStr = xpath.evaluate(".//tei:date[@type='published']/@when", biblNode);
                if (yearStr != null && !yearStr.isEmpty()) {
                    try {
                        referencedDocument.setPublicationYear(Integer.valueOf(yearStr));
                    } catch (NumberFormatException e) {
                        System.out.println("Error during converting year." + yearStr);
                    }
                }

                // Extract publisher
                String publisher = xpath.evaluate(".//tei:publisher", biblNode);
                referencedDocument.setPublisher(publisher);

                // Extract authors
                NodeList authorNodes = (NodeList) xpath.evaluate(".//tei:author/tei:persName", biblNode, XPathConstants.NODESET);
                List<Author> authors = new ArrayList<>();

                // each author in a reference
                for (int j = 0; j < authorNodes.getLength(); j++) {
                    Node authorNode = authorNodes.item(j);

                    String firstName = xpath.evaluate(".//tei:forename", authorNode);
                    String lastName = xpath.evaluate(".//tei:surname", authorNode);

                    String authorKey = lastName.toLowerCase() + "," + firstName.toLowerCase();

                    if (authorMap.containsKey(authorKey)) {
                        authors.add(authorMap.get(authorKey));
                        System.out.println("Author: " + authorMap.get(authorKey) + " already exists in database.");
                    } else {
                        Author newAuthor = new Author(firstName, lastName);
                        authors.add(newAuthor);
                        authorMap.put(authorKey, newAuthor);
                    }
                }
                referencedDocument.setAuthors(authors);

                List<String> authorLastNames= authors.stream().map(Author::getLastname).toList();

                // check if document exists in dbs
                boolean exists = documentRepository.existsByTitleAndAuthorsIn(title, authorLastNames);

                // check whether the document is already saved in DBS
                if(exists){
                    System.out.println("Document with this title and authors already exist");

                    // vyhľadaj dokument podľa TITLE alebo AUTORA a nastav ho ako toDokument

                    referencedDocument = documentRepository.findByTitleAndAuthorsIn(title, authorLastNames)
                            .orElseThrow(() -> new IllegalStateException("Document should exist but was NOT FOUND."));

                    this.toDocument = referencedDocument;
                    System.out.println("Document already exists in database : " + referencedDocument.getTitle() + " with ID : " + referencedDocument.getId());
                } else {
                    // create new dokument
                    this.setToDocument(referencedDocument);
                    this.documentRepository.save(toDocument);
                    this.authorRepository.saveAll(authors);
                }

                Reference reference = new Reference("[i]", fromDocument, toDocument);
                referenceRepository.save(reference);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }


