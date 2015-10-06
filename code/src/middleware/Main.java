package middleware;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
	private ServerSocket server;
	public RMconnection airplaneRMconnection;
	public RMconnection carRMconnection;
	public RMconnection hotelRMconnection;
	public RMconnection customerRMconnection;
	
	ArrayList<ConnectionWithClient> clientConnections = new ArrayList<ConnectionWithClient>();
	
	public Main(int port) throws Exception
	{
		server = new ServerSocket(port, 100);
	}
	
	public void loadAirplaneRMconnection(String host, int port) throws Exception
	{
		airplaneRMconnection = new RMconnection(host, port);
	}
	
	public void loadCarRMconnection(String host, int port) throws Exception
	{
		carRMconnection = new RMconnection(host, port);
	}
	
	public void loadHotelRMconnection(String host, int port) throws Exception
	{
		hotelRMconnection = new RMconnection(host, port);
	}
	
	public void loadCustomerRMconnection(String host, int port) throws Exception
	{
		customerRMconnection = new RMconnection(host, port);
	}
	
	public static void main(String[] args) throws Exception
	{
		
		if(args.length != 1)
		{
			System.out.println("Usage: java Main <service-port>");
	        System.exit(-1);
		}
		
		Main middleware = new Main(Integer.parseInt(args[0]));
		
		Scanner scanner = new Scanner(System.in);
		String[] connectionInfo;
		
		System.out.println("AirplaneRM: <service-IP>,<service-port>");
		connectionInfo = scanner.nextLine().split(",");
		middleware.loadAirplaneRMconnection(connectionInfo[0], Integer.parseInt(connectionInfo[1]));
		
		System.out.println("CarRM: <service-IP>,<service-port>");
		connectionInfo = scanner.nextLine().split(",");
		middleware.loadCarRMconnection(connectionInfo[0], Integer.parseInt(connectionInfo[1]));
		
		System.out.println("HotelRM: <service-IP>,<service-port>");
		connectionInfo = scanner.nextLine().split(",");
		middleware.loadHotelRMconnection(connectionInfo[0], Integer.parseInt(connectionInfo[1]));
		
		System.out.println("CustomerRM: <service-IP>,<service-port>");
		connectionInfo = scanner.nextLine().split(",");
		middleware.loadCustomerRMconnection(connectionInfo[0], Integer.parseInt(connectionInfo[1]));
		
		System.out.println("Waiting for connection...");
		
		middleware.run();
	}
	
	public void run() throws Exception
	{
		Socket connection;
		ConnectionWithClient temp;
		
		while(true)
		{
			connection = server.accept();
			temp = new ConnectionWithClient(this, connection);
			clientConnections.add(temp);
			temp.start();
			System.out.println("Someone has connected!!!");
		}
	}
}
