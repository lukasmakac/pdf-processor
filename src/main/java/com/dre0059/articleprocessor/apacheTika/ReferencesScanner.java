package com.dre0059.articleprocessor.apacheTika;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import javax.sound.midi.Soundbank;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReferencesScanner {

    // private Vector<String> references;
    // notFound is just for TESTING
    private int notFound;
    private int notParsed;
    public int getNotFound(){
        return this.notFound;
    }
    public int getNotParsed(){ return this.notParsed; }


    public boolean findReferences(String filepath, String outputPath) {
        Tika tika = new Tika();
        String content;

        try {
            content = tika.parseToString(new File(filepath));
            String[] lines = content.split("\\r?\\n"); // split text to lines

            boolean found = false;
            StringBuilder referencesData = new StringBuilder();

            // Regular expression for "References" and variations

            String regex = "\\b[Rr][Ee][Ff][Ee][Rr][Ee][Nn][Cc][Ee][Ss]?\\b|" +
                    "\\b[Nn][Oo][Tt][Ee][Ss]?\\s+[Aa][Nn][Dd]\\s+[Rr][Ee][Ff][Ee][Rr][Ee][Nn][Cc][Ee][Ss]?\\b";

            // the most suitable for finding the references
            //String regex = "(?i)(\\d*\\s*References|References|R\\s*E\\s*F\\s*E\\s*R\\s*E\\s*N\\s*C\\s*E\\s*S|Notes\\s+and\\s+References|RE[FE]*R[EE]*N[C]*E[S]*|^\\s*REFERENCES\\s*$)";
            Pattern pattern = Pattern.compile(regex);

            for (String line : lines) {
                if (found) {
                    // TODO : FURTHER READING is in the end of the line and in the beggining of the other line (this below is not working)
                    if (line.contains("APPENDIX") || line.contains("Appendix") || line.contains("FURTHER\nREADING"))
                        break;
                    // Append lines of references
                    referencesData.append(line).append(System.lineSeparator());
                } else {
                    // Check if line contains references
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        found = true; // Reference found
                        referencesData.append(line).append(System.lineSeparator());
                    }
                }
            }

            // Save references to TXT file
            if (found) {
                try (FileWriter writer = new FileWriter(outputPath)) {
                    writer.write(referencesData.toString());
                    System.out.println("References found and saved to " + outputPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true; // Return true indicating references were found
            } else {
                System.out.println("References NOT found in file: " + filepath);
                notFound++;
                return false; // Return false indicating no references were found
            }

        } catch (IOException | TikaException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurs
        }
    }
    // parse references to vector
    public Vector<String> parseReferences(String inputReferencesPath, String outputPath) throws IOException {
        Vector<String> parsedReferences = new Vector<>();

        FileInputStream stream = null;
        try{
            stream = new FileInputStream(inputReferencesPath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found " + inputReferencesPath, e);
        }

        Scanner scanner = new Scanner(stream);
        String line = null;

        int index = 1;  // number of current reference
        boolean found = false;     // used for lines behind found reference

        while (scanner.hasNextLine()){
            // first reference (from second until the last, it will be true - because we dont wanna go to another line, we wanna stay on line with next [ i ] )
            if(!found)
                line = scanner.nextLine();

            // regex for patterns : 1 1. (1) [1] ... s* - spaces
            //String regex = "(\\(\\s*\\b" + index + "\\b\\s*\\))|(\\[\\s*\\b " + index +"\\b\\s*\\])|(\\b" + index + "\\b\\s*\\.)|(\\b" + index + "\\b)";

            String regex = "(\\(\\s*" + index + "\\s*\\))"        // Formát (index)
                    + "|(\\[\\s*" + index + "\\s*\\])"      // Formát [index]
                    + "|(\\b" + index + "\\b\\s*\\.)"       // Formát index.
                    + "|(\\b" + index + "\\b)";             // Samotné číslo index


            Pattern pattern = Pattern.compile(regex);   // regular expression
            Matcher matcher = pattern.matcher(line);    // matcher for comparing regular exrpession

            // [ i ] found, add reference
            if(matcher.find()){
                StringBuilder currReference = new StringBuilder();
                currReference.append(line); // append line which contains [ 1 ]

                index++;    // regex searches for 2 instead of 1
                //regex = "(\\(\\s*\\b" + index + "\\b\\s*\\))|(\\[\\s*\\b " + index +"\\b\\s*\\])|(\\b" + index + "\\b\\s*\\.)|(\\b" + index + "\\b\\s+)"; // [ 2 ]
                regex = "(\\(\\s*" + index + "\\s*\\))"        // Formát (index)
                        + "|(\\[\\s*" + index + "\\s*\\])"      // Formát [index]
                        + "|(\\b" + index + "\\b\\s*\\.)"       // Formát index.
                        + "|(\\b" + index + "\\b\\s+)";             // Samotné číslo index

                pattern = Pattern.compile(regex);

                //line = scanner.nextLine();
                //matcher = pattern.matcher(line);    // looking for [ 2 ] on the next line

                while(scanner.hasNextLine()){ // all lines without regex (these lines belong to first reference)
                    line = scanner.nextLine();
                    matcher = pattern.matcher(line);    // looking for [ 2 ] on the next line

                    if(matcher.find()){     // [ 2 ] was found
                        found = true;
                        break;
                    }
                    currReference.append(" ").append(line); // [ 2 ] was not found, lines belong to the first reference
                }
                //System.out.println("Match was found\n");

                parsedReferences.add(currReference.toString());     // add the whole reference [ 1 ] to vector
            }
        }

        scanner.close();

        int i = 0;
        try(FileWriter writer = new FileWriter(outputPath)) {
            for (String ref : parsedReferences) {
                i++;
                writer.write(i + ". " + ref + "\n");
                //System.out.println(i + " " + ref);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        if(!(parsedReferences.size() > 0)){
            notParsed++;
            System.out.println("References NOT parsed in file: " + inputReferencesPath);
        }
        return parsedReferences;
    }

    // pslit reference and get NAME and year out of it
    public void splitReferences(Vector<String> oneDocumentReferences){
        for(String ref : oneDocumentReferences){
            System.out.println(ref + "\n");
        }
    }
}
