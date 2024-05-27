import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage
    //
    //
     try (final ExecutorService executorService = Executors.newCachedThreadPool()) {
         ServerSocket serverSocket = new ServerSocket(4221);
         serverSocket.setReuseAddress(true);
       while (true) {
           Socket clientSocket = serverSocket.accept();
           System.out.println("new connection accepted");
           executorService.submit(new ResponseHandler(clientSocket));
       }
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}
