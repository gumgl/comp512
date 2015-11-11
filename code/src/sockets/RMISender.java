package sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/*
 * Helper class for SocketResourceManager
 */

public class RMISender {

    Socket serverSocket;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;

    public RMISender(InetAddress host, int port) {
        while(true) {
            try {
                System.out.printf("Connecting to server %s:%d...\n",host.toString(),port);
                serverSocket = new Socket(host, port);
                System.out.println("Connected!");
                outStream = new ObjectOutputStream(serverSocket.getOutputStream());
                System.out.println("OutputStream created");
                outStream.flush();
                System.out.println("OutputStream flushed");
                inStream = new ObjectInputStream(serverSocket.getInputStream());

                break; // Successful connection, break out of loop

            } catch(IOException e){
                System.err.println("Failed to connect to server, trying again...");
            }
        }
    }

    public Object invoke(RMI rmi) throws Exception {
        System.out.println("Sending RMI to server: " + rmi.toString());
        outStream.writeObject(rmi);
        System.out.println("Sent! Waiting for response...");
        RMIResponse response = (RMIResponse) inStream.readObject();
        System.out.println("Response received!");

        if (response.getException() != null)
            throw response.getException();
        else
            return response.getReturnValue();
    }
}
