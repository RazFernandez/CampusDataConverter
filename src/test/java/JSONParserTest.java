import org.jsoncsvconverter.Logic.JSONParser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Test class for JSONParser using JUnit 3.8.1.
 * This test suite validates all functionality of the JSONParser class including
 * JSON parsing, flattening of nested structures, array handling, header generation,
 * and row creation for various JSON structures and edge cases.
 *
 * <p>Test Coverage:</p>
 * <ul>
 *   <li>Constructor validation with valid and malformed JSON</li>
 *   <li>Simple JSON object flattening</li>
 *   <li>Nested object handling with proper prefixing</li>
 *   <li>Primitive array processing</li>
 *   <li>Object array flattening</li>
 *   <li>Mixed array types handling</li>
 *   <li>Complex nested structures</li>
 *   <li>Edge cases with null values and empty arrays</li>
 *   <li>Header generation and ordering</li>
 *   <li>Row consistency and data integrity</li>
 * </ul>
 *
 * @author Miguel Fernandez
 * @version 1.0
 * @since 1.0
 */
public class JSONParserTest extends TestCase {

    /** Simple JSON object for basic testing */
    private static final String SIMPLE_JSON =
            "{\"id\": 1, \"name\": \"John\", \"age\": 30}";

    /** JSON with nested object */
    private static final String NESTED_JSON =
            "{\"id\": 1, \"name\": \"John\", \"contact\": {\"email\": \"john@email.com\", \"phone\": \"123-456-7890\"}}";

    /** JSON with primitive arrays */
    private static final String PRIMITIVE_ARRAYS_JSON =
            "{\"id\": 2, \"name\": \"Bob\", \"hobbies\": [\"reading\", \"cycling\", \"gaming\"], \"languages\": [\"English\", \"Spanish\"]}";

    /** JSON with object arrays */
    private static final String OBJECT_ARRAYS_JSON =
            "{\"id\": 3, \"name\": \"Charlie\", \"projects\": [{\"title\": \"Project A\", \"status\": \"completed\"}, {\"title\": \"Project B\", \"status\": \"in-progress\"}]}";

    /** Complex JSON with mixed structures */
    private static final String COMPLEX_JSON =
            "{\"id\": 4, \"name\": \"Alice\", \"contact\": {\"email\": \"alice@email.com\", \"address\": {\"street\": \"123 Main St\", \"city\": \"New York\"}}, \"skills\": [\"Java\", \"Python\"], \"projects\": [{\"name\": \"Web App\", \"details\": {\"budget\": 5000, \"duration\": \"3 months\"}}, {\"name\": \"Mobile App\", \"details\": {\"budget\": 8000, \"duration\": \"4 months\"}}]}";

    /** JSON with null values and empty arrays */
    private static final String NULL_AND_EMPTY_JSON =
            "{\"id\": 5, \"name\": null, \"hobbies\": [], \"contact\": {\"email\": null, \"phone\": \"555-1234\"}}";

    /**
     * Constructor for JSONParserTest.
     *
     * @param testName Name of the test case
     */
    public JSONParserTest(String testName) {
        super(testName);
    }

    /**
     * Creates and returns a test suite containing all test methods.
     *
     * @return Test suite for JSONParser
     */
    public static Test suite() {
        return new TestSuite(JSONParserTest.class);
    }

    /**
     * Tests constructor with valid simple JSON.
     * Verifies that basic JSON objects are parsed correctly without exceptions.
     */
    public void testConstructorWithValidSimpleJson() {
        try {
            JSONParser parser = new JSONParser(SIMPLE_JSON);
            assertNotNull("JSONParser should be created successfully", parser);
            assertNotNull("JsonObject should not be null", parser.getJsonObject());
        } catch (Exception e) {
            fail("Constructor should not throw exception with valid JSON: " + e.getMessage());
        }
    }

