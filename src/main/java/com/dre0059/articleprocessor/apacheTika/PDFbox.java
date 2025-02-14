package com.dre0059.articleprocessor.apacheTika;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.lowagie.text.html.HtmlTagMap.isTitle;

@Component
public class PDFbox {
    private int countTXT = 0;
    public int getCountTXT(){ return this.countTXT; }

    public void toTXT(String inputPDFpath, String outputTXTpath){
        File pdfFile = new File(inputPDFpath);

        try(PDDocument document = PDDocument.load(pdfFile)){
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String extractedText = pdfStripper.getText(document);

            //String[] lines = extractedText.split("\n");
            //StringBuilder contentWithHeaders = new StringBuilder();

            try (FileWriter writer = new FileWriter(outputTXTpath)) {
                writer.write(extractedText);
                System.out.println("File was sucessfully saved to : " + outputTXTpath);
                countTXT++;
            } catch (IOException e) {
                System.err.println("FAILURE - file was not saved : " + e.getMessage());
            }

        } catch (IOException ex) {
            System.err.println("FAILURE - Problem kin reading file : " + ex.getMessage());
        }
    }
}
