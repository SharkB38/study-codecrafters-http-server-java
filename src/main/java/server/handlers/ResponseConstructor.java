package server.handlers;

import java.util.HashMap;
import java.util.Map;

public class ResponseConstructor {
    private String status;
    private final Map<String, String[]> headers;
    private String body;

    public ResponseConstructor() {
        status = null;
        headers = new HashMap<>();
        body = null;
    }

    public void setStatus(int code) {
        status = "HTTP/1.1 ";
        switch (code) {
            case 200:
                status += code + " OK";
                break;
            case 201:
                status += code + " Created";
            case 404:
                status += code + " Not Found";
            default:
                status += "404 Not Found";
        }
    }

    public void addHeaders(String headerName, String[] headerParams) {
        if (!headerName.isEmpty() && headerParams.length > 0)
            headers.put(headerName, headerParams);
    }

    public void setBody(String bodyContent) {
        if (!bodyContent.isEmpty()) {
            body = bodyContent;
        }
    }

    public String constructResponse() {
        StringBuilder response = new StringBuilder();
        if (status == null) {
            System.out.println("Can't construct response, status is empty!");
            return null;
        }

        response.append(status);
        response.append("\r\n");
        if (!headers.isEmpty()) {
            for (String headerName : headers.keySet()) {
                response.append(headerName).append(": ");
                String headerParams = String.join(", ", headers.get(headerName));
                response.append(headerParams);
                response.append("\r\n");
            }
        }
        response.append("\r\n");

        if (body != null) {
            response.append(body);
        }

        return response.toString();
    }
}