    /**
     * Tests constructor with malformed JSON.
     * Verifies that JsonSyntaxException is thrown for invalid JSON syntax.
     */
    public void testConstructorWithMalformedJson() {
        String[] malformedJsons = {
                "{invalid json}",
                "{\"name\": \"John\", \"age\":}",
                "{\"unclosed\": \"quote}",
                "not json at all",
                "{\"trailing\": \"comma\",}",
                ""
        };

        for (int i = 0; i < malformedJsons.length; i++) {
            try {
                new JSONParser(malformedJsons[i]);
                fail("Constructor should throw JsonSyntaxException for malformed JSON: " + malformedJsons[i]);
            } catch (JsonSyntaxException e) {
                // Expected exception
                assertNotNull("Exception should have a message", e.getMessage());
            } catch (Exception e) {
                fail("Constructor should throw JsonSyntaxException, not " + e.getClass().getSimpleName() +
                        " for JSON: " + malformedJsons[i]);
            }
        }
    }

    /**
     * Tests constructor with non-object root JSON.
     * Verifies that arrays and primitives at root level are rejected.
     */
    public void testConstructorWithNonObjectRootJson() {
        String[] nonObjectJsons = {
                "[{\"id\": 1}, {\"id\": 2}]",  // Array at root
                "\"just a string\"",           // String at root
                "123",                         // Number at root
                "true",                        // Boolean at root
                "null"                         // Null at root
        };

        for (int i = 0; i < nonObjectJsons.length; i++) {
            try {
                new JSONParser(nonObjectJsons[i]);
                fail("Constructor should throw exception for non-object root: " + nonObjectJsons[i]);
            } catch (IllegalStateException e) {
                // Expected for non-object root
                assertNotNull("Exception should have a message", e.getMessage());
            } catch (Exception e) {
                // Other exceptions are also acceptable as long as they're thrown
                assertNotNull("Exception should have a message", e.getMessage());
            }
        }
    }

    /**
     * Tests simple JSON object flattening.
     * Verifies that basic key-value pairs are processed correctly.
     */
    public void testSimpleJsonFlattening() {
        JSONParser parser = new JSONParser(SIMPLE_JSON);

        Set headers = parser.getHeaders();
        String[] headersArray = parser.getHeadersArray();
        List rows = parser.getRows();

        // Verify headers
        assertEquals("Should have 3 headers", 3, headers.size());
        assertTrue("Headers should contain 'id'", headers.contains("id"));
        assertTrue("Headers should contain 'name'", headers.contains("name"));
        assertTrue("Headers should contain 'age'", headers.contains("age"));

        // Verify headers array matches set
        assertEquals("Headers array should match headers set size", headers.size(), headersArray.length);

        // Verify rows
        assertEquals("Should have exactly one row", 1, rows.size());
        String[] row = (String[]) rows.get(0);
        assertEquals("Row should have same length as headers", headersArray.length, row.length);

        // Find positions and verify values
        int idIndex = findHeaderIndex(headersArray, "id");
        int nameIndex = findHeaderIndex(headersArray, "name");
        int ageIndex = findHeaderIndex(headersArray, "age");

        assertEquals("ID should be '1'", "1", row[idIndex]);
        assertEquals("Name should be 'John'", "John", row[nameIndex]);
        assertEquals("Age should be '30'", "30", row[ageIndex]);
    }

    /**
     * Tests nested object flattening with proper prefixing.
     * Verifies that nested objects are flattened with double underscore separator.
     */
    public void testNestedObjectFlattening() {
        JSONParser parser = new JSONParser(NESTED_JSON);

        Set headers = parser.getHeaders();
        List rows = parser.getRows();

        // Verify headers include flattened nested fields
        assertTrue("Headers should contain 'id'", headers.contains("id"));
        assertTrue("Headers should contain 'name'", headers.contains("name"));
        assertTrue("Headers should contain 'contact__email'", headers.contains("contact__email"));
        assertTrue("Headers should contain 'contact__phone'", headers.contains("contact__phone"));

        // Verify single row
        assertEquals("Should have exactly one row", 1, rows.size());
        String[] row = (String[]) rows.get(0);
        String[] headersArray = parser.getHeadersArray();

        // Verify nested values
        int emailIndex = findHeaderIndex(headersArray, "contact__email");
        int phoneIndex = findHeaderIndex(headersArray, "contact__phone");

        assertEquals("Email should be correct", "john@email.com", row[emailIndex]);
        assertEquals("Phone should be correct", "123-456-7890", row[phoneIndex]);
    }

