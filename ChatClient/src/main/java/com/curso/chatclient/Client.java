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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;

/**
 * Class Client with the methods: sendMessage and getMessage
 *
 * @author pcorrales2010
 */
public class Client implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(Client.class.getName());
    PrintWriter writer;
    BufferedReader reader;

    Encrypt encryption = new Encrypt();
    boolean cipherMessage = false;
    protected boolean logged = false;
    protected ArrayDeque<String> messages = new ArrayDeque<String>();
    private Socket socket;

    Runnable listening = new Runnable() {

        @Override
        public void run() {
            while (true) {
                try {
                    messages.add(getMessage());
                } catch (ClientException ex) {
                    LOGGER.log(Level.SEVERE, ex.toString(), ex);
                }
            }
        }
    };

    /**
     * Constructor that receive a Socket and fill writer and reader private
     * variables.
     *
     * @param newSocket
     * @throws ClientException                        when an I/O error occurs while
     *                                                creating the
     *                                                output/input stream.
     * @throws java.security.NoSuchAlgorithmException
     */
    public Client(Socket newSocket) throws ClientException, NoSuchAlgorithmException {
        if (newSocket != null) {
            socket = newSocket;
            InputStream input;
            OutputStream output;
            cipherMessage = false;

            try {
                output = socket.getOutputStream();
            } catch (IOException ex) {
                throw new ClientException("Error creating the output stream: the socket could not be connected");
            }

            try {
                writer = new PrintWriter(output, true);
                input = socket.getInputStream();
            } catch (SecurityException | IllegalArgumentException | IOException ex) {
                throw new ClientException(
                        "Error creating the input stream: The socket is closed, not connected or the input has been shutdown");
            }
            reader = new BufferedReader(new InputStreamReader(input));
        }
    }

    public void setSecret(String message) {
        String messageSplitted[] = message.split("/secret");
        if (messageSplitted.length != 1) {
            if (messageSplitted[1].toUpperCase().contains("ON")) {
                this.cipherMessage = true;
            } else if (messageSplitted[1].toUpperCase().contains("OFF")) {
                this.cipherMessage = false;
            } else {
                System.out.println("The command is incorrect");
            }
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
            throw new ClientException("Error reading line.");
        }

        if (line.contains("'secret' ")) {
            String[] arrayString = line.split("\'secret\' ");
            line = arrayString[0] + Encrypt.decrypt(arrayString[1]);
        }
        return line;
    }

    public void run() {
    }

    public void readingInput(String message) {
        boolean commands = false;

        var command = Command.parseCommand(message);
        switch (command) {
            case SECRET:
                setSecret(message);
                commands = true;
                break;
            case JOIN:
                sendMessage(message);
                commands = true;
                break;
            case NOOP:
                break;
        }
        if (!commands) {
            try {
                sendMessageSecret(message);
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Send the message and current date from client to server
     *
     * @param message The message to send to server
     * @param encrypt
     * @throws javax.crypto.NoSuchPaddingException
     */
    public void sendMessage(String message) {
        writer.println(message);
    }

    public void sendMessageSecret(String message) throws NoSuchPaddingException {
        if (cipherMessage) {
            message = "'secret' " + Encrypt.encrypt(message);
        }
        sendMessage(message);
    }

    public boolean sendCredentials(String username, String password, String mode) {
        // Server asks for username
        try {
            sendMessage(mode);

            String server_message = getMessage();
            if (server_message.toUpperCase().trim().equals("USER:")) {
                sendMessage(username);
            }

            // Server asks for password
            server_message = getMessage();
            if (server_message.toUpperCase().trim().equals("PASSWORD:")) {
                sendMessage(password);
            }

            // Server answers 'successful' or 'Error'
            server_message = getMessage();
            return server_message.trim().toUpperCase().equals("SUCCESSFUL");
            
        } catch ( ClientException ex) {           
            LOGGER.log(Level.SEVERE, ex.toString(), ex);;
        }
        return false;
    }

}
