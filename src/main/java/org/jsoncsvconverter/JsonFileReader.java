package org.jsoncsvconverter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JsonFileReader {
    private final String filename;

    // Constructor that only saves the filename
    public JsonFileReader(String filename) {
        if (!filename.endsWith(".json")) {
            throw new IllegalArgumentException("Error: File must have a .json extension");
        }
        this.filename = filename;
        System.out.println("JsonFileReader created for file: " + filename);
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
}
