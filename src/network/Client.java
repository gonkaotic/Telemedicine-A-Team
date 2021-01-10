package network;

import network.DoctorClient.DoctorClient;

import javax.crypto.*;
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
import java.util.Arrays;

public class Client implements Network {

    private KeyPair keyPair;
    private boolean connected;
    protected String serverIP;
    protected Socket socket = null;
    protected ObjectOutputStream objectOutputStream = null;
    protected ObjectInputStream objectInputStream = null;
    protected DataOutputStream dataOutputStream = null;
    protected DataInputStream dataInputStream = null;
    protected Cipher cipherOut;
    protected Cipher cipherIn;

    private Key serverKey;

    protected NetworkMessage deserialize(byte[] byteArray) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
            ObjectInputStream in = new ObjectInputStream(bis);
            return (NetworkMessage) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected byte[] serialize(NetworkMessage message) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(message);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


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

            objectOutputStream.writeObject(new NetworkMessage(NetworkMessage.Protocol.PUSH_KEY, keyPair.getPublic()));

            NetworkMessage answer = (NetworkMessage) objectInputStream.readObject();
            if (answer.getProtocol() == NetworkMessage.Protocol.PUSH_KEY) {
                serverKey = answer.getKey();
            } else if (answer.getProtocol() == NetworkMessage.Protocol.ERROR) {
                //connected is always false here, it means the server had an error establishing the encryption
                return connected;
            }

            //objectOutputStream.close();
            //objectInputStream.close();

            System.out.println("Creating the cypher");
            cipherOut = Cipher.getInstance(encryptionAlgorithm);
            cipherOut.init(Cipher.ENCRYPT_MODE, serverKey);

            System.out.println("Initializing the cypher");
            cipherIn = Cipher.getInstance(encryptionAlgorithm);
            cipherIn.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());

            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            System.out.println("ready!");
            connected = true;

        } catch (UnknownHostException e) {
            System.out.println("Unknown server");
            e.printStackTrace();
            releaseResources(objectInputStream, objectOutputStream, socket);
        } catch (IOException e) {
            System.out.println("There was an error trying to connect to the server.");
            e.printStackTrace();
            releaseResources(objectInputStream, objectOutputStream, socket);
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
        } catch (SocketException ex) {
            System.out.println("Socket closed");
        } catch (IOException e) {
            System.out.println("Disconnection error");
            e.printStackTrace();
        } finally {
            releaseResources(objectInputStream, objectOutputStream, socket);
        }

    }

    /**
     * Sends an encrypted NetworkMessage to the server. First it sends the size in bytes of the message to be sent
     *
     * @param message
     */
    public void sendMessageToServer(NetworkMessage message) throws IOException {
        try {
            if (dataOutputStream != null) {
                byte[] data = serialize(message);
                int size = (int) (Math.ceil(data.length / MAX_ENCRYPTION_SIZE)) * ENCRYPTED_SIZE;
                byte[] bytesToSend = new byte[size];
                dataOutputStream.writeInt(size);
                for (int i = 0, round = 0; i < data.length; i = i + MAX_ENCRYPTION_SIZE, round = round + ENCRYPTED_SIZE) {
                    if (i + MAX_ENCRYPTION_SIZE < data.length) {
                        System.arraycopy(cipherOut.doFinal(Arrays.copyOfRange(data, i, i + MAX_ENCRYPTION_SIZE - 1)), 0, bytesToSend, round, ENCRYPTED_SIZE);
                    } else {
                        System.arraycopy(cipherOut.doFinal(Arrays.copyOfRange(data, i, data.length - 1)), 0, bytesToSend, round, ENCRYPTED_SIZE);
                    }
                }
                dataOutputStream.write(bytesToSend);
            }
        } catch (BadPaddingException e) {
            e.printStackTrace();
            System.out.println("Error with the encryption");
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            System.out.println("Error with the encryption");
        }
    }

    /**
     * Receives a message from server
     *
     * @return the message received from the server
     * null if there was any problem with the encryption process or if dataInputStream is null
     * @throws IOException
     */
    public NetworkMessage getMessageFromServer() throws IOException {
        try {
            if (dataInputStream != null) {
                int size = dataInputStream.readInt();
                System.out.println("Size: "+size);
                byte[] byteArray = new byte[size];
                dataInputStream.read(byteArray);
                int numberOfBlocks = size / ENCRYPTED_SIZE;
                System.out.println("Number of blocks: "+numberOfBlocks);
                byte[] decryptedArray = new byte[numberOfBlocks * MAX_ENCRYPTION_SIZE];

                for (int i = 0, coveredSize = 0; i < size; i=i+ENCRYPTED_SIZE) {
                    System.out.println("i: "+i);
                    byte[] decrypted = cipherIn.doFinal(Arrays.copyOfRange(byteArray, i, i+ENCRYPTED_SIZE));

                    System.arraycopy(decrypted, 0, decryptedArray, coveredSize, decrypted.length-1);
                    coveredSize = coveredSize + decrypted.length;
                }
                NetworkMessage message = deserialize(decryptedArray);
                return message;
            }
            return null;
        } catch (BadPaddingException e) {
            e.printStackTrace();
            System.out.println("Problems in the encryption");
            return null;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            System.out.println("Problems in the encryption");
            return null;
        }
    }

    //passing the parameters to the methods is kind of an overkill, as they are global variables, but lets work like that
    protected void releaseResources(InputStream input, OutputStream output,
                                    Socket socket) {

        connected = false;

        if (input != null) {
            try {
                input.close();
            } catch (IOException ex) {
                //Logger.getLogger(DoctorClient.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Input already closed by the other end");
            } finally {
                input = null;
            }
        }

        if (output != null) {
            try {
                output.close();
            } catch (IOException ex) {
                //Logger.getLogger(DoctorClient.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Output already closed by the other end");
            } finally {
                output = null;
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println("Socket already closed by the other end");
                //Logger.getLogger(DoctorClient.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                socket = null;
            }
        }

    }

    public boolean isConnected() {
        return connected;
    }


    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

}
