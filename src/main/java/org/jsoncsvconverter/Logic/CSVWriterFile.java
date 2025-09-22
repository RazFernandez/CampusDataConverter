package org.jsoncsvconverter.Logic;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriterFile {
    private final String[] headers;

    public CSVWriterFile(String[] headers) {
        if (headers == null || headers.length == 0) {
            throw new IllegalArgumentException("Headers cannot be null or empty.");
        }
        this.headers = headers;
    }

    /**
     * Creates a new CSV file and writes only the header row.
     */
    public void createNewCSVFile(String filePathOutput) {
        File file = new File(filePathOutput);

        try {
            ensureParentDir(file);

            try (FileWriter outputfile = new FileWriter(file);
                 CSVWriter writer = new CSVWriter(outputfile)) {

                writer.writeNext(headers);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error while creating CSV file: " + filePathOutput, e);
        }
    }

    /**
     * Creates a new CSV file and writes full data (headers + rows).
     * Each row is a String[] inside the List.
     */
    public void createCSVWithData(String filePathOutput, List<String[]> rows) {
        File file = new File(filePathOutput);

        try {
            ensureParentDir(file);

            try (FileWriter outputfile = new FileWriter(file);
                 CSVWriter writer = new CSVWriter(outputfile)) {

                // Write headers first
                writer.writeNext(headers);

                // Write all rows
                if (rows != null && !rows.isEmpty()) {
                    writer.writeAll(rows);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error while creating CSV file with data: " + filePathOutput, e);
        }
    }

    /**
     * Ensures parent directories exist for the given file.
     */
    private void ensureParentDir(File file) throws IOException {
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create parent directories: " + parentDir.getAbsolutePath());
            }
        }
    }
}