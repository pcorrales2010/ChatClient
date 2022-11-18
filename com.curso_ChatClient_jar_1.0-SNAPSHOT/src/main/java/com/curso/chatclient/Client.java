/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.curso.chatclient;

import com.curso.exceptions.ClientException;

import static org.mockito.ArgumentMatchers.booleanThat;

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
import java.util.Date;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.NoSuchElementException;
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
public class Client implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(Client.class.getName());
    PrintWriter writer;
    BufferedReader reader;

    Encrypt encryption = new Encrypt();
    boolean cipherMessage;

    private boolean logged = false;
    private ArrayDeque<String> messages = new ArrayDeque<String>();
    private String msg = null;
    private Socket socket;
    private ListenThread listener;
    Connection conct;
    private Interface terminal;

    Runnable listening = new Runnable() {

        @Override
        public void run() {
            messages.add("while");
            while (true) {
                try {
                    messages.add(getMessage());
                } catch (ClientException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Runnable Input = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    readingInput();
                } catch (NoSuchPaddingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
            terminal = new Interface();

            try {
                output = newSocket.getOutputStream();
            } catch (IOException ex) {
                throw new ClientException("Error creating the output stream: the socket could not be connected");
            }

            try {
                writer = new PrintWriter(output, true);
                input = socket.getInputStream();
            } catch (SecurityException | IllegalArgumentException | IOException ex) {
                LOGGER.log(Level.FINE, ex.toString(), ex);
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
            LOGGER.log(Level.FINE, ex.toString(), ex);
            throw new ClientException("Error reading line.");
        }
        // two bars to indicate the characters are literal

        if (line.contains("*secret* ")) {
            line = line.split("//*secret//* ")[1];
            line = Encrypt.decrypt(line);
        }
        return line;
    }

    public void run() {

        // Client authentication
        try {
            logged = runAuthentication();
        } catch (NoSuchPaddingException | InterruptedException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (logged) {
            // Client run
            messages.add("hola");
            // Initialize new instance of ListenThred name listener
            Thread listener = new Thread(listening);
            listener.start();

            // Initialize a subroutine for sending messages
            Thread readInput = new Thread(Input);
            readInput.start();

            while (logged) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ;

                terminal.output(Integer.toString(messages.size()));

                // while cola_de_mensajes no está vacía
                while (messages.size() != 0) {
                    terminal.output("[" + new Date() + "]: " + messages.poll());
                    terminal.output("> ");
                }

            }
            terminal.closeScanner();
        }
    }

    public void readingInput() throws NoSuchPaddingException {

        System.out.print("> ");

        // Check message mode
        try {
            msg = terminal.input();
        } catch (NoSuchElementException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        /*
         * var command = Command.parseCommand(msg);
         * switch (command) {
         * case EXIT:
         * reading = false;
         * exit();
         * break;
         * }
         */
        sendMessage(msg);

    }

    /**
     * Send the message and current date from client to server
     *
     * @param message The message to send to server
     * @param encrypt
     * @throws javax.crypto.NoSuchPaddingException
     */
    public void sendMessage(String message) throws NoSuchPaddingException {
        writer.println(message);
    }

    public void sendMessageSecret(String message, boolean cipherMessage) throws NoSuchPaddingException {
        if (cipherMessage) {
            message = "*secret* " + Encrypt.encrypt(message);
        }
        sendMessage(message);
    }

    public void exit() {
        // Close socket connection
        logged = false;
        conct.close();
        // Stop boolean variable and then, interrupt the thread execution
        listener.stopThread();
        listener.interrupt();
    }

    public boolean runAuthentication() throws InterruptedException, IOException, NoSuchPaddingException {
        boolean serverAnswer = false;
        String selectedOption = "";

        try {
            while (!serverAnswer) {
                // Check if it is a client
                optionLoginRegister();

                try {
                    selectedOption = terminal.input();
                } catch (NoSuchElementException e) {
                    LOGGER.log(Level.SEVERE, e.toString(), e);
                }
                // Checking selectedOption value
                serverAnswer = registerLogin(selectedOption);
            }
        } catch (

        ClientException CliExp) {
            System.out.println(CliExp.getMessage());
        }
        return logged;
    }

    public boolean registerLogin(String selectedOption)
            throws NoSuchPaddingException, InterruptedException, IOException, ClientException {
        boolean serverAnswer = false;
        switch (selectedOption.toLowerCase()) {
            case "1" -> {
                if (inputUsernamePassword("REGISTER")) {
                    serverAnswer = true;
                    logged = true;
                }
            }
            case "2" -> {
                if (inputUsernamePassword("LOGIN")) {
                    serverAnswer = true;
                    logged = true;
                }
            }
            case "exit" -> {
                serverAnswer = false;
                logged = false;
            }
            default -> {
                System.out.println("Incorrect option");
                logged = false;
            }
        }
        return serverAnswer;
    }

    public void optionLoginRegister() {
        terminal.output("Choose an option.\n'exit' for end the application.");
        terminal.output("1. Sign up");
        terminal.output("2. Log in");
    }

    public boolean inputUsernamePassword(String mode)
            throws InterruptedException, IOException, ClientException, NoSuchPaddingException {
        String username;
        String password;
        boolean connect = false;

        while (!connect) {
            username = username();
            if (username.equals("back")) {
                return false;
            }
            password = password();
            if (password.equals("back")) {
                return false;
            }
            connect = sendCredentials(username, password, mode);
            if (!connect) {
                terminal.output("The username or password are incorrect, please try again or 'back' to return back");
            }
        }
        return connect;
    }

    public String username() {
        terminal.output("Username: ");
        return terminal.input();
    }

    public String password() {
        terminal.output("Password :");
        return terminal.input();
    }

    public boolean sendCredentials(String username, String password, String mode)
            throws IOException, ClientException, InterruptedException, NoSuchPaddingException {
        // Server asks for username
        getMessage();
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
    }

}
