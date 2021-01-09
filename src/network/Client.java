package network;

import network.DoctorClient.DoctorClient;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

public class Client implements Network {

    private KeyPair keyPair;
    private boolean connected;
    protected String serverIP;
    protected Socket socket = null;
    protected ObjectOutputStream objectOutputStream = null;
    protected ObjectInputStream objectInputStream = null;

    private Key serverKey;

    /**
     * Connection to the server
     *
     * @return true if the connection was established false if an error occurred
     */
    public boolean connect() {
        try {
            connected = false;

            //generate the keys for RSA algorithm
            KeyPairGenerator keysGenerator = KeyPairGenerator.getInstance("RSA");
            keysGenerator.initialize(KEY_SIZE);
            keyPair = keysGenerator.genKeyPair();

            this.socket = new Socket(serverIP, SERVERPORT);

            //first connection isn't encrypted, to share public keys.
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            objectOutputStream.writeObject( new NetworkMessage( NetworkMessage.Protocol.PUSH_KEY, keyPair.getPublic() ));

            NetworkMessage answer = ( NetworkMessage ) objectInputStream.readObject();
            if ( answer.getProtocol() == NetworkMessage.Protocol.PUSH_KEY ) {
                serverKey = answer.getKey();
            } else if (answer.getProtocol() == NetworkMessage.Protocol.ERROR) {
                //connected is always false here, it means the server had an error establishing the encryption
                return connected;
            }

            //objectOutputStream.close();
            //objectInputStream.close();

            System.out.println("Creating the cypher");
            //now that we have the public key of the server we encrypt our communications.
            //AES stands for Advance Encryption Standard
            Cipher cipherOut = Cipher.getInstance(encryptionAlgorithm) ;
            cipherOut.init( Cipher.ENCRYPT_MODE, serverKey);

            System.out.println("Initializing the cypher");
            Cipher cipherIn = Cipher.getInstance(encryptionAlgorithm) ;
            cipherIn.init( Cipher.DECRYPT_MODE, keyPair.getPrivate());


            System.out.println("new cyphered streams.");
            CipherInputStream cis = new CipherInputStream(socket.getInputStream(), cipherIn);
            CipherOutputStream cos = new CipherOutputStream(socket.getOutputStream(), cipherOut);

            System.out.println("setting them up");
            objectInputStream = new ObjectInputStream(cis);
            System.out.println("setting them up2");
            objectOutputStream = new ObjectOutputStream(cos);

            System.out.println("ready!");
            connected = true;

        } catch (UnknownHostException e) {
            System.out.println("Unknown server");
            e.printStackTrace();
            releaseResources(objectInputStream, objectOutputStream, socket );
        } catch (IOException e) {
            System.out.println("There was an error trying to connect to the server.");
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
