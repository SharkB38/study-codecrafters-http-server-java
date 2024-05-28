public class Main {

    public static void main(String[] args) {

        System.out.println("Logs from your program will appear here!");
        Server server = new Server(4221, args);
        server.startServer();

    }

}
