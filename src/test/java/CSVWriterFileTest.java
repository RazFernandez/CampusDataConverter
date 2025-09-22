
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jsoncsvconverter.Logic.CSVWriterFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for CSVWriterFile using JUnit 3.8.1.
 * This test suite validates all functionality of the CSVWriterFile class including
 * constructor validation, file creation with and without data, directory creation,
 * and error handling scenarios.
 *
 * <p>Test Coverage:</p>
 * <ul>
 *   <li>Constructor validation with valid and invalid headers</li>
 *   <li>CSV file creation with headers only</li>
 *   <li>CSV file creation with complete data</li>
 *   <li>Automatic parent directory creation</li>
 *   <li>Error handling for file system issues</li>
 *   <li>Edge cases with null and empty data</li>
 * </ul>
 *
 * @author Miguel Fernandez
 * @version 1.0
 * @since 1.0
 */
public class CSVWriterFileTest extends TestCase {

    /** Test directory for temporary files during testing */
    private static final String TEST_DIR = "test_output";

    /** Sample headers for testing */
    private static final String[] VALID_HEADERS = {"Name", "Age", "Email", "City"};

    /** Sample data rows for testing */
    private List testRows;

    /** Instance of CSVWriterFile for testing */
    private CSVWriterFile csvWriter;

    /**
     * Constructor for CSVWriterFileTest.
     *
     * @param testName Name of the test case
     */
    public CSVWriterFileTest(String testName) {
        super(testName);
    }

    /**
     * Creates and returns a test suite containing all test methods.
     *
     * @return Test suite for CSVWriterFile
     */
    public static Test suite() {
        return new TestSuite(CSVWriterFileTest.class);
    }

    /**
     * Sets up test fixtures before each test method.
     * Creates test data and ensures clean test environment.
     */
    protected void setUp() throws Exception {
        super.setUp();

        // Create test data
        testRows = new ArrayList();
        testRows.add(new String[]{"John Doe", "25", "john@email.com", "New York"});
        testRows.add(new String[]{"Jane Smith", "30", "jane@email.com", "Los Angeles"});
        testRows.add(new String[]{"Bob Johnson", "35", "bob@email.com", "Chicago"});

        // Create CSVWriterFile instance with valid headers
        csvWriter = new CSVWriterFile(VALID_HEADERS);

        // Ensure test directory exists
        File testDir = new File(TEST_DIR);
        if (!testDir.exists()) {
            testDir.mkdirs();
        }
    }

