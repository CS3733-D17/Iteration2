package com.slackers.inc.Http;

import java.util.List;
import java.util.Map;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 *
 *     Represents an HTTP response in our system.
 */
public class HttpResponse {
    
    protected final int code;
    // The actual response for the HttpResponse
    protected final String response;
    // Holds the headers for this response
    protected final Map<String, List<String>> headers;

    HttpResponse(int code, String response, Map<String, List<String>> headerFields) {
        this.code = code;
        this.response = response;
        this.headers = headerFields;
    }

    // Getters
    public int getCode() {
        return code;
    }

    public String getResponse() {
        return response;
    }    

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
    
    
}
