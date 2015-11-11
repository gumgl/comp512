package middleware;

import server.SocketResourceManager;
import server.Server;
import system.ResourceManager;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class MiddleWareServer extends Server {
    // Simply for setup so we have access to them in setupResourceManager()
    ArrayList<InetAddress> RMaddresses;
    ArrayList<Integer> RMports;

    public static void main(String[] args) throws Exception {
        int offset = 1;
        int numRMs = 3;

        int port = Integer.parseInt(args[0]);

        ArrayList<InetAddress> RMaddresses = new ArrayList<InetAddress>();
        ArrayList<Integer> RMports = new ArrayList<Integer>();

        for (int i=offset; i<offset+numRMs*2; i += 2) {
            RMaddresses.add(InetAddress.getByName(args[i]));
            RMports.add(Integer.parseInt(args[i+1]));
        }

        Server server = new MiddleWareServer(port, RMaddresses, RMports);
        server.run();
    }

    public MiddleWareServer(int port, ArrayList<InetAddress> RMaddresses, ArrayList<Integer> RMports) throws IOException {
        super(port);

        // Store addresses and ports so that when Server calls setupResourceManager, we have access to them
        this.RMaddresses = RMaddresses;
        this.RMports = RMports;
    }

    @Override
    protected ResourceManager setupResourceManager() {
        System.out.println("Trying to connect to the ResourceManagers...");
        // the MiddleWareResourceManager will forward all received RMIs to the specific RMs
        return new MiddleWareResourceManager(
                new SocketResourceManager(RMaddresses.get(0), RMports.get(0)),
                new SocketResourceManager(RMaddresses.get(1), RMports.get(1)),
                new SocketResourceManager(RMaddresses.get(2), RMports.get(2)));
    }
}
