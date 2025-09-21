package org.jsoncsvconverter.Logic;
import java.util.*;

public class JSONParser {
    private final List<String> headers = new ArrayList<>();

    // Add a single header
    public void addHeader(String header) {
        headers.add(header);
    }

    // Add multiple headers at once
    public void addHeaders(Collection<String> newHeaders) {
        headers.addAll(newHeaders);
    }

    // Get an unmodifiable copy (safe)
    public List<String> getHeaders() {
        return Collections.unmodifiableList(headers);
    }
}