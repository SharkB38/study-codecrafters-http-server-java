import java.io.*;
import java.net.Socket;
import java.util.stream.Collectors;

public class ResponseHandler extends Thread {

    private final Socket clientSocket;
    private final String[] argv;

    public ResponseHandler(Socket clientSocket, String[] argv) {
        this.clientSocket = clientSocket;
        this.argv = argv;
    }

    @Override
    public void run() {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String response = "HTTP/1.1 404 Not Found\r\n\r\n";

            String request = reader.readLine();
                //System.out.println(request + "//");
            if (request.contains("GET")) {
                System.out.println(request);
                if (request.contains("/ ")) {
                    response = "HTTP/1.1 200 OK\r\n\r\n";
                } else if (request.contains("/echo/")) {
                    int start = request.indexOf("/echo/") + "/echo/".length();
                    int end = request.indexOf(" HTTP");
                    String echo = request.substring(start, end);
                    response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-length: "
                            + echo.length() + "\r\n\r\n" + echo;
                } else if (request.contains("/user-agent")) {
                    while (!request.contains("User-Agent"))
                        request = reader.readLine();
                    int start = request.indexOf(" ") + 1;
                    int end = request.length();
                    String agent = request.substring(start, end);
                    response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-length: "
                            + agent.length() + "\r\n\r\n" + agent;
                } else if (request.contains("/files/")) {
                    if (argv[0].equals("--directory")) {
                        String path = argv[1];
                        int start = request.indexOf("/files/") + "/files/".length();
                        int end = request.indexOf(" HTTP");
                        path += request.substring(start, end);
                        System.out.println(path);
                        File file = new File(path);
                        if (file.exists())
                            System.out.println("file exists");
                        try (FileReader fr = new FileReader(file)) {
                            BufferedReader fileReader = new BufferedReader(fr);
                            StringBuilder content = new StringBuilder();
                            content.append(fileReader.lines().collect(Collectors.joining("\n")));
                            response = "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-length: "
                                    + content.toString().length() + "\r\n\r\n" + content;
                        } catch (IOException e) {

                        }
                    }
                }
            }
            System.out.println(response);
            writer.write(response);
            writer.flush();

            reader.close();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
