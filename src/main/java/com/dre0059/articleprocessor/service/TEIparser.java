package com.dre0059.articleprocessor.service;

import com.dre0059.articleprocessor.model.DocumentMetadata;
import com.dre0059.articleprocessor.model.Reference;
import com.dre0059.articleprocessor.repository.DocumentRepository;
import com.dre0059.articleprocessor.repository.ReferenceRepository;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class TEIparser {


    private final ReferenceRepository referenceRepository;
    private final DocumentRepository documentRepository;

    public TEIparser(ReferenceRepository referenceRepository, DocumentRepository documentRepository) {
        this.referenceRepository = referenceRepository;
        this.documentRepository = documentRepository;
    }

    public void parseAndSaveToDB(String xmlContent, DocumentMetadata document) {
        try {
            List<Reference> references = parseReferencesFromXML(xmlContent, document);

            if (!references.isEmpty()) {
                referenceRepository.saveAll(references);
                System.out.println("References successfully saved to DB");
            } else {
                System.out.println("No valid references found in XML.");
            }
        } catch (Exception e) {
            System.err.println("Error parsing references: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Reference> parseReferencesFromXML(String xmlContent, DocumentMetadata document) {
        List<Reference> references = new ArrayList<>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xmlContent)));

            // <biblStruct> - bibliographical information (consists of all needed information)
            NodeList biblStructs = doc.getElementsByTagNameNS("*", "biblStruct");

            for (int i = 0; i < biblStructs.getLength(); i++) {
                Element bibl = (Element) biblStructs.item(i);

                String title = getTagValueNS("title", bibl, "Unknown Title");
                String publisher = getTagValueNS("publisher", bibl, "Unknown Publisher");
                String year = getTagValueNS("year", bibl, "Unknown Year");

                List<String> authors = new ArrayList<>();
                NodeList authorNodes = bibl.getElementsByTagNameNS("*", "author");

                for (int j = 0; j < authorNodes.getLength(); j++) {
                    Element authorElement = (Element) authorNodes.item(j);
                    Element persName = (Element) authorElement.getElementsByTagNameNS("*", "persName");

                    if (persName != null) {
                        String forename = getTagValueNS("forename", persName, "");
                        String surname = getTagValueNS("surname", persName, "");
                        if (!forename.isEmpty() || !surname.isEmpty()) {
                            authors.add(forename + " " + surname);
                        }
                    }

                }
                references.add(new Document(title, year, doi, abstractText, pages, publisher));

            }
        } catch (Exception e) {
            System.err.println("Failed to parse references XML: " + e.getMessage());
            e.printStackTrace();
        }

        return references;
    }

    private static String getTagValueNS(String tagName, Element element, String defaultValue) {
        NodeList nodeList = element.getElementsByTagNameNS("*", tagName);
        return (nodeList.getLength() > 0) ? nodeList.item(0).getTextContent().trim() : defaultValue;
    }
}
