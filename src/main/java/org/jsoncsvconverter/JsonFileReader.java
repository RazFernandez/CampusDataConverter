package org.jsoncsvconverter;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class JsonFileReader {
    private FileReader fileReader;
    private String filename;
    private String jsonString;

    // Constructor that always requires a filename
    public JsonFileReader(String filename) {
        if (!filename.endsWith(".json")) {
            throw new IllegalArgumentException("Error: File must have a .json extension");
        }
        this.filename = filename;
        try {
            fileReader = new FileReader(filename);
            System.out.println("File opened successfully: " + filename);
        } catch (IOException e) {
            System.out.println("Error opening file: " + e.getMessage());
        }
    }

    // Method to save the read data from the json file into a variable string
    public String getJsonString() {
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return content.toString();
    }

    // Method to print the content of the file
    public void printFileContent() {
        if (fileReader == null) {
            System.out.println("No file to read.");
            return;
        }

        try (BufferedReader br = new BufferedReader(fileReader)) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Getter (optional, so you can use fileReader outside if needed)
    public FileReader getFileReader() {
        return fileReader;
    }
}