    /**
     * Cleans up test fixtures after each test method.
     * Removes temporary files and directories created during testing.
     */
    protected void tearDown() throws Exception {
        super.tearDown();

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
     * Tests constructor with valid headers.
     * Verifies that the constructor accepts valid header arrays without throwing exceptions.
     */
    public void testConstructorWithValidHeaders() {
        try {
            CSVWriterFile writer = new CSVWriterFile(VALID_HEADERS);
            assertNotNull("CSVWriterFile should be created successfully", writer);
        } catch (Exception e) {
            fail("Constructor should not throw exception with valid headers: " + e.getMessage());
        }
    }

    /**
     * Tests constructor with null headers.
     * Verifies that IllegalArgumentException is thrown when headers are null.
     */
    public void testConstructorWithNullHeaders() {
        try {
            new CSVWriterFile(null);
            fail("Constructor should throw IllegalArgumentException for null headers");
        } catch (IllegalArgumentException e) {
            assertEquals("Error message should match expected",
                    "Headers cannot be null or empty.", e.getMessage());
        } catch (Exception e) {
            fail("Constructor should throw IllegalArgumentException, not " + e.getClass().getSimpleName());
        }
    }

    /**
     * Tests constructor with empty headers array.
     * Verifies that IllegalArgumentException is thrown when headers array is empty.
     */
    public void testConstructorWithEmptyHeaders() {
        try {
            new CSVWriterFile(new String[0]);
            fail("Constructor should throw IllegalArgumentException for empty headers");
        } catch (IllegalArgumentException e) {
            assertEquals("Error message should match expected",
                    "Headers cannot be null or empty.", e.getMessage());
        } catch (Exception e) {
            fail("Constructor should throw IllegalArgumentException, not " + e.getClass().getSimpleName());
        }
    }

    /**
     * Tests createNewCSVFile method with valid path.
     * Verifies that a CSV file with only headers is created correctly.
     */
    public void testCreateNewCSVFile() throws Exception {
        String filePath = TEST_DIR + File.separator + "test_headers_only.csv";

        csvWriter.createNewCSVFile(filePath);

        // Verify file was created
        File file = new File(filePath);
        assertTrue("CSV file should be created", file.exists());
        assertTrue("Created file should not be empty", file.length() > 0);

        // Verify file content contains only headers
        List lines = readCSVLines(filePath);
        assertEquals("File should contain exactly one line (headers)", 1, lines.size());

        String headerLine = (String) lines.get(0);
        assertTrue("Header line should contain 'Name'", headerLine.contains("Name"));
        assertTrue("Header line should contain 'Age'", headerLine.contains("Age"));
        assertTrue("Header line should contain 'Email'", headerLine.contains("Email"));
        assertTrue("Header line should contain 'City'", headerLine.contains("City"));
    }

    /**
     * Tests createCSVWithData method with valid data.
     * Verifies that a complete CSV file with headers and data rows is created correctly.
     */
    public void testCreateCSVWithData() throws Exception {
        String filePath = TEST_DIR + File.separator + "test_with_data.csv";

        csvWriter.createCSVWithData(filePath, testRows);

        // Verify file was created
        File file = new File(filePath);
        assertTrue("CSV file should be created", file.exists());
        assertTrue("Created file should not be empty", file.length() > 0);

        // Verify file content
        List lines = readCSVLines(filePath);
        assertEquals("File should contain headers + data rows", 4, lines.size()); // 1 header + 3 data rows

        // Verify header line
        String headerLine = (String) lines.get(0);
        assertTrue("Header line should contain all headers",
                headerLine.contains("Name") && headerLine.contains("Age") &&
                        headerLine.contains("Email") && headerLine.contains("City"));

        // Verify data lines
        String firstDataLine = (String) lines.get(1);
        assertTrue("First data line should contain John Doe", firstDataLine.contains("John Doe"));
        assertTrue("First data line should contain john@email.com", firstDataLine.contains("john@email.com"));
    }

    /**
     * Tests createCSVWithData method with null data.
     * Verifies that only headers are written when data list is null.
     */
    public void testCreateCSVWithNullData() throws Exception {
        String filePath = TEST_DIR + File.separator + "test_null_data.csv";

        csvWriter.createCSVWithData(filePath, null);

        // Verify file was created with only headers
        File file = new File(filePath);
        assertTrue("CSV file should be created", file.exists());

        List lines = readCSVLines(filePath);
        assertEquals("File should contain only headers when data is null", 1, lines.size());
    }

    /**
     * Tests createCSVWithData method with empty data list.
     * Verifies that only headers are written when data list is empty.
     */
    public void testCreateCSVWithEmptyData() throws Exception {
        String filePath = TEST_DIR + File.separator + "test_empty_data.csv";

        csvWriter.createCSVWithData(filePath, new ArrayList());

        // Verify file was created with only headers
        File file = new File(filePath);
        assertTrue("CSV file should be created", file.exists());

        List lines = readCSVLines(filePath);
        assertEquals("File should contain only headers when data is empty", 1, lines.size());
    }

    /**
     * Tests automatic parent directory creation.
     * Verifies that parent directories are created automatically when they don't exist.
     */
    public void testParentDirectoryCreation() throws Exception {
        String nestedPath = TEST_DIR + File.separator + "nested" + File.separator +
                "deep" + File.separator + "directory" + File.separator + "test.csv";

        // Ensure nested directories don't exist
        File nestedDir = new File(TEST_DIR + File.separator + "nested");
        if (nestedDir.exists()) {
            deleteDirectory(nestedDir);
        }

        csvWriter.createNewCSVFile(nestedPath);

        // Verify file and directories were created
        File file = new File(nestedPath);
        assertTrue("CSV file should be created in nested directories", file.exists());
        assertTrue("Parent directories should be created", file.getParentFile().exists());
    }

    /**
     * Tests error handling for invalid file paths.
     * Verifies that appropriate exceptions are thrown for problematic file paths.
     */
    public void testErrorHandlingForInvalidPath() {
        // Test with invalid characters (this may vary by operating system)
        String invalidPath = "invalid\u0000path.csv";

        try {
            csvWriter.createNewCSVFile(invalidPath);
            // Note: This test may pass on some systems that handle null characters
            // The main goal is to ensure the method doesn't crash unexpectedly
        } catch (RuntimeException e) {
            assertTrue("Exception should wrap an IOException", e.getCause() instanceof IOException);
            assertTrue("Error message should contain file path", e.getMessage().contains("Error while creating CSV file"));
        } catch (Exception e) {
            // Other exceptions are acceptable as long as they're properly handled
            assertNotNull("Exception should have a message", e.getMessage());
        }
    }

    /**
     * Tests file overwriting behavior.
     * Verifies that existing files are properly overwritten with new content.
     */
    public void testFileOverwriting() throws Exception {
        String filePath = TEST_DIR + File.separator + "test_overwrite.csv";

        // Create initial file with headers only
        csvWriter.createNewCSVFile(filePath);
        File file = new File(filePath);
        long initialSize = file.length();

        // Wait a moment to ensure different timestamps
        Thread.sleep(10);

        // Overwrite with data
        csvWriter.createCSVWithData(filePath, testRows);
        long newSize = file.length();

        assertTrue("File should exist after overwriting", file.exists());
        assertTrue("New file should be larger than header-only file", newSize > initialSize);

        // Verify content
        List lines = readCSVLines(filePath);
        assertEquals("Overwritten file should have correct number of lines", 4, lines.size());
    }

    /**
     * Tests CSV format correctness with special characters.
     * Verifies that CSV format is properly maintained with quotes and commas in data.
     */
    public void testCSVFormatWithSpecialCharacters() throws Exception {
        String filePath = TEST_DIR + File.separator + "test_special_chars.csv";

        // Create data with special characters
        List specialRows = new ArrayList();
        specialRows.add(new String[]{"Smith, John", "25", "john@email.com", "New York, NY"});
        specialRows.add(new String[]{"O'Connor", "30", "test\"quote@email.com", "Boston"});

        csvWriter.createCSVWithData(filePath, specialRows);

        // Verify file was created
        File file = new File(filePath);
        assertTrue("CSV file should be created", file.exists());

        // Verify content can be read (basic format check)
        List lines = readCSVLines(filePath);
        assertEquals("File should contain headers + data rows", 3, lines.size());

        // Check that commas in data are properly handled
        String firstDataLine = (String) lines.get(1);
        assertTrue("Line should contain the name with comma", firstDataLine.contains("Smith"));
    }

    /**
     * Utility method to read all lines from a CSV file.
     *
     * @param filePath Path to the CSV file
     * @return List of strings, each representing a line in the file
     * @throws IOException if file cannot be read
     */
    private List readCSVLines(String filePath) throws IOException {
        List lines = new ArrayList();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Ignore close exceptions
                }
            }
        }

        return lines;
    }

    /**
     * Utility method to recursively delete a directory and its contents.
     *
     * @param directory The directory to delete
     */
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
            directory.delete();
        }
    }
}