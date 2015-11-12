package rmi;

import server.Trace;
import system.ResourceManager;

import java.lang.reflect.Method;
import java.net.*;
import java.io.*;

/*
 * This class will pass the RMIs received from a socket connection to the localResource object;
 */
public class Receiver implements Runnable {
    Socket clientSocket;
    ObjectInputStream inStream;
    ObjectOutputStream outputStream;
    ResourceManager localResource;

    public Receiver(Socket clientSocket, ResourceManager localResource) {
        this.clientSocket = clientSocket;
        this.localResource = localResource;
    }

    @Override
    public void run() {
        try {
            // A little hack because by default the OIS listens for a message at connection time: http://stackoverflow.com/q/5658089/646562
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outputStream.flush();

            inStream = new ObjectInputStream(clientSocket.getInputStream());
            Trace.info("InputStream created");

            while(localResource.active) { // Read requests forever...
                Trace.info("Waiting for an Invocation...");
                Invocation invocation = (Invocation) inStream.readObject();
                Response response = handleRMI(invocation);
                Trace.info("Sending back response...");
                outputStream.writeObject(response);
            }
            Trace.info("This receiver stops receiving.");
        } catch(SocketException e) {
            Trace.info("Client disconnected (" + e + ")");
        } catch (Exception e) {
            Trace.error("Socket stream failed!");
            e.printStackTrace();
        }
    }

    public Response handleRMI(Invocation invocation) {
        Trace.info("Received Invocation: " + invocation.toString());

        try {
            // Naive method search going by name and parameter count. Not complete signature (ignores types)
            Method method = null;
            for (Method m : localResource.getClass().getMethods()) { // Search in the RM's methods
                if (m.getName().equals(invocation.getMethodName()) && m.getParameterCount() == invocation.getParamCount()) {
                    //System.out.printf("m.getParameterCount()=%d, invocation.getParamCount()=%d\n", m.getParameterCount(), invocation.getParamCount());
                    method = m;
                    break;
                }
            }
            if (method == null)
                throw new NoSuchMethodException();

            // Method m = rm.getClass().getDeclaredMethod(invocation.getMethodName()); // Does not work because we don't know the types
            Object result = method.invoke(localResource, invocation.getParams().toArray());
            return new Response(result);
        } catch (NoSuchMethodException e) {
            Trace.error("Method not found");
            e.printStackTrace();
            return Response.error(e);
        } catch (Exception e) {
            Trace.error("Uncaught exception, bundling with response.");
            e.printStackTrace();
            return Response.error(e);
        }

    }
}
