package network.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.logging.Level;
import java.util.logging.Logger;

import network.Network;
import network.NetworkMessage;


/**
 * This class 
 * 
 * @author LolaA
 *
 */
public class Client implements  Network{
	
	private String serverIP;
	private Socket socket;
	OutputStream outputStream = null;
	InputStream inputStream=null;
    ObjectOutputStream objectOutputStream = null; 
    ObjectInputStream objectInputStream = null; 
    NetworkMessage msg;
	/**
	 * Client constructor
	 */
	public Client(String serverIP) {
		this.serverIP=serverIP;
	}
	
	
	/**
	 * Connection to the server
	 * 
	 * @return true if the connection was established
	 *  	   false if an error occurred
	 */
	public boolean connect() {
		try {
			this.socket= new Socket(serverIP,SERVERPORT);
			outputStream = socket.getOutputStream();    
			return true;
		} catch (UnknownHostException e) {
			System.out.println("It was not possible to connect to the server.");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.out.println("It was not possible to connect to the server.");
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	/**
	 * Disconnect from server and release resources
	 */
	public void disconnect() {
		//Release input and output stream
		releaseResources(objectInputStream, objectOutputStream, socket);
		// I would define a command to be sent to the server
		
	}
	
	public boolean sendToServer() {
	
		while(true) {
		try {
			//Here: out and in streams			
		
			objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(msg);
            objectInputStream = new ObjectInputStream(inputStream);
            NetworkMessage msg_read = (NetworkMessage) inputStream.readObject();
            
            if (msg.getProtocol() == NetworkMessage.Protocol.GET_PATIENT) {
            	System.out.println("Starting");
               
            }
            if (msg.getProtocol() == NetworkMessage.Protocol.DENY_PATIENT) {
            	System.out.println("Finish");
                releaseResources3(outputStream, socket);
                System.exit(0);
            }
            
            objectOutputStream.flush();//just in case
           
        } catch (IOException ex) {
            System.out.println("It is not possible to connect with the Server");
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            releaseResources2(objectOutputStream, socket);

        }
		
		return true;
	}}
	
	private static void releaseResources(ObjectInputStream objectInputStream,ObjectOutputStream objectOutputStream, 
			Socket socket) {
        try {
            objectInputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            objectOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
	private static void releaseResources2(ObjectOutputStream objectOutputStream, 
			Socket socket) {
       
        try {
            objectOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
	private static void releaseResources3(OutputStream outputStream, 
			Socket socket) {
       
        try {
           outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
