package org.jsoncsvconverter.Logic;

import com.google.gson.*;
import java.util.*;

/**
 * A JSON parser that converts JSON objects into a flattened tabular format suitable for CSV conversion.
 * This class handles complex nested structures, arrays of primitives, and arrays of objects by flattening
 * them into rows and columns that can be easily exported to CSV format.
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Flattens nested JSON objects using double underscore (__) as separator</li>
 *   <li>Handles primitive arrays by creating separate rows for each element</li>
 *   <li>Processes object arrays by flattening each object and creating individual rows</li>
 *   <li>Maintains consistent column structure across all rows</li>
 * </ul>
 *
 * @author Miguel Fernandez
 * @version 1.0
 * @since 1.0
 */
public class JSONParser {

    /** The original JSON object parsed from the input string */
    private final JsonObject jsonObject;

    /** Set of all unique column headers found during parsing */
    private final Set<String> headers = new LinkedHashSet<>();

    /** List of rows, where each row is an array of string values corresponding to headers */
    private final List<String[]> rows = new ArrayList<>();

    /**
     * Constructs a new JSONParser and immediately processes the provided JSON string.
     *
     * @param jsonString A valid JSON string to be parsed and flattened
     * @throws JsonSyntaxException if the JSON string is malformed
     * @throws IllegalStateException if the root element is not a JSON object
     */
    public JSONParser(String jsonString) {
        this.jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        processJson();
    }

    /**
     * Processes the JSON object by flattening it into tabular format.
     * This method orchestrates the flattening process and ensures all rows have
     * values for all discovered headers.
     *
     * <p>The processing involves:</p>
     * <ol>
     *   <li>Recursively flattening the JSON structure</li>
     *   <li>Collecting all unique headers from all rows</li>
     *   <li>Normalizing rows to ensure consistent column structure</li>
     * </ol>
     */
    private void processJson() {
        List<Map<String, String>> flattenedRows = flattenJson(jsonObject, "");

        // Collect all unique headers
        for (Map<String, String> row : flattenedRows) {
            headers.addAll(row.keySet());
        }

        // Create normalized rows with all headers
        for (Map<String, String> row : flattenedRows) {
            List<String> rowValues = new ArrayList<>();
            for (String header : headers) {
                rowValues.add(row.getOrDefault(header, ""));
            }
            rows.add(rowValues.toArray(new String[0]));
        }
    }

    /**
     * Recursively flattens a JSON element into a list of key-value maps.
     * This is the core method that handles the complex logic of converting
     * nested JSON structures into tabular format.
     *
     * <p>Handling strategy:</p>
     * <ul>
     *   <li><strong>Objects:</strong> Nested objects are flattened with prefixed keys</li>
     *   <li><strong>Primitive Arrays:</strong> Each element creates a separate row</li>
     *   <li><strong>Object Arrays:</strong> Each object is flattened and creates separate rows</li>
     *   <li><strong>Primitives/Null:</strong> Direct key-value mapping</li>
     * </ul>
     *
     * @param element The JSON element to flatten (object, array, primitive, or null)
     * @param prefix The current key prefix for nested structures (uses "__" as separator)
     * @return A list of maps, where each map represents a row with column-value pairs
     */
    private List<Map<String, String>> flattenJson(JsonElement element, String prefix) {
        List<Map<String, String>> result = new ArrayList<>();

        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();

            // Separate different types of fields for specialized handling
            Map<String, List<String>> primitiveArrays = new LinkedHashMap<>();
            List<String> objectArrayKeys = new ArrayList<>();
            Map<String, String> scalarData = new LinkedHashMap<>();

            // Classify each property in the JSON object
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                String newPrefix = prefix.isEmpty() ? key : prefix + "__" + key;

                if (value.isJsonArray()) {
                    JsonArray arr = value.getAsJsonArray();
                    if (!arr.isEmpty() && arr.get(0).isJsonPrimitive()) {
                        // This is a primitive array - collect all values
                        List<String> arrayValues = new ArrayList<>();
                        for (JsonElement item : arr) {
                            arrayValues.add(item.getAsString());
                        }
                        primitiveArrays.put(newPrefix, arrayValues);
                    } else {
                        // This is an object array - handle separately
                        objectArrayKeys.add(key);
                    }
                } else {
                    // Handle scalar elements (objects, primitives, null)
                    List<Map<String, String>> childRows = flattenJson(value, newPrefix);
                    for (Map<String, String> childRow : childRows) {
                        scalarData.putAll(childRow);
                    }
                }
            }

            // Process object arrays first - each object creates its own set of rows
            List<Map<String, String>> objectArrayRows = new ArrayList<>();
            for (String arrayKey : objectArrayKeys) {
                JsonArray arr = obj.get(arrayKey).getAsJsonArray();
                String newPrefix = prefix.isEmpty() ? arrayKey : prefix + "__" + arrayKey;

                for (JsonElement item : arr) {
                    List<Map<String, String>> itemRows = flattenJson(item, newPrefix);
                    objectArrayRows.addAll(itemRows);
                }
            }

