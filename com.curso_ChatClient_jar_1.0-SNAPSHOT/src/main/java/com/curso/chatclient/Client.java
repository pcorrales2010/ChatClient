/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.curso.chatclient;

import com.curso.exceptions.ClientException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Class Client with the methods: sendMessage and getMessage
 *
 * @author pcorrales2010
 */
public class Client {

    Socket socket;
    PrintWriter writer;
    BufferedReader reader;
    private final static Logger LOGGERCLIENT = Logger.getLogger(Client.class.getName());

    Encrypt encryption = new Encrypt();
    boolean cipherMessage;

    private final static Logger LOGGER = Logger.getLogger(Interface.class.getName());
    private boolean reading = true;
    private boolean logged = false;
    private String msg = null;
    private Socket mySocket;
    private Client sender;
    private ListenThread listener;
    Connection conct;
    private Scanner sc;
    private Logger log;
    /**
     * Constructor that receive a Socket and fill writer and reader private
 variables.
     *
     * @param newSocket
     * @throws ClientException when an I/O error occurs while creating the
     * output/input stream.
     * @throws java.security.NoSuchAlgorithmException
     */
    public Client(Socket newSocket) throws ClientException, NoSuchAlgorithmException {
        if (newSocket != null) {
            socket = newSocket;
            InputStream input;
            OutputStream output;

            try {
                output = newSocket.getOutputStream();
            } catch (IOException ex) {
                throw new ClientException("Error creating the output stream: the socket could not be connected");
            }

            try {
                writer = new PrintWriter(output, true);
                input = socket.getInputStream();
            } catch (SecurityException | IllegalArgumentException | IOException ex) {
                LOGGERCLIENT.log(Level.FINE, ex.toString(), ex);
                throw new ClientException("Error creating the input stream: The socket is closed, not connected or the input has been shutdown");

            }

            reader = new BufferedReader(new InputStreamReader(input));
        }
    }

    /**
     * Parametrized constructor to inject variables. This constructor is being
     * used into Test files.
     *
     * @param newSocket
     * @param newWriter
     * @param newReader
     */
    public Client(Socket newSocket, PrintWriter newWriter, BufferedReader newReader) {
        socket = newSocket;
        writer = newWriter;
        reader = newReader;
        cipherMessage = false;
    }

    /**
     * Send the message and current date from client to server
     *
     * @param message The message to send to server
     * @param encrypt
     * @throws javax.crypto.NoSuchPaddingException
     */
    public void sendMessage(String message) throws NoSuchPaddingException {
        if (message.toUpperCase().contains("/SECRET")) {
            String messageSplit = message.substring(message.toUpperCase().indexOf("/SECRET") + 7);
            String messageSplitted[] = messageSplit.split(" ");
            if (messageSplitted.length != 1) {
                if (messageSplitted[1].toUpperCase().contains("ON")) {
                    setSecret(true);
                } else if (messageSplitted[1].toUpperCase().contains("OFF")) {
                    setSecret(false);
                } else {
                    System.out.println("The command is incorrect");
                }
            }
        }
        if (cipherMessage) {
            writer.println(encryption.encrypt(message) + "*secret*");
        } else {
            writer.println(message);
        }
    }

    /**
     * Get the message from server
     *
     * @return Message sent by server
     * @throws ClientException if an I/O error occurs when reading a line.
     */
    public String getMessage() throws ClientException {
        String line;

        try {
            line = reader.readLine();
        } catch (IOException ex) {
            LOGGERCLIENT.log(Level.FINE, ex.toString(), ex);
            throw new ClientException("Error reading line.");
        }
        if (line.contains("*secret*")) {
            line = line.split("] ")[1];
            line = line.substring(0, line.length() - 8);
            line = encryption.decrypt(line);
        }
        return line;
    }

    public void setSecret(boolean secret) {
        this.cipherMessage = secret;
    }

    public void run(int isClient) throws ClientException, InterruptedException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {
        boolean running = true;

        // Stablish socket connection
        while (running) {
            try {
                mySocket = stablishConnection();
                running = false;
            } catch (ClientException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }
        }

        // Initialize new instance of Client named sender
        try {
            sender = new Client(mySocket);
        } catch (ClientException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }

        // Client authentication
        logged = runAuthentication(isClient);

        // Client run
        if (isClient == 1) {
            // Initialize new instance of ListenThred name listener
            try {
                listener = new ListenThread(mySocket);
            } catch (ClientException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }

            listener.start();

            // Initialize a subroutine for sending messages
            entryMessageByUser();

            // Bot run
        } else {
            Bot myBot = new Bot(sender);
            myBot.listeningMessages();
        }

        // Close scanner
        sc.close();
    }
    
    public Socket stablishConnection() throws ClientException, IOException {

        String ip;
        String port;

        System.out.println("Introduce hostname:");
        ip = "127.0.0.1";
        //ip = sc.nextLine();
        System.out.println("Introduce port:");
        port = "49080";
        //port = sc.nextLine();

        if (port.matches("[0-9]+")) {
            conct = new Connection(ip, Integer.parseInt(port));
            mySocket = conct.connect();

            // Check if socket is connected successfully
            if (mySocket != null) {
                if (mySocket.isConnected()) {
                    return mySocket;
                } else {
                    throw new ClientException("Error: Socket connection could not be stablished.");
                }
            } else {
                throw new ClientException("Error: Server is not running.");

            }
        } else {
            throw new ClientException("Error: Incorrect port format.");
        }
    }
    
}
