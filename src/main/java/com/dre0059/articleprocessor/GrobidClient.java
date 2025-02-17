package com.dre0059.articleprocessor;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.io.FileSystemResource;
import reactor.core.publisher.Mono;

import java.io.File;


@Service
public class GrobidClient {
    private final WebClient webClient;

    public GrobidClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://158.196.98.65:8080")   // URL kde beží GROBID server
                .build();
    }

    // get METADATA of the file
    public String processHeader(File pdfFile){    // Mono - vráti jeden string, výsledok je JSON
        return webClient.post()
                .uri("/api/processHeaderDocument")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("input", new FileSystemResource(pdfFile)))
                .attribute("consolidateHeader", 1)  // Možnosť na zjednotenie hlavičky
                .attribute("includeRawAffiliations", 1)    // Prípadne pridať ďalšie parametre, ak Grobid podporuje takéto rozšírenie
                .attribute("includeRawCopyrights", 1)    // Prípadne pridať ďalšie parametre, ak Grobid podporuje takéto rozšírenie
                //.attribute("includeReferences", 1) // Možnosť pridať aj referencie priamo do hlavičky
                .retrieve()
                .bodyToMono(String.class)
                .block();   // returns String instead of Mono<String>
    }

    public String processFullMetadata(File pdfFile) {
        return webClient.post()
                .uri("/api/processFullMetadata")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("input", new FileSystemResource(pdfFile)))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


    // spracuje REFERENCIE z PDF
    public String processReferences(File pdfFile){
        return webClient.post()
                .uri("/api/processReferences")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("input", new FileSystemResource(pdfFile)))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}