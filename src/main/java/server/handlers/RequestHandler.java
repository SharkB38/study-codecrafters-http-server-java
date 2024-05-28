package server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {

    private final InputStream input;
    private String status;
    private String path;
    private final Map<String, String[]> headers;
    private String body = null;

    public RequestHandler(InputStream input) {
        this.input = input;
        headers = new HashMap<>();
    }

    public void parseRequest() {

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        try {
            String[] statusParam = reader.readLine().split(" ");


            status = statusParam[0];
            path = statusParam[1];

            String header;
            while (!(header = reader.readLine()).isEmpty()) {
                String[] headerContent = header.split(": ");
                String headerName = headerContent[0].toLowerCase();
                String[] headerParams = headerContent[1].split(", ");

                headers.put(headerName, headerParams);
            }
            if (headers.containsKey("content-length")) {
                StringBuilder bodyContent = new StringBuilder();
                while(reader.ready()) {
                    bodyContent.append((char) reader.read());
                }

                body = bodyContent.toString();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String[]> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
