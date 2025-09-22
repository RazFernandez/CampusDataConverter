package org.jsoncsvconverter.Logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * A utility class for reading JSON files from the file system.
 * This class provides a simple interface to read JSON files and return their contents
 * as a string for further processing.
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Validates that the file has a .json extension</li>
 *   <li>Efficiently reads files using BufferedReader</li>
 *   <li>Handles I/O errors gracefully with proper exception management</li>
 *   <li>Preserves line breaks in the original JSON format</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * JsonFileReader reader = new JsonFileReader("data.json");
 * String jsonContent = reader.getJsonString();
 * }</pre>
 *
 * @author Miguel Fernandez
 * @version 1.0
 * @since 1.0
 */
public class JsonFileReader {

    /** The path to the JSON file to be read */
    private final String filename;

    /**
     * Constructs a new JsonFileReader for the specified file.
     * This constructor validates that the file has a .json extension but does not
     * verify that the file exists or is readable until {@link #getJsonString()} is called.
     *
     * <p>The constructor performs immediate validation to ensure the file extension
     * is correct, helping to catch configuration errors early in the process.</p>
     *
     * @param filename The path to the JSON file to read. Must end with .json extension.
     * @throws IllegalArgumentException if the filename does not end with .json extension
     *
     * @see #getJsonString()
     */
    public JsonFileReader(String filename) {
        if (!filename.endsWith(".json")) {
            throw new IllegalArgumentException("Error: File must have a .json extension");
        }
        this.filename = filename;
        System.out.println("JsonFileReader created for file: " + filename);
    }

    /**
     * Reads the entire contents of the JSON file and returns it as a string.
     * This method opens the file, reads all lines, and concatenates them with
     * newline characters preserved to maintain the original JSON formatting.
     *
     * <p>The method uses a {@link BufferedReader} for efficient file reading and
     * automatically closes the file using try-with-resources to prevent resource leaks.
     * If an I/O error occurs during reading, the error message is printed to the console
     * and an empty string is returned.</p>
     *
     * <p><strong>Error Handling:</strong></p>
     * <ul>
     *   <li>FileNotFoundException: If the specified file doesn't exist</li>
     *   <li>IOException: If there are problems reading the file (permissions, disk errors, etc.)</li>
     *   <li>SecurityException: If access to the file is denied by security manager</li>
     * </ul>
     *
     * <p><strong>Performance Note:</strong> This method reads the entire file into memory.
     * For very large JSON files, consider streaming approaches if memory usage is a concern.</p>
     *
     * @return The complete contents of the JSON file as a string, with original line breaks preserved.
     *         Returns an empty string if an I/O error occurs during reading.
     *
     * @see BufferedReader
     * @see FileReader
     */
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