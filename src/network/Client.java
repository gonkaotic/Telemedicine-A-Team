package network;


import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Network {

    private boolean connected;
    protected String serverIP;
    protected Socket socket = null;
    protected ObjectOutputStream objectOutputStream = null;
    protected ObjectInputStream objectInputStream = null;

    /**
     * Connection to the server
     *
     * @return true if the connection was established false if an error occurred
     */
    public boolean connect() {
        try {
            connected = false;
            this.socket = new Socket(serverIP, SERVERPORT);

            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            connected = true;
        } catch (UnknownHostException e) {
            System.out.println("Unknown server");
            e.printStackTrace();
            releaseResources(objectInputStream, objectOutputStream, socket );
        } catch (IOException e) {
            System.out.println("It was not possible to connect to the server.");
            e.printStackTrace();
            releaseResources(objectInputStream, objectOutputStream, socket );
        } finally {
            //set to false in the "releaseResources" method
            return connected;
        }
    }


    /**
     * Disconnect from server and release resources
     */
    public void disconnect() {
        // Release input and output stream
        try {
            NetworkMessage msg = new NetworkMessage(NetworkMessage.Protocol.DISCONNECT);
            objectOutputStream.writeObject(msg);
        }catch(SocketException ex){
            System.out.println("Socket closed");
        } catch ( IOException e){
            System.out.println("Disconnection error");
            e.printStackTrace();
        } finally {
            releaseResources(objectInputStream, objectOutputStream, socket);
        }

    }

    //passing the parameters to the methods is kind of an overkill, as they are global variables, but lets work like that
    protected void releaseResources(InputStream input, OutputStream output,
                                         Socket socket) {

        connected = false;

        if( input != null) {
            try {
                input.close();
            } catch (IOException ex) {
                //Logger.getLogger(DoctorClient.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Input already closed by the other end");
            } finally {
                input = null;
            }
        }

        if ( output != null ) {
            try {
                output.close();
            } catch (IOException ex) {
                //Logger.getLogger(DoctorClient.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Output already closed by the other end");
            }finally {
                output = null;
            }
        }

        if ( socket != null ) {
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println("Socket already closed by the other end");
                //Logger.getLogger(DoctorClient.class.getName()).log(Level.SEVERE, null, ex);
            }finally {
                socket = null;
            }
        }

    }

    public boolean isConnected(){
        return connected;
    }


    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }


}