    /**
     * Tests primitive array handling.
     * Verifies that arrays of primitives create separate rows with proper alignment.
     */
    public void testPrimitiveArrayHandling() {
        JSONParser parser = new JSONParser(PRIMITIVE_ARRAYS_JSON);

        Set headers = parser.getHeaders();
        List rows = parser.getRows();
        String[] headersArray = parser.getHeadersArray();

        // Verify headers
        assertTrue("Headers should contain 'id'", headers.contains("id"));
        assertTrue("Headers should contain 'name'", headers.contains("name"));
        assertTrue("Headers should contain 'hobbies'", headers.contains("hobbies"));
        assertTrue("Headers should contain 'languages'", headers.contains("languages"));

        // Should have multiple rows for array elements
        assertTrue("Should have more than one row for arrays", rows.size() > 1);

        // Find indices
        int idIndex = findHeaderIndex(headersArray, "id");
        int nameIndex = findHeaderIndex(headersArray, "name");
        int hobbiesIndex = findHeaderIndex(headersArray, "hobbies");
        int languagesIndex = findHeaderIndex(headersArray, "languages");

        // First row should have id and name, plus first hobby and language
        String[] firstRow = (String[]) rows.get(0);
        assertEquals("First row should have ID", "2", firstRow[idIndex]);
        assertEquals("First row should have name", "Bob", firstRow[nameIndex]);
        assertEquals("First row should have first hobby", "reading", firstRow[hobbiesIndex]);
        assertEquals("First row should have first language", "English", firstRow[languagesIndex]);

        // Subsequent rows should have empty id and name but array values
        boolean foundCycling = false;
        boolean foundSpanish = false;

        for (int i = 1; i < rows.size(); i++) {
            String[] row = (String[]) rows.get(i);
            assertEquals("Subsequent rows should have empty ID", "", row[idIndex]);
            assertEquals("Subsequent rows should have empty name", "", row[nameIndex]);

            if ("cycling".equals(row[hobbiesIndex])) {
                foundCycling = true;
            }
            if ("Spanish".equals(row[languagesIndex])) {
                foundSpanish = true;
            }
        }

        assertTrue("Should find 'cycling' in hobbies", foundCycling);
        assertTrue("Should find 'Spanish' in languages", foundSpanish);
    }

    /**
     * Tests object array flattening.
     * Verifies that arrays of objects are properly flattened and create separate rows.
     */
    public void testObjectArrayFlattening() {
        JSONParser parser = new JSONParser(OBJECT_ARRAYS_JSON);

        Set headers = parser.getHeaders();
        List rows = parser.getRows();
        String[] headersArray = parser.getHeadersArray();

        // Verify headers include flattened object array fields
        assertTrue("Headers should contain 'id'", headers.contains("id"));
        assertTrue("Headers should contain 'name'", headers.contains("name"));
        assertTrue("Headers should contain 'projects__title'", headers.contains("projects__title"));
        assertTrue("Headers should contain 'projects__status'", headers.contains("projects__status"));

        // Should have multiple rows for object array
        assertTrue("Should have multiple rows for object array", rows.size() >= 2);

        // Find indices
        int idIndex = findHeaderIndex(headersArray, "id");
        int nameIndex = findHeaderIndex(headersArray, "name");
        int titleIndex = findHeaderIndex(headersArray, "projects__title");
        int statusIndex = findHeaderIndex(headersArray, "projects__status");

        // First row should have basic info plus first project
        String[] firstRow = (String[]) rows.get(0);
        assertEquals("First row should have ID", "3", firstRow[idIndex]);
        assertEquals("First row should have name", "Charlie", firstRow[nameIndex]);
        assertEquals("First row should have first project title", "Project A", firstRow[titleIndex]);
        assertEquals("First row should have first project status", "completed", firstRow[statusIndex]);

        // Second row should have empty basic info but second project
        String[] secondRow = (String[]) rows.get(1);
        assertEquals("Second row should have empty ID", "", secondRow[idIndex]);
        assertEquals("Second row should have empty name", "", secondRow[nameIndex]);
        assertEquals("Second row should have second project title", "Project B", secondRow[titleIndex]);
        assertEquals("Second row should have second project status", "in-progress", secondRow[statusIndex]);
    }

