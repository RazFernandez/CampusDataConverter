import org.jsoncsvconverter.Logic.JsonFileReader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

/**
 * Test class for JsonFileReader using JUnit 3.8.1.
 * This test suite validates all functionality of the JsonFileReader class including
 * constructor validation, file extension checking, JSON file reading capabilities,
 * error handling, and console output verification.
 *
 * <p>Test Coverage:</p>
 * <ul>
 *   <li>Constructor validation with valid and invalid file extensions</li>
 *   <li>JSON file reading with various content types</li>
 *   <li>Error handling for non-existent files</li>
 *   <li>Console output verification</li>
 *   <li>Edge cases with empty files and special characters</li>
 *   <li>Line break preservation in JSON content</li>
 * </ul>
 *
 * @author Miguel Fernandez
 * @version 1.0
 * @since 1.0
 */
public class JsonFileReaderTest extends TestCase {

    /** Test directory for temporary files during testing */
    private static final String TEST_DIR = "test_json_files";

    /** Sample JSON content for testing */
    private static final String SAMPLE_JSON = "{\n" +
            "  \"name\": \"John Doe\",\n" +
            "  \"age\": 30,\n" +
            "  \"email\": \"john@example.com\",\n" +
            "  \"address\": {\n" +
            "    \"street\": \"123 Main St\",\n" +
            "    \"city\": \"New York\"\n" +
            "  }\n" +
            "}";

    /** Complex JSON with arrays for testing */
    private static final String COMPLEX_JSON = "{\n" +
            "  \"users\": [\n" +
            "    {\"id\": 1, \"name\": \"Alice\"},\n" +
            "    {\"id\": 2, \"name\": \"Bob\"}\n" +
            "  ],\n" +
            "  \"settings\": {\n" +
            "    \"theme\": \"dark\",\n" +
            "    \"notifications\": true\n" +
            "  }\n" +
            "}";

    /** Original System.out for restoration after tests */
    private PrintStream originalSystemOut;

    /** Captured console output for verification */
    private ByteArrayOutputStream capturedOutput;

    /**
     * Constructor for JsonFileReaderTest.
     *
     * @param testName Name of the test case
     */
    public JsonFileReaderTest(String testName) {
        super(testName);
    }

    /**
     * Creates and returns a test suite containing all test methods.
     *
     * @return Test suite for JsonFileReader
     */
    public static Test suite() {
        return new TestSuite(JsonFileReaderTest.class);
    }

