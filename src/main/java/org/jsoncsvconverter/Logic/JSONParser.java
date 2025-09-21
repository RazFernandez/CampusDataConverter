package org.jsoncsvconverter.Logic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Extracts flat headers (keys) from JSON structures.
 * - Only leaf keys are added (primitive values or nulls).
 * - Nested keys are flattened with underscore separators:
 *     contact -> email  -> contact_email
 *     projects -> details -> budget -> projects_details_budget
 * - Order follows first-seen discovery using a LinkedHashSet.
 */
public class JSONParser {
    private final Set<String> headers = new LinkedHashSet<>();

    /**
     * Clear existing headers so the parser can be reused.
     */
    public void clearHeaders() {
        headers.clear();
    }

    /**
     * Add a single header (internal use).
     */
    private void addHeader(String header) {
        headers.add(header);
    }

    /**
     * Public entry point: extract headers from a JSONObject (no prefix).
     */
    public void extractHeadersFromObject(JSONObject jsonObject) {
        extractHeadersFromObject(jsonObject, "");
    }

    /**
     * Recursive extractor for JSONObject.
     * - If value is a primitive (String, Number, boolean, JSONObject.NULL), add the header.
     * - If value is JSONObject, recurse with prefix.
     * - If value is JSONArray, recurse into array elements with the same prefix.
     */
    private void extractHeadersFromObject(JSONObject jsonObject, String prefix) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            String fullKey = prefix.isEmpty() ? key : prefix + "_" + key;

            if (value instanceof JSONObject) {
                // Do NOT add the parent key itself; go deeper so we only add leaf keys.
                extractHeadersFromObject((JSONObject) value, fullKey);
            } else if (value instanceof JSONArray) {
                // For arrays, inspect elements; we don't add the array name as a header
                // unless the array contains primitive elements (handled in the array method).
                extractHeadersFromArray((JSONArray) value, fullKey);
            } else {
                // Primitive or null: this is a leaf value -> add header
                addHeader(fullKey);
            }
        }
    }

    /**
     * Recursive extractor for JSONArray.
     * - If elements are objects, recurse into each element using the provided prefix.
     * - If elements are arrays, recurse further.
     * - If elements are primitive values, add the prefix as a header once (e.g. "tags").
     */
    private void extractHeadersFromArray(JSONArray jsonArray, String prefix) {
        if (jsonArray.isEmpty()) {
            // If array is empty, we don't know element structure, skip adding header.
            return;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            Object element = jsonArray.get(i);

            if (element instanceof JSONObject) {
                // Recurse into the object element using same prefix.
                extractHeadersFromObject((JSONObject) element, prefix);
            } else if (element instanceof JSONArray) {
                // Nested arrays: recurse with same prefix.
                extractHeadersFromArray((JSONArray) element, prefix);
            } else {
                // Primitive element (String, Number, boolean, or null)
                // Use the prefix as the header (add once).
                addHeader(prefix);
                // If array contains primitives, adding once is enough — break to avoid duplicates.
                break;
            }
        }
    }

    /**
     * Get headers as an unmodifiable List (safe to return to callers).
     */
    public List<String> getHeaders() {
        return Collections.unmodifiableList(new ArrayList<>(headers));
    }

    /**
     * Get headers as an array for libraries like OpenCSV.
     */
    public String[] getHeadersArray() {
        return headers.toArray(new String[0]);
    }
}