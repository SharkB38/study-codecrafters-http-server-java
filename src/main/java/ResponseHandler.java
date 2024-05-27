import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

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
                    int echoLength = echo.length();
                    while (!request.toLowerCase().contains("accept-encoding:") && !request.isEmpty()) {
                        request = reader.readLine();
                    }
                    response = "HTTP/1.1 200 OK\r\n";
                    String[] encoding = {};
                    if (request.toLowerCase().contains("accept-encoding:")) {
                        encoding = request.substring("accept-encoding: ".length()).split(", ");
                        for (String encode : encoding) {
                            if (encode.equals("gzip")) {
                                response += "Content-Encoding: " + encode + "\r\n";
                                ByteArrayOutputStream obj = new ByteArrayOutputStream();
                                GZIPOutputStream gzip = new GZIPOutputStream(obj);
                                gzip.write(echo.getBytes(StandardCharsets.UTF_8));
                                gzip.close();
                                echo = Base64.getEncoder().encodeToString(obj.toByteArray());
                                echoLength = obj.toByteArray().length;
                                obj.close();
                                break;
                            }
                        }
                    }
                    response += "Content-Type: text/plain\r\nContent-length: "
                            + echoLength + "\r\n\r\n" + echo;

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
            } else if (request.contains("POST")) {
                if (argv[0].equals("--directory")) {
                    System.out.println(request);
                    int start = request.indexOf("/files/") + "/files/".length();
                    int end = request.indexOf(" HTTP");
                    String filename = request.substring(start, end);
                    StringBuilder requestText = new StringBuilder();
                    String line;
                    while (!(line = reader.readLine()).isEmpty()) {
                        requestText.append(line).append("\n");
                    }
                    String path;

                    path = argv[1];
                    path += filename;
                    StringBuilder content = new StringBuilder();
                    while (reader.ready()) {
                        content.append((char) reader.read());
                    }
                    File file = new File(path);
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(content.toString().getBytes());
                    response = "HTTP/1.1 201 Created\r\n\r\n";

                    System.out.println(requestText);
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
