package com.slackers.inc.Http;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class HttpRequest {

    public static enum HttpMethod {
        POST,
        GET;
    }
    private static final String AGENT = "SuperSlackersAutopullBot\\1.0";

    protected String url;
    protected HttpMethod method;
    protected List<String> cookies;

    protected Map<String, String> parameters;

    public HttpRequest(String urlString, HttpMethod method, Map<String, String> parameters) {
        this.url = urlString;
        this.method = method;
        this.parameters = parameters;
        this.cookies = new LinkedList<>();
    }

    public HttpRequest(String urlString, Map<String, String> parameters) {
        this(urlString, HttpMethod.POST, parameters);
    }

    public HttpRequest(String urlString) {
        this(urlString, HttpMethod.POST, new HashMap<>());
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void addParameter(String name, String value) {
        this.parameters.put(name, value);
    }

    public void addParameter(Map<String, String> values) {
        this.parameters.putAll(values);
    }

    private String buildParameterList() {
        StringBuilder s = new StringBuilder();
        boolean isFirst = true;
        for (Entry<String, String> e : this.parameters.entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                s.append("&");
            }
            s.append(e.getKey()).append("=").append(e.getValue());
        }
        return s.toString();
    }

    public void addCookie(String name, String value)
    {
        this.cookies.add(name+"="+value);
    }
    
    public HttpResponse submitRequest() {
        try {
            if (!(this.url.startsWith("http://") || this.url.startsWith("https://"))) {
                this.url = "http://" + this.url;
            }
            if (null != this.method) {
                switch (this.method) {
                    case POST: {
                        URL obj = new URL(this.url);
                        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("User-Agent", AGENT);
                        if (!this.cookies.isEmpty())
                            con.setRequestProperty("Cookie", String.join("; ", this.cookies));
                        con.setDoOutput(true);
                        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                            wr.writeBytes(this.buildParameterList());
                            wr.flush();
                        }
                        int responseCode = con.getResponseCode();
                        StringBuilder response = null;
                        try (BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream()))) {

                            String inputLine;
                            response = new StringBuilder();

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine).append("\n");
                            }

                        }
                        if (response != null) {
                            return new HttpResponse(responseCode, response.toString(), con.getHeaderFields());
                        }
                        break;
                    }
                    case GET: {
                        if (!this.url.contains("?")) // did not add parameters in url
                        {
                            this.url = this.url + "?" + this.buildParameterList();
                        }
                        URL obj = new URL(this.url);
                        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                        con.setRequestMethod("GET");
                        con.setRequestProperty("User-Agent", AGENT);
                        if (!this.cookies.isEmpty())
                            con.setRequestProperty("Cookie", String.join("; ", this.cookies));
                        int responseCode = con.getResponseCode();
                        StringBuilder response = null;
                        try (BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream()))) {

                            String inputLine;
                            response = new StringBuilder();

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine).append("\n");
                            }

                        }
                        if (response != null) {
                            return new HttpResponse(responseCode, response.toString(), con.getHeaderFields());
                        }
                        break;
                    }
                    default:
                        Logger.getLogger(HttpRequest.class.getName()).log(Level.SEVERE, "Unknown Request Method");
                        break;
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(HttpRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
