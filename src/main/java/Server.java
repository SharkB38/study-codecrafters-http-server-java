import server.handlers.ResponseHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int port;
    private final String[] args;

    public Server(int port, String[] args) {
        this.port = port;
        this.args = args;
    }

    public void startServer() {
        try (
                ServerSocket serverSocket = new ServerSocket(port);
                ExecutorService executorService = Executors.newCachedThreadPool()
        ) {

            System.out.println("Server is started...");
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection accepted...");
                executorService.submit(new ResponseHandler(clientSocket, args));
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Server stopped");
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
