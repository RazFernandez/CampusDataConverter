package org.jsoncsvconverter;
import java.io.FileReader;
import java.io.IOException;

// Entry point for JSON converter to CSV application

public class Main {
    public static void main(String[] args) {
        JsonFileReader reader = new JsonFileReader("text.json");
//        reader.printFileContent();
        String jsonData = reader.getJsonString();
        System.out.println(jsonData);
    }
}
