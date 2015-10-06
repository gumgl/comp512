package server.ws;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import server.ResourceManagerImpl;


public class Main
{
	
	ObjectOutputStream output;
 	ObjectInputStream input;
 	ServerSocket server;
 	Socket connection;
 	
 	ResourceManagerImpl rManager;
	
	private Main(int port) throws IOException
	{
		server = new ServerSocket(port, 100);
		rManager = new ResourceManagerImpl();
	}

    public static void main(String[] args) 
    throws Exception {
    
        if (args.length != 1) {
            System.out.println(
                "Usage: java Main <service-port>");
            System.exit(-1);
        }
    
        int port = Integer.parseInt(args[0]);
        
       
    	Main main = new Main(port);
    	main.run();
    }
    
    private void run()
    {
    	while(true)
    	{
    		try
    		{
    			System.out.println("Waiting for someone to connect...");
    			connection = server.accept();
    			System.out.println("Connected!");
    			System.out.println();
    	    
    	    
    			output = new ObjectOutputStream(connection.getOutputStream());
    			output.flush();
    			input = new ObjectInputStream(connection.getInputStream());
    	    
    			String message = "";
    			String[] args;
    			String toReturn;
    	    
    			while(true)
    			{
    				message = (String)input.readObject();
    				args = message.split(",");
    				toReturn = doAction(findChoice(args[0]), args);
    				output.writeObject(toReturn);
    				output.flush();
    			}
    		}
    		catch(Exception e)
    		{
    			System.out.println("Connection lost.");
    		}
    	}
    }
    
    
    private int findChoice(String argument)
    {
        if (argument.compareToIgnoreCase("help") == 0)
            return 1;
        else if (argument.compareToIgnoreCase("newflight") == 0)
            return 2;
        else if (argument.compareToIgnoreCase("newcar") == 0)
            return 3;
        else if (argument.compareToIgnoreCase("newroom") == 0)
            return 4;
        else if (argument.compareToIgnoreCase("newcustomer") == 0)
            return 5;
        else if (argument.compareToIgnoreCase("deleteflight") == 0)
            return 6;
        else if (argument.compareToIgnoreCase("deletecar") == 0)
            return 7;
        else if (argument.compareToIgnoreCase("deleteroom") == 0)
            return 8;
        else if (argument.compareToIgnoreCase("deletecustomer") == 0)
            return 9;
        else if (argument.compareToIgnoreCase("queryflight") == 0)
            return 10;
        else if (argument.compareToIgnoreCase("querycar") == 0)
            return 11;
        else if (argument.compareToIgnoreCase("queryroom") == 0)
            return 12;
        else if (argument.compareToIgnoreCase("querycustomer") == 0)
            return 13;
        else if (argument.compareToIgnoreCase("queryflightprice") == 0)
            return 14;
        else if (argument.compareToIgnoreCase("querycarprice") == 0)
            return 15;
        else if (argument.compareToIgnoreCase("queryroomprice") == 0)
            return 16;
        else if (argument.compareToIgnoreCase("reserveflight") == 0)
            return 17;
        else if (argument.compareToIgnoreCase("reservecar") == 0)
            return 18;
        else if (argument.compareToIgnoreCase("reserveroom") == 0)
            return 19;
        else if (argument.compareToIgnoreCase("itinerary") == 0)
            return 20;
        else if (argument.compareToIgnoreCase("quit") == 0)
            return 21;
        else if (argument.compareToIgnoreCase("newcustomerid") == 0)
            return 22;
        else if (argument.compareToIgnoreCase("newcustomid") == 0)
            return 23;
        else
            return 666;
    }
    
    
    private String doAction(int actionCode, String[] args)
    {
    	String toReturn = "";
    	switch(actionCode) {
            
        case 2:  //new flight
        	toReturn = Boolean.toString(rManager.addFlight(getInt(args[1]), getInt(args[2]), getInt(args[3]), getInt(args[4])));
            break;
            
        case 3:  //new car
            toReturn = Boolean.toString(rManager.addCars(getInt(args[1]), args[2], getInt(args[3]), getInt(args[4])));
            break;
            
        case 4:  //new room
            toReturn = Boolean.toString(rManager.addRooms(getInt(args[1]), args[2], getInt(args[3]), getInt(args[4])));
            break;
            
        case 5:  //new Customer
            toReturn = Integer.toString(rManager.newCustomer(getInt(args[1])));
            break;
            
        case 6: //delete Flight
            toReturn = Boolean.toString(rManager.deleteFlight(getInt(args[1]), getInt(args[2])));
            break;
            
        case 7: //delete car
            toReturn = Boolean.toString(rManager.deleteCars(getInt(args[1]), args[2]));
            break;
            
        case 8: //delete room
            toReturn = Boolean.toString(rManager.deleteRooms(getInt(args[1]), args[2]));
            break;
            
        case 9: //delete Customer
            String toString = Boolean.toString(rManager.deleteCustomer(getInt(args[1]), getInt(args[2])));
            break;
            
        case 10: //querying a flight
            toReturn = Integer.toString(rManager.queryFlight(getInt(args[1]), getInt(args[2])));
            break;
            
        case 11: //querying a car Location
            toReturn = Integer.toString(rManager.queryCars(getInt(args[1]), args[2]));
            break;
            
        case 12: //querying a room location
            toReturn = Integer.toBinaryString(rManager.queryRooms(getInt(args[1]), args[2]));
            break;
            
        case 13: //querying Customer Information
            toReturn = rManager.queryCustomerInfo(getInt(args[1]), getInt(args[2]));
            break;               
            
        case 14: //querying a flight Price
            toReturn = Integer.toString(rManager.queryFlightPrice(getInt(args[1]), getInt(args[2])));
            break;
            
        case 15: //querying a car Price
            toReturn = Integer.toString(rManager.queryCarsPrice(getInt(args[1]), args[2]));             
            break;

        case 16: //querying a room price
            toReturn = Integer.toString(rManager.queryRoomsPrice(getInt(args[1]), args[2]));
            break;
            
        case 17:  //reserve a flight
            toReturn = Boolean.toString(rManager.reserveFlight(getInt(args[1]), getInt(args[2]), getInt(args[3])));
            break;
            
        case 18:  //reserve a car
            toReturn = Boolean.toString(rManager.reserveCar(getInt(args[1]), getInt(args[2]), args[3]));
            break;
            
        case 19:  //reserve a room
            toReturn = Boolean.toString(rManager.reserveRoom(getInt(args[1]), getInt(args[2]), args[3]));
            break;
            
        case 20:  //reserve an Itinerary
        	toReturn = Boolean.toString(false);
            break;
            
        case 22:  //new Customer given id
            toReturn = Boolean.toString(rManager.newCustomerId(getInt(args[1]), getInt(args[2])));
            break;
            
        case 23:  //new Customer with custom id
        	rManager.newCustomId(getInt(args[1]), getInt(args[2]));
            //toReturn = getInt(args[2]) + "";
            break;
            
        default:
            System.out.println("The interface does not support this command.");
            break;
        }
    	
    	return toReturn;
    }
    
    private int getInt(String string)
    {
    	return Integer.parseInt(string);
    }
    
}