    /**
     * Tests complex nested structure with mixed array types.
     * Verifies that complex JSON with nested objects and mixed arrays is handled correctly.
     */
    public void testComplexNestedStructure() {
        JSONParser parser = new JSONParser(COMPLEX_JSON);

        Set headers = parser.getHeaders();
        List rows = parser.getRows();

        // Verify presence of deeply nested headers
        assertTrue("Should contain contact email", headers.contains("contact__email"));
        assertTrue("Should contain nested address", headers.contains("contact__address__street"));
        assertTrue("Should contain nested address city", headers.contains("contact__address__city"));
        assertTrue("Should contain skills array", headers.contains("skills"));
        assertTrue("Should contain project details", headers.contains("projects__details__budget"));
        assertTrue("Should contain project duration", headers.contains("projects__details__duration"));

        // Should have multiple rows due to both primitive and object arrays
        assertTrue("Should have multiple rows for complex structure", rows.size() > 2);

        String[] headersArray = parser.getHeadersArray();

        // Verify first row has basic info
        String[] firstRow = (String[]) rows.get(0);
        int idIndex = findHeaderIndex(headersArray, "id");
        int nameIndex = findHeaderIndex(headersArray, "name");
        int emailIndex = findHeaderIndex(headersArray, "contact__email");

        assertEquals("First row should have correct ID", "4", firstRow[idIndex]);
        assertEquals("First row should have correct name", "Alice", firstRow[nameIndex]);
        assertEquals("First row should have correct email", "alice@email.com", firstRow[emailIndex]);

        // Verify object array data is present
        boolean foundWebApp = false;
        boolean foundMobileApp = false;

        for (int i = 0; i < rows.size(); i++) {
            String[] row = (String[]) rows.get(i);
            int projectNameIndex = findHeaderIndex(headersArray, "projects__name");
            if (projectNameIndex >= 0 && "Web App".equals(row[projectNameIndex])) {
                foundWebApp = true;
            }
            if (projectNameIndex >= 0 && "Mobile App".equals(row[projectNameIndex])) {
                foundMobileApp = true;
            }
        }

        assertTrue("Should find Web App project", foundWebApp);
        assertTrue("Should find Mobile App project", foundMobileApp);
    }

    /**
     * Tests handling of null values and empty arrays.
     * Verifies that null values are converted to empty strings and empty arrays are handled gracefully.
     */
    public void testNullValuesAndEmptyArrays() {
        JSONParser parser = new JSONParser(NULL_AND_EMPTY_JSON);

        Set headers = parser.getHeaders();
        List rows = parser.getRows();
        String[] headersArray = parser.getHeadersArray();

        // Verify headers are still created for null fields
        assertTrue("Headers should contain 'name' even if null", headers.contains("name"));
        assertTrue("Headers should contain nested null field", headers.contains("contact__email"));
        assertTrue("Headers should contain non-null field", headers.contains("contact__phone"));

        // Should have at least one row
        assertTrue("Should have at least one row", rows.size() >= 1);

        String[] firstRow = (String[]) rows.get(0);

        // Verify null values are converted to empty strings
        int nameIndex = findHeaderIndex(headersArray, "name");
        int emailIndex = findHeaderIndex(headersArray, "contact__email");
        int phoneIndex = findHeaderIndex(headersArray, "contact__phone");

        assertEquals("Null name should become empty string", "", firstRow[nameIndex]);
        assertEquals("Null email should become empty string", "", firstRow[emailIndex]);
        assertEquals("Non-null phone should be preserved", "555-1234", firstRow[phoneIndex]);
    }

