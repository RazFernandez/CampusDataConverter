package org.jsoncsvconverter.Logic;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * A utility class for creating and writing CSV files using OpenCSV library.
 * This class provides a convenient interface to write structured data to CSV format
 * with proper header management and directory creation capabilities.
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Header validation and management for consistent CSV structure</li>
 *   <li>Automatic parent directory creation when needed</li>
 *   <li>Support for header-only files and full data files</li>
 *   <li>Proper resource management with try-with-resources</li>
 *   <li>Comprehensive error handling with meaningful exception messages</li>
 * </ul>
 *
 * <p>Usage examples:</p>
 * <pre>{@code
 * // Create a CSV writer with headers
 * String[] headers = {"Name", "Age", "Email"};
 * CSVWriterFile csvWriter = new CSVWriterFile(headers);
 *
 * // Create header-only file
 * csvWriter.createNewCSVFile("output/template.csv");
 *
 * // Create file with data
 * List<String[]> rows = Arrays.asList(
 *     new String[]{"John", "25", "john@email.com"},
 *     new String[]{"Jane", "30", "jane@email.com"}
 * );
 * csvWriter.createCSVWithData("output/data.csv", rows);
 * }</pre>
 *
 * @author Miguel Fernandez
 * @version 1.0
 * @since 1.0
 *
 * @see CSVWriter
 * @see FileWriter
 */
public class CSVWriterFile {

    /** The column headers for the CSV file */
    private final String[] headers;

    /**
     * Constructs a new CSVWriterFile with the specified column headers.
     * The headers define the structure of the CSV file and will be written as the first row
     * in all CSV files created by this instance.
     *
     * <p>Headers are validated during construction to ensure they are not null or empty,
     * preventing runtime errors during file creation.</p>
     *
     * @param headers An array of column header names. Must not be null or empty.
     * @throws IllegalArgumentException if headers is null or has zero length
     *
     * @see #createNewCSVFile(String)
     * @see #createCSVWithData(String, List)
     */
    public CSVWriterFile(String[] headers) {
        if (headers == null || headers.length == 0) {
            throw new IllegalArgumentException("Headers cannot be null or empty.");
        }
        this.headers = headers;
    }

    /**
     * Creates a new CSV file containing only the header row.
     * This method is useful for creating template files or when data will be added later
     * through other means. The file will contain only the column headers specified during
     * construction.
     *
     * <p>The method automatically:</p>
     * <ul>
     *   <li>Creates parent directories if they don't exist</li>
     *   <li>Overwrites existing files at the specified path</li>
     *   <li>Properly closes all file resources using try-with-resources</li>
     * </ul>
     *
     * @param filePathOutput The complete path where the CSV file should be created,
     *                      including filename and .csv extension
     * @throws RuntimeException if an I/O error occurs during file creation, directory creation,
     *                         or writing operations. The original IOException is wrapped and
     *                         includes the file path for easier debugging.
     *
     * @see #createCSVWithData(String, List)
     * @see #ensureParentDir(File)
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
     * Creates a new CSV file with complete data including headers and all data rows.
     * This is the primary method for creating fully populated CSV files. The file will
     * contain the headers as the first row followed by all provided data rows.
     *
     * <p>Data handling:</p>
     * <ul>
     *   <li>Headers are always written first</li>
     *   <li>Each String[] in the rows list becomes one CSV row</li>
     *   <li>Null or empty row lists are handled gracefully (header-only file created)</li>
     *   <li>Row data doesn't need to match header count exactly (OpenCSV handles this)</li>
     * </ul>
     *
     * <p>The method automatically:</p>
     * <ul>
     *   <li>Creates parent directories if they don't exist</li>
     *   <li>Overwrites existing files at the specified path</li>
     *   <li>Properly closes all file resources using try-with-resources</li>
     *   <li>Uses OpenCSV's writeAll() for efficient bulk writing</li>
     * </ul>
     *
     * @param filePathOutput The complete path where the CSV file should be created,
     *                      including filename and .csv extension
     * @param rows A list of String arrays, where each array represents one data row.
     *            Can be null or empty, in which case only headers will be written.
     * @throws RuntimeException if an I/O error occurs during file creation, directory creation,
     *                         or writing operations. The original IOException is wrapped and
     *                         includes the file path for easier debugging.
     *
     * @see #createNewCSVFile(String)
     * @see #ensureParentDir(File)
     * @see CSVWriter#writeAll(List)
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
     * Ensures that all parent directories exist for the given file path.
     * This utility method creates any missing directories in the file path hierarchy,
     * allowing files to be created in nested directory structures that may not yet exist.
     *
     * <p>Behavior:</p>
     * <ul>
     *   <li>Does nothing if parent directories already exist</li>
     *   <li>Creates the entire directory hierarchy if needed using {@link File#mkdirs()}</li>
     *   <li>Handles cases where the file is in the current directory (no parent)</li>
     * </ul>
     *
     * <p><strong>Example:</strong> For file path "/home/user/data/output/file.csv",
     * this method ensures that "/home/user/data/output/" exists.</p>
     *
     * @param file The File object for which parent directories should be ensured
     * @throws IOException if the parent directories cannot be created due to:
     *                    <ul>
     *                      <li>Insufficient permissions</li>
     *                      <li>Disk space issues</li>
     *                      <li>Invalid path characters</li>
     *                      <li>Other file system errors</li>
     *                    </ul>
     *
     * @see File#getParentFile()
     * @see File#mkdirs()
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