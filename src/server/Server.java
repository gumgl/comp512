package server;

import middleware.MiddlewareResourceManager;
import rmi.Receiver;
import rmi.SocketSender;
import system.LocalResourceManager;
import system.ResourceManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Server {
	protected ExecutorService executorService = Executors.newFixedThreadPool(4);
	protected ServerSocket serverSocket;
	protected ArrayList<ResourceManager> rms;
	protected boolean active = true;

	public Server(int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
	}

	public void start() {
		Trace.info("Server starting...");
		// the MiddlewareResourceManager will forward all received RMIs to the specific RMs

		Thread.setDefaultUncaughtExceptionHandler(
			new Thread.UncaughtExceptionHandler() {
				@Override public void uncaughtException(Thread t, Throwable e) {
					Trace.info("Client connection lost", e);
				}
			});

		while (this.active) { // While we are active
			try { // Accept incoming client connections
				//Trace.info("Waiting for client connection...");
				Socket clientSocket = serverSocket.accept();
				Trace.info("New client! Setting up...");

				ResourceManager rm = setupRM();
				Receiver receiver = new Receiver(clientSocket, rm);
				executorService.execute(receiver);
				Trace.info("Setup done");
			} /*catch (UncheckedIOException e) { // Meant to come from receiver but we won't get it since it's in a thread pool
				Trace.info("Client connection lost", e.getCause());
			}*/ catch (IOException e) {
				Trace.error("Problem with the ServerSocket");
				e.printStackTrace(System.err);
			}
		}
		Trace.info("Server shut down. Good bye.");
	}

	/* Called when we receive a new client connection and create a new thread. The receiver will pass
	 * RMI calls to this RM */
	public abstract ResourceManager setupRM();
}