            // Merge object array rows with scalar data and handle primitive arrays
            if (!objectArrayRows.isEmpty()) {
                // Combine scalar data with each object array row
                for (int i = 0; i < objectArrayRows.size(); i++) {
                    Map<String, String> finalRow = new LinkedHashMap<>();

                    // Add scalar data only to the first row to avoid duplication
                    if (i == 0) {
                        finalRow.putAll(scalarData);
                    }

                    // Add the object array data
                    finalRow.putAll(objectArrayRows.get(i));
                    result.add(finalRow);
                }

                // Handle primitive arrays - they create additional rows
                int maxPrimitiveArrayLength = 0;
                for (List<String> arrayValues : primitiveArrays.values()) {
                    maxPrimitiveArrayLength = Math.max(maxPrimitiveArrayLength, arrayValues.size());
                }

                for (int i = 0; i < maxPrimitiveArrayLength; i++) {
                    Map<String, String> rowMap = new LinkedHashMap<>();

                    // Add primitive array elements at this index
                    for (Map.Entry<String, List<String>> arrayEntry : primitiveArrays.entrySet()) {
                        String arrayKey = arrayEntry.getKey();
                        List<String> arrayValues = arrayEntry.getValue();

                        if (i < arrayValues.size()) {
                            rowMap.put(arrayKey, arrayValues.get(i));
                        }
                    }
                    result.add(rowMap);
                }

            } else {
                // No object arrays, handle primitive arrays normally
                int maxArrayLength = 0;
                for (List<String> arrayValues : primitiveArrays.values()) {
                    maxArrayLength = Math.max(maxArrayLength, arrayValues.size());
                }

                if (maxArrayLength > 0) {
                    // Create rows: one row per index across all primitive arrays
                    for (int i = 0; i < maxArrayLength; i++) {
                        Map<String, String> rowMap = new LinkedHashMap<>();

                        // Add scalar data only to the first row
                        if (i == 0) {
                            rowMap.putAll(scalarData);
                        }

                        // Add primitive array elements at this index
                        for (Map.Entry<String, List<String>> arrayEntry : primitiveArrays.entrySet()) {
                            String arrayKey = arrayEntry.getKey();
                            List<String> arrayValues = arrayEntry.getValue();

                            if (i < arrayValues.size()) {
                                rowMap.put(arrayKey, arrayValues.get(i));
                            }
                        }
                        result.add(rowMap);
                    }
                } else {
                    // No arrays at all, just return the scalar data
                    result.add(scalarData);
                }
            }

        } else if (element.isJsonArray()) {
            JsonArray arr = element.getAsJsonArray();

            // Differentiate between primitive and object arrays
            if (!arr.isEmpty() && arr.get(0).isJsonPrimitive()) {
                // For primitive arrays, create one row per array element
                for (JsonElement item : arr) {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put(prefix, item.getAsString());
                    result.add(map);
                }
            } else {
                // For object arrays, flatten each object and create separate rows
                for (JsonElement item : arr) {
                    result.addAll(flattenJson(item, prefix));
                }
            }

        } else if (element.isJsonPrimitive() || element.isJsonNull()) {
            // Handle primitive values and null
            Map<String, String> map = new LinkedHashMap<>();
            map.put(prefix, element.isJsonNull() ? "" : element.getAsString());
            result.add(map);
        }

        return result;
    }

    /**
     * Returns the set of all unique column headers discovered during JSON flattening.
     * Headers are ordered in the sequence they were first encountered during parsing.
     *
     * @return An ordered set of column headers
     */
    public Set<String> getHeaders() {
        return headers;
    }

    /**
     * Returns all column headers as an array.
     * This is a convenience method for systems that work better with arrays than sets.
     *
     * @return An array containing all column headers in their original order
     */
    public String[] getHeadersArray() {
        return headers.toArray(new String[0]);
    }

    /**
     * Returns all parsed rows as a list of string arrays.
     * Each inner array represents one row, with values corresponding to the headers
     * returned by {@link #getHeaders()} or {@link #getHeadersArray()}.
     *
     * <p>Missing values are represented as empty strings to maintain consistent
     * column structure across all rows.</p>
     *
     * @return A list of rows, where each row is a string array of column values
     */
    public List<String[]> getRows() {
        return rows;
    }

    /**
     * Returns the original JsonObject that was parsed from the input string.
     * This can be useful for accessing the raw JSON data if needed.
     *
     * @return The original JsonObject from the parsed JSON string
     */
    public JsonObject getJsonObject() {
        return jsonObject;
    }
}