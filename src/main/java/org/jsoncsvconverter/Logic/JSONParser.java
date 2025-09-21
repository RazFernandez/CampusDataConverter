package org.jsoncsvconverter.Logic;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.LinkedHashSet;
import java.util.Set;

public class JSONParser {
    private final JsonObject jsonObject;          // Holds the parsed JSON
    private final Set<String> headers = new LinkedHashSet<>(); // Maintains order

    // Constructor that takes a JSON string
    public JSONParser(String jsonString) {
        this.jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        extractHeaders();
    }

    // Method to extract keys from the top-level object
    private void extractHeaders() {
        for (String key : jsonObject.keySet()) {
            headers.add(key);
        }
    }

    // Getter for headers as Set
    public Set<String> getHeaders() {
        return headers;
    }

    // Getter for headers as Array
    public String[] getHeadersArray() {
        return headers.toArray(new String[0]);
    }

    // Getter for the internal JsonObject
    public JsonObject getJsonObject() {
        return jsonObject;
    }
}
