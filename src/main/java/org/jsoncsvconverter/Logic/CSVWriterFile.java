package org.jsoncsvconverter.Logic;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVWriterFile {
    private final String[] headers;

    public CSVWriterFile(String[] headers) {
        if (headers == null || headers.length == 0) {
            throw new IllegalArgumentException("Headers cannot be null or empty.");
        }
        this.headers = headers;
    }

    public void createNewCSVFile(String filePathOutput) {
        File file = new File(filePathOutput);

        try {
            // Ensure parent directories exist
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw new IOException("Failed to create parent directories: " + parentDir.getAbsolutePath());
                }
            }

            // Try-with-resources auto-closes writer
            try (FileWriter outputfile = new FileWriter(file);
                 CSVWriter writer = new CSVWriter(outputfile)) {

                writer.writeNext(headers);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error while creating CSV file: " + filePathOutput, e);
        }
    }
}
