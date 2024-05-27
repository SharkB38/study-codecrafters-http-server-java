import java.io.*;
import java.net.Socket;

public class ResponseHandler extends Thread {

    private final Socket clientSocket;

    public ResponseHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String response = "HTTP/1.1 404 Not Found\r\n\r\n";

            String request;
            boolean agentFlag = false;
            while (reader.ready()) {
                request = reader.readLine();
                System.out.println(request + "//");
                if (request.contains("GET")) {
                    if (request.contains("/ "))
                        response = "HTTP/1.1 200 OK\r\n\r\n";
                    if (request.contains("/echo/")) {
                        int start = request.indexOf("/echo/") + "/echo/".length();
                        int end = request.indexOf(" HTTP");
                        String echo = request.substring(start, end);
                        response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-length: "
                                + echo.length() + "\r\n\r\n" + echo;
                    } else if (request.contains("/user-agent")) {
                        agentFlag = true;
                    }
                }
                if (request.contains("User-Agent") && agentFlag) {
                    int start = request.indexOf(" ") + 1;
                    int end = request.length();
                    String agent = request.substring(start, end);
                    response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-length: "
                            + agent.length() + "\r\n\r\n" + agent;
                }
            }
            writer.write(response);
            writer.flush();

            reader.close();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
