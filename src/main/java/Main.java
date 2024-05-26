import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage
    //
     //ServerSocket serverSocket = null;
     //Socket clientSocket = null;
    //
     try {
         ServerSocket serverSocket = new ServerSocket(4221);
       serverSocket.setReuseAddress(true);
       while (true) {
           Thread socketThread = new Thread(() -> {
               try {
                   Socket clientSocket = serverSocket.accept(); // Wait for connection from client.
                   BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                   BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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
                   System.out.println("accepted new connection");
               } catch (IOException e) {
                   System.out.println("IOException: " + e.getMessage());
               }
           });
           socketThread.start();
       }
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}
