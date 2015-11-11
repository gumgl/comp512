package server;

import sockets.RMIReceiver;
import system.ResourceManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Server implements Runnable {
    ServerSocket serverSocket;
    ExecutorService executorService;
    RMIReceiver receiver;
    ResourceManager rm;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        executorService = Executors.newFixedThreadPool(4);
    }

    protected abstract ResourceManager setupResourceManager();

    /* Accept an incoming socket connection */
    @Override
    public void run() {
        // Different types of servers have different RMs
        rm = setupResourceManager();

        while (true) {
            try {
                System.out.println("Waiting for a connection over sockets...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection found!");
                receiver = new RMIReceiver(clientSocket, rm);
                executorService.execute(receiver);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Shutdown gracefully */
    abstract public boolean shutdown();
}
