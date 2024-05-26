import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage
    //
     ServerSocket serverSocket = null;
     Socket clientSocket = null;
    //
     try {
       serverSocket = new ServerSocket(4221);
       serverSocket.setReuseAddress(true);
       clientSocket = serverSocket.accept(); // Wait for connection from client.
         String response = "HTTP/1.1 404 Not Found\r\n\r\n";
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
         BufferedReader reader  =new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         String request;
         while (reader.ready()) {
             request = reader.readLine();
             if (request.contains("GET")) {
                 if (request.contains("/ "))
                     response = "HTTP/1.1 200 OK\r\n\r\n";
                 if (request.contains("/echo/")) {
                     int start = request.indexOf("/echo/") + "/echo/".length();
                     int end = request.indexOf(" HTTP");
                     String echo = request.substring(start, end);
                     response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-length: "
                             + echo.length() + "\r\n\r\n" + echo;
                 }
             }
         }
         writer.write(response);
         writer.flush();
       System.out.println("accepted new connection");
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}
