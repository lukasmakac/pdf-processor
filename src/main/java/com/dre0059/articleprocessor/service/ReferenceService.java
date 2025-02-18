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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO :
//  1. uložiť prepojenie toDocument a fromDocument do tabuľky referencie
//  2. vytiahnuť orderNumber z referencie (toto riešiť cez GROBID)
//  3. aktuálne sa mi toDocument ukladá vždy ako nový.. ja ho potrebujem vyhľadať a na základe toho uložiť alebo prepojiť

@Service
public class ReferenceService {

    private final DocumentRepository documentRepository;
    private final AuthorRepository authorRepository;

    //private String title;
    private Integer year;
    private String doi;
    private String abstractText;
    private Integer pages;
    private String publisher;

    private String author;
    private List<Author> authorList = new ArrayList<>();

    private Dokument fromDocument;
    private Dokument toDocument;


    @Autowired
    public ReferenceService(DocumentRepository documentRepository, AuthorRepository authorRepository) {
        this.documentRepository = documentRepository;
        this.authorRepository = authorRepository;
    }

    public void setFromDocument(Dokument fromDocument) {
        this.fromDocument = fromDocument;
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

            for (int i = 0; i < biblNodes.getLength(); i++) {
                Node biblNode = biblNodes.item(i);
                Dokument toDokument = new Dokument();

                // Extract title - toDocument
                String title = xpath.evaluate(".//tei:title[@level='m' or @level='a']", biblNode);
                toDokument.setTitle(title);

                // Extract authors
                NodeList authorNodes = (NodeList) xpath.evaluate(".//tei:author/tei:persName", biblNode, XPathConstants.NODESET);
                List<Author> authors = new ArrayList<>();

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

                    toDokument.setAuthors(authors);

                    // Extract year of publication
                    String yearStr = xpath.evaluate(".//tei:date[@type='published']/@when", biblNode);
                    if (yearStr != null && !yearStr.isEmpty()) {
                        try {
                            toDokument.setPublicationYear(Integer.valueOf(yearStr));
                        } catch (NumberFormatException e) {
                            System.out.println("Error during converting year." + yearStr);
                        }
                    }

                    // Extract publisher
                    String publisher = xpath.evaluate(".//tei:publisher", biblNode);
                    toDokument.setPublisher(publisher);

                    this.documentRepository.save(toDokument);
                    this.authorRepository.saveAll(authors);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }


