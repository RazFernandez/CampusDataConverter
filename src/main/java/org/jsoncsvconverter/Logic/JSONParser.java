package org.jsoncsvconverter.Logic;

import com.google.gson.*;
import java.util.*;

public class JSONParser {
    private final JsonObject jsonObject;
    private final Set<String> headers = new LinkedHashSet<>();
    private final List<String[]> rows = new ArrayList<>();

    public JSONParser(String jsonString) {
        this.jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        processJson();
    }

    private void processJson() {
        List<Map<String, String>> flattenedRows = flattenJson(jsonObject, "");
        for (Map<String, String> row : flattenedRows) {
            headers.addAll(row.keySet());
        }
        for (Map<String, String> row : flattenedRows) {
            List<String> rowValues = new ArrayList<>();
            for (String header : headers) {
                rowValues.add(row.getOrDefault(header, ""));
            }
            rows.add(rowValues.toArray(new String[0]));
        }
    }

    // Recursive flattener
    private List<Map<String, String>> flattenJson(JsonElement element, String prefix) {
        List<Map<String, String>> result = new ArrayList<>();

        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();

            // Separate different types of fields
            Map<String, List<String>> primitiveArrays = new LinkedHashMap<>();
            List<String> objectArrayKeys = new ArrayList<>();
            Map<String, String> scalarData = new LinkedHashMap<>();

            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                String newPrefix = prefix.isEmpty() ? key : prefix + "__" + key;

                if (value.isJsonArray()) {
                    JsonArray arr = value.getAsJsonArray();
                    if (!arr.isEmpty() && arr.get(0).isJsonPrimitive()) {
                        // This is a primitive array
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

            // First, handle object arrays - each creates its own set of rows
            List<Map<String, String>> objectArrayRows = new ArrayList<>();
            for (String arrayKey : objectArrayKeys) {
                JsonArray arr = obj.get(arrayKey).getAsJsonArray();
                String newPrefix = prefix.isEmpty() ? arrayKey : prefix + "__" + arrayKey;

                for (JsonElement item : arr) {
                    List<Map<String, String>> itemRows = flattenJson(item, newPrefix);
                    objectArrayRows.addAll(itemRows);
                }
            }

            // If we have object array rows, we need to merge them with scalar data and primitive arrays
            if (!objectArrayRows.isEmpty()) {
                // Each object array row needs to be combined with scalar data and primitive arrays
                for (int i = 0; i < objectArrayRows.size(); i++) {
                    Map<String, String> finalRow = new LinkedHashMap<>();

                    // Add scalar data only to the first row
                    if (i == 0) {
                        finalRow.putAll(scalarData);
                    }

                    // Add the object array data
                    finalRow.putAll(objectArrayRows.get(i));

                    result.add(finalRow);
                }

                // Now handle primitive arrays - they create additional rows
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

            // Check if array is of primitives or objects
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
            Map<String, String> map = new LinkedHashMap<>();
            map.put(prefix, element.isJsonNull() ? "" : element.getAsString());
            result.add(map);
        }

        return result;
    }

    // Getters
    public Set<String> getHeaders() {
        return headers;
    }

    public String[] getHeadersArray() {
        return headers.toArray(new String[0]);
    }

    public List<String[]> getRows() {
        return rows;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }
}