    /**
     * Tests header ordering consistency.
     * Verifies that headers maintain consistent ordering across multiple parser instances.
     */
    public void testHeaderOrderingConsistency() {
        JSONParser parser1 = new JSONParser(SIMPLE_JSON);
        JSONParser parser2 = new JSONParser(SIMPLE_JSON);

        String[] headers1 = parser1.getHeadersArray();
        String[] headers2 = parser2.getHeadersArray();

        assertEquals("Header arrays should have same length", headers1.length, headers2.length);

        for (int i = 0; i < headers1.length; i++) {
            assertEquals("Headers should be in same order", headers1[i], headers2[i]);
        }
    }

    /**
     * Tests row consistency across all generated rows.
     * Verifies that all rows have the same number of columns as headers.
     */
    public void testRowConsistency() {
        JSONParser parser = new JSONParser(COMPLEX_JSON);

        String[] headers = parser.getHeadersArray();
        List rows = parser.getRows();

        assertTrue("Should have at least one row", rows.size() > 0);

        for (int i = 0; i < rows.size(); i++) {
            String[] row = (String[]) rows.get(i);
            assertEquals("Row " + i + " should have same length as headers",
                    headers.length, row.length);

            // Verify no null values in row
            for (int j = 0; j < row.length; j++) {
                assertNotNull("Row values should not be null", row[j]);
            }
        }
    }

    /**
     * Tests getter method functionality.
     * Verifies that all getter methods return expected types and non-null values.
     */
    public void testGetterMethods() {
        JSONParser parser = new JSONParser(SIMPLE_JSON);

        // Test getHeaders()
        Set headers = parser.getHeaders();
        assertNotNull("getHeaders() should not return null", headers);
        assertTrue("Headers should not be empty", headers.size() > 0);

        // Test getHeadersArray()
        String[] headersArray = parser.getHeadersArray();
        assertNotNull("getHeadersArray() should not return null", headersArray);
        assertEquals("Headers array should match set size", headers.size(), headersArray.length);

        // Test getRows()
        List rows = parser.getRows();
        assertNotNull("getRows() should not return null", rows);
        assertTrue("Rows should not be empty", rows.size() > 0);

        // Test getJsonObject()
        assertNotNull("getJsonObject() should not return null", parser.getJsonObject());
        assertTrue("Should return JsonObject", parser.getJsonObject().isJsonObject());
    }

    /**
     * Tests empty JSON object handling.
     * Verifies that empty JSON objects are processed without errors.
     */
    public void testEmptyJsonObject() {
        String emptyJson = "{}";

        JSONParser parser = new JSONParser(emptyJson);

        Set headers = parser.getHeaders();
        List rows = parser.getRows();

        assertEquals("Empty JSON should produce no headers", 0, headers.size());
        assertEquals("Empty JSON should produce one empty row", 1, rows.size());

        String[] row = (String[]) rows.get(0);
        assertEquals("Empty row should have no columns", 0, row.length);
    }

    /**
     * Utility method to find the index of a header in the headers array.
     *
     * @param headers Array of header names
     * @param headerName The header name to find
     * @return Index of the header, or -1 if not found
     */
    private int findHeaderIndex(String[] headers, String headerName) {
        for (int i = 0; i < headers.length; i++) {
            if (headerName.equals(headers[i])) {
                return i;
            }
        }
        return -1;
    }
}