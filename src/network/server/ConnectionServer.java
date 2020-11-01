package network.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import network.Network;

public class ConnectionServer implements Network {	
	
	public static void main( String[] args ) {
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(PORT);
			acceptConnection ( server.accept() );
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			releaseResources(server);
		}
	}
	
	private static void acceptConnection(Socket s) {
		if ( s != null ) {
			InputStream inputStream = null;
			try {
				inputStream = s.getInputStream();
				
//				int byteRead;
//		        while (true) {
//		            byteRead = inputStream.read();        
//		            if (byteRead == -1 || byteRead == 'x') {
//		                System.out.println("Character reception finished");
//		                System.exit(0);
//		            }
//		            char caracter = (char) byteRead;
//		            System.out.print(caracter);
//		            System.out.print(" ");
//		        }
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(inputStream != null) {
					try {
						inputStream.close();
					} catch ( IOException e) {
						e.printStackTrace();
					}
				}
				
				try {
					s.close();
				} catch ( IOException e) {
					e.printStackTrace();
				}
			}
	        
		}
	}
	
	private static void releaseResources(ServerSocket server) {
		if( server != null) {
			try {
				server.close();
			} catch ( IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
