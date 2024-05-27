import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage
    //
    //
     try {
         ServerSocket serverSocket = new ServerSocket(4221);
         serverSocket.setReuseAddress(true);
         Socket clientSocket = serverSocket.accept();
         System.out.println("new connection accepted");
         (new ResponseHandler(clientSocket)).start();
       while (true) {
           clientSocket = serverSocket.accept();
           System.out.println("new connection accepted");
           (new ResponseHandler(clientSocket)).start();
       }
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}
