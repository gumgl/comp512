package server.sockets;

import server.ResourceManagerImpl;
import server.ws.ResourceManager;

import java.io.IOError;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    ServerSocket serverSocket;
    RMIReceiver receiver;
    ResourceManager rm;
    ExecutorService executorService;

    public static void main(String[] args)
            throws Exception {

        if (args.length != 2) {
            System.out.println(
                    "Usage: MyServer <host> <port>");
            System.exit(-1);
        }

        InetAddress host = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);

        System.out.println("hello from Server main?");

        Server server = new Server(host, port);
        server.run();
    }

    public Server(InetAddress host, int port) throws IOException {
        serverSocket = new ServerSocket(port);
        rm = new ResourceManagerImpl();
        executorService = Executors.newFixedThreadPool(4);
    }

    // Accept an incoming socket connection
    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Waiting for a client to connect...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection found!");
                receiver = new RMIReceiver(clientSocket, rm);
                executorService.execute(receiver);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
