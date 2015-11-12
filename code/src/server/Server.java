package server;

import rmi.Receiver;
import system.IResourceManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Server implements Runnable {
    ServerSocket serverSocket;
    ExecutorService executorService;
    Receiver receiver;
    IResourceManager rm;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        executorService = Executors.newFixedThreadPool(4);
    }

    /* RM who will receive all RMIs */
    protected abstract IResourceManager setupResourceManager();

    /* Accept an incoming socket connection */
    @Override
    public void run() {
        // Setup our RM receiving the RMIs
        rm = setupResourceManager();

        while (true) {
            try {
                Trace.info("Waiting for a connection over sockets...");
                Socket clientSocket = serverSocket.accept();
                Trace.info("Connection found!");
                receiver = new Receiver(clientSocket, rm);
                executorService.execute(receiver);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Shutdown gracefully */
    abstract public boolean shutdown();
}
