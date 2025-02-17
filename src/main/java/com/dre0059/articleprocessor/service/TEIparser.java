package com.dre0059.articleprocessor.service;

import com.dre0059.articleprocessor.model.Author;
import com.dre0059.articleprocessor.model.Dokument;
import com.dre0059.articleprocessor.model.Reference;
import com.dre0059.articleprocessor.service.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ReferenceService referenceService;

    @Autowired
    private DocumentService documentService;

    public void processReferences(String xmlContent, Dokument parentDocument){
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));

            NodeList biblioEntries = doc.getElementsByTagName("biblStruct");

            for(int i = 0; i < biblioEntries.getLength(); i++){
                Element biblEntry = (Element) biblioEntries.item(i);
                String title = biblEntry.getElementsByTagName("title").item(0).getTextContent();
                String authorSurname = biblEntry.getElementsByTagName("surname").item(0).getTextContent();

                Dokument referencedDocument = new Dokument();
                referencedDocument.setTitle(title);

                Author author = new Author();
                author.setLastname(authorSurname);
                referencedDocument.setAuthors(List.of(author));

                Dokument savedReferencedDocument = documentService.saveDocument(referencedDocument);

                // save Dokument
                Reference reference = new Reference();
                reference.setFromDocument(parentDocument);
                reference.setToDocument(savedReferencedDocument);
                referenceService.saveReference(reference);



            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}