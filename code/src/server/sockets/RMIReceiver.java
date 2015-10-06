package server.sockets;

import server.ResourceManagerImpl;
import server.ws.ResourceManager;

import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import java.util.Arrays;

public class RMIReceiver implements Runnable {
    Socket clientSocket;
    ObjectInputStream inStream;
    ObjectOutputStream outputStream;
    ResourceManager rm;

    public RMIReceiver(Socket clientSocket, ResourceManager rm) {
        this.clientSocket = clientSocket;
        this.rm = rm;
    }

    @Override
    public void run() {
        try {
            // A little hack because by default the OIS listens for a message at connection time: http://stackoverflow.com/q/5658089/646562
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outputStream.flush();

            inStream = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("InputStream created");

            while(true) { // Read requests forever...
                System.out.println("Waiting for an RMI...");
                RMI rmi = (RMI) inStream.readObject();
                RMIResponse response = handleRMI(rmi);
                System.out.println("Sending back response...");
                outputStream.writeObject(response);
            }
        } catch(SocketException e) {
            System.out.println("Client disconnected (" + e + ")");
        } catch (Exception e) {
            System.err.println("Socket stream failed!");
            e.printStackTrace();
        }
    }

    public RMIResponse handleRMI(RMI rmi) {
        System.out.println("Received RMI: " + rmi.toString());

        try {
            // Naive method search going by name and not complete signature (parameter types)
            Method method = null;
            for (Method m : rm.getClass().getMethods()) { // Search in the RM's methods
                if (m.getName().equals(rmi.getMethodName())) {
                    method = m;
                    break;
                }
            }
            if (method == null)
                throw new NoSuchMethodException();

            // Method m = rm.getClass().getDeclaredMethod(rmi.getMethodName()); // Does not work because we don't know the types
            Object result = method.invoke(rm, rmi.getParams().toArray());
            return new RMIResponse(result);
        } catch (NoSuchMethodException e) {
            System.err.println("Method not found");
            e.printStackTrace();
            return RMIResponse.error(e);
        } catch (Exception e) {
            System.err.println("Error in response");
            e.printStackTrace();
            return RMIResponse.error(e);
        }

    }
}