    /**
     * Sets up test fixtures before each test method.
     * Creates test directory, captures console output, and prepares test environment.
     */
    protected void setUp() throws Exception {
        super.setUp();

        // Create test directory
        File testDir = new File(TEST_DIR);
        if (!testDir.exists()) {
            testDir.mkdirs();
        }

        // Capture System.out for console output testing
        originalSystemOut = System.out;
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));
    }

    /**
     * Cleans up test fixtures after each test method.
     * Removes temporary files, directories, and restores console output.
     */
    protected void tearDown() throws Exception {
        super.tearDown();

        // Restore original System.out
        System.setOut(originalSystemOut);

        // Clean up test files and directory
        File testDir = new File(TEST_DIR);
        if (testDir.exists()) {
            File[] files = testDir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
            testDir.delete();
        }
    }

    /**
     * Tests constructor with valid JSON file extension.
     * Verifies that constructor accepts valid .json filenames without throwing exceptions.
     */
    public void testConstructorWithValidJsonExtension() {
        try {
            JsonFileReader reader = new JsonFileReader("test.json");
            assertNotNull("JsonFileReader should be created successfully", reader);

            // Verify console output
            String consoleOutput = capturedOutput.toString();
            assertTrue("Console should show creation message",
                    consoleOutput.contains("JsonFileReader created for file: test.json"));
        } catch (Exception e) {
            fail("Constructor should not throw exception with valid .json extension: " + e.getMessage());
        }
    }

    /**
     * Tests constructor with valid JSON file path including directories.
     * Verifies that full paths with .json extension are accepted.
     */
    public void testConstructorWithValidJsonPath() {
        try {
            String validPath = "path/to/file.json";
            JsonFileReader reader = new JsonFileReader(validPath);
            assertNotNull("JsonFileReader should be created with full path", reader);

            // Verify console output contains the full path
            String consoleOutput = capturedOutput.toString();
            assertTrue("Console should show full path in creation message",
                    consoleOutput.contains(validPath));
        } catch (Exception e) {
            fail("Constructor should not throw exception with valid path: " + e.getMessage());
        }
    }

    /**
     * Tests constructor with file extension other than .json.
     * Verifies that IllegalArgumentException is thrown for non-JSON extensions.
     */
    public void testConstructorWithInvalidExtension() {
        String[] invalidExtensions = {"test.txt", "data.csv", "file.xml", "document.pdf", "noextension"};

        for (int i = 0; i < invalidExtensions.length; i++) {
            try {
                new JsonFileReader(invalidExtensions[i]);
                fail("Constructor should throw IllegalArgumentException for extension: " + invalidExtensions[i]);
            } catch (IllegalArgumentException e) {
                assertEquals("Error message should match expected",
                        "Error: File must have a .json extension", e.getMessage());
            } catch (Exception e) {
                fail("Constructor should throw IllegalArgumentException, not " + e.getClass().getSimpleName() +
                        " for extension: " + invalidExtensions[i]);
            }
        }
    }

    /**
     * Tests constructor with case variations of JSON extension.
     * Verifies that case-sensitive extension checking works correctly.
     */
    public void testConstructorWithCaseVariationsOfJsonExtension() {
        String[] caseVariations = {"test.JSON", "test.Json", "test.jSoN"};

        for (int i = 0; i < caseVariations.length; i++) {
            try {
                new JsonFileReader(caseVariations[i]);
                fail("Constructor should be case-sensitive and reject: " + caseVariations[i]);
            } catch (IllegalArgumentException e) {
                assertEquals("Error message should match expected for case variation",
                        "Error: File must have a .json extension", e.getMessage());
            } catch (Exception e) {
                fail("Constructor should throw IllegalArgumentException for case variation: " + caseVariations[i]);
            }
        }
    }

    /**
     * Tests getJsonString method with existing valid JSON file.
     * Verifies that JSON content is read correctly and formatting is preserved.
     */
    public void testGetJsonStringWithValidFile() throws Exception {
        String fileName = TEST_DIR + File.separator + "valid_test.json";

        // Create test JSON file
        createTestJsonFile(fileName, SAMPLE_JSON);

        // Test reading the file
        JsonFileReader reader = new JsonFileReader(fileName);
        String result = reader.getJsonString();

        assertNotNull("Result should not be null", result);
        assertFalse("Result should not be empty", result.trim().isEmpty());
        assertTrue("Result should contain JSON content", result.contains("John Doe"));
        assertTrue("Result should contain nested object", result.contains("address"));
        assertTrue("Result should preserve line breaks", result.contains("\n"));

        // Verify the content matches what we wrote (accounting for added newlines)
        String expectedWithNewlines = SAMPLE_JSON + "\n";
        assertEquals("Content should match expected JSON with preserved formatting",
                expectedWithNewlines, result);
    }

    /**
     * Tests getJsonString method with complex JSON structure.
     * Verifies that complex nested objects and arrays are read correctly.
     */
    public void testGetJsonStringWithComplexJson() throws Exception {
        String fileName = TEST_DIR + File.separator + "complex_test.json";

        // Create complex JSON file
        createTestJsonFile(fileName, COMPLEX_JSON);

        JsonFileReader reader = new JsonFileReader(fileName);
        String result = reader.getJsonString();

        assertNotNull("Result should not be null", result);
        assertTrue("Result should contain users array", result.contains("users"));
        assertTrue("Result should contain Alice", result.contains("Alice"));
        assertTrue("Result should contain Bob", result.contains("Bob"));
        assertTrue("Result should contain settings object", result.contains("settings"));
        assertTrue("Result should contain theme property", result.contains("theme"));
    }

    /**
     * Tests getJsonString method with empty JSON file.
     * Verifies that empty files are handled gracefully.
     */
    public void testGetJsonStringWithEmptyFile() throws Exception {
        String fileName = TEST_DIR + File.separator + "empty_test.json";

        // Create empty JSON file
        createTestJsonFile(fileName, "");

        JsonFileReader reader = new JsonFileReader(fileName);
        String result = reader.getJsonString();

        assertNotNull("Result should not be null even for empty file", result);
        assertEquals("Result should be empty string for empty file", "", result);
    }

    /**
     * Tests getJsonString method with single line JSON.
     * Verifies that single-line JSON is read correctly and newline is added.
     */
    public void testGetJsonStringWithSingleLineJson() throws Exception {
        String fileName = TEST_DIR + File.separator + "single_line_test.json";
        String singleLineJson = "{\"name\":\"Test\",\"value\":123}";

        createTestJsonFile(fileName, singleLineJson);

        JsonFileReader reader = new JsonFileReader(fileName);
        String result = reader.getJsonString();

        assertNotNull("Result should not be null", result);
        assertTrue("Result should contain the JSON content", result.contains("Test"));
        assertTrue("Result should contain the value", result.contains("123"));
        assertEquals("Result should have newline added", singleLineJson + "\n", result);
    }

    /**
     * Tests getJsonString method with non-existent file.
     * Verifies that FileNotFoundException is handled gracefully and empty string is returned.
     */
    public void testGetJsonStringWithNonExistentFile() {
        String nonExistentFile = TEST_DIR + File.separator + "nonexistent.json";

        // Ensure file doesn't exist
        File file = new File(nonExistentFile);
        if (file.exists()) {
            file.delete();
        }

        JsonFileReader reader = new JsonFileReader(nonExistentFile);
        String result = reader.getJsonString();

        assertNotNull("Result should not be null even for non-existent file", result);
        assertEquals("Result should be empty string for non-existent file", "", result);

        // Restore System.out temporarily to check error output
        System.setOut(originalSystemOut);
        String errorOutput = capturedOutput.toString();
        assertTrue("Console should show error message", errorOutput.contains("Error reading file"));

        // Restore captured output stream
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));
    }

    /**
     * Tests getJsonString method with JSON containing special characters.
     * Verifies that special characters, unicode, and escape sequences are preserved.
     */
    public void testGetJsonStringWithSpecialCharacters() throws Exception {
        String fileName = TEST_DIR + File.separator + "special_chars_test.json";
        String specialJson = "{\n" +
                "  \"message\": \"Hello, \\\"World\\\"!\",\n" +
                "  \"unicode\": \"Café résumé naïve\",\n" +
                "  \"symbols\": \"@#$%^&*()_+-={}[]|\\\\:;<>?,./'`~\",\n" +
                "  \"newlines\": \"Line 1\\nLine 2\\nLine 3\"\n" +
                "}";

        createTestJsonFile(fileName, specialJson);

        JsonFileReader reader = new JsonFileReader(fileName);
        String result = reader.getJsonString();

        assertNotNull("Result should not be null", result);
        assertTrue("Result should contain escaped quotes", result.contains("\\\"World\\\""));
        assertTrue("Result should contain unicode characters", result.contains("Café"));
        assertTrue("Result should contain special symbols", result.contains("@#$%"));
        assertTrue("Result should contain escaped newlines", result.contains("\\n"));
    }

    /**
     * Tests line break preservation in multi-line JSON.
     * Verifies that original formatting and line breaks are maintained.
     */
    public void testLineBreakPreservation() throws Exception {
        String fileName = TEST_DIR + File.separator + "multiline_test.json";
        String multilineJson = "{\n" +
                "  \"line1\": \"first\",\n" +
                "\n" +
                "  \"line2\": \"second\",\n" +
                "    \"indented\": \"value\"\n" +
                "}";

        createTestJsonFile(fileName, multilineJson);

        JsonFileReader reader = new JsonFileReader(fileName);
        String result = reader.getJsonString();

        // Count newlines in original vs result (result should have one additional \n at end)
        int originalNewlines = countNewlines(multilineJson);
        int resultNewlines = countNewlines(result);

        assertEquals("Result should have original newlines plus one additional",
                originalNewlines + 1, resultNewlines);

        // Verify specific formatting is preserved
        assertTrue("Result should contain empty line", result.contains("\n\n"));
        assertTrue("Result should preserve indentation", result.contains("    \"indented\""));
    }

    /**
     * Tests console output verification.
     * Verifies that constructor properly outputs creation message to console.
     */
    public void testConsoleOutputVerification() {
        String testFileName = "console_test.json";

        JsonFileReader reader = new JsonFileReader(testFileName);

        String consoleOutput = capturedOutput.toString();
        assertTrue("Console should contain creation message",
                consoleOutput.contains("JsonFileReader created for file:"));
        assertTrue("Console should contain filename",
                consoleOutput.contains(testFileName));
    }

    /**
     * Tests multiple file readings with same reader instance.
     * Verifies that the same reader can be used multiple times consistently.
     */
    public void testMultipleReadingsWithSameReader() throws Exception {
        String fileName = TEST_DIR + File.separator + "multiple_reads_test.json";

        createTestJsonFile(fileName, SAMPLE_JSON);

        JsonFileReader reader = new JsonFileReader(fileName);

        // Read the file multiple times
        String firstRead = reader.getJsonString();
        String secondRead = reader.getJsonString();
        String thirdRead = reader.getJsonString();

        assertNotNull("First read should not be null", firstRead);
        assertNotNull("Second read should not be null", secondRead);
        assertNotNull("Third read should not be null", thirdRead);

        assertEquals("All reads should return identical content", firstRead, secondRead);
        assertEquals("All reads should return identical content", secondRead, thirdRead);

        assertTrue("Content should contain expected data", firstRead.contains("John Doe"));
    }

    /**
     * Utility method to create a test JSON file with specified content.
     *
     * @param fileName Path to the file to create
     * @param content JSON content to write to the file
     * @throws IOException if file creation fails
     */
    private void createTestJsonFile(String fileName, String content) throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(fileName);
            writer.write(content);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // Ignore close exceptions in test utility
                }
            }
        }
    }

    /**
     * Utility method to count newline characters in a string.
     *
     * @param text The string to count newlines in
     * @return Number of newline characters found
     */
    private int countNewlines(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                count++;
            }
        }
        return count;
    }
}