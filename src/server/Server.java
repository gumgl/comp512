package server;

import rmi.Receiver;
import system.ResourceManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Server {
	protected ServerSocket serverSocket;
	protected Receiver receiver;
	protected ResourceManager rm;

	public Server(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

	public abstract void start();
}
