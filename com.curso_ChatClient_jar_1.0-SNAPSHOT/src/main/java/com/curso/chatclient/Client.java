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
import java.net.http.WebSocket.Listener;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;
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

    protected String message = null;
    protected boolean logged = false;
    protected ArrayDeque<String> messages = new ArrayDeque<String>();
    private Socket socket;
    Connection conct;
    protected Interface terminal;

    Runnable listening = new Runnable() {

        @Override
        public void run() {
            //while (logged) {
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
            while (logged) {
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
                output = socket.getOutputStream();
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

        if (line.contains("'secret' ")) {
            String[] arrayString = line.split("\'secret\' ");
            line = arrayString[0] + Encrypt.decrypt(arrayString[1]);
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
            terminal.outputLine("> ");

            // Initialize a subroutine for receiving messages
            Thread listener = new Thread(listening);
            listener.start();

            // Initialize a subroutine for sending messages
            Thread readInput = new Thread(Input);
            readInput.start();

            while (logged) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                // while cola_de_mensajes no está vacía
                while (!messages.isEmpty()) {
                    message = messages.poll();
                    terminal.output("[" + new Date() + "]: " + message);
                    terminal.outputLine("> ");
                }

            }
            readInput.interrupt();
            listener.interrupt();

            terminal.closeScanner();
        }
    }

    public void readingInput(String message) throws NoSuchPaddingException {
        boolean commands = false;
        //message = terminal.input();

        var command = Command.parseCommand(message);
        switch (command) {
            case EXIT:
                try {
                    exit();
                    commands = true;
                } catch (IOException ex) {
                    LOGGER.log(Level.FINE, ex.toString(), ex);
                }
                break;
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
            sendMessageSecret(message);
        }

    }

    public void readingInput() throws NoSuchPaddingException {
        boolean commands = false;
        // Check message mode

        message = terminal.input();

        var command = Command.parseCommand(message);
        switch (command) {
            case EXIT:
                try {
                    exit();
                    commands = true;
                } catch (IOException ex) {
                    LOGGER.log(Level.FINE, ex.toString(), ex);
                }
                break;
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
            sendMessageSecret(message);
        }

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

    public void sendMessageSecret(String message) throws NoSuchPaddingException {
        if (cipherMessage) {
            message = "'secret' " + Encrypt.encrypt(message);
        }
        sendMessage(message);
    }

    public void exit() throws IOException {
        logged = false;
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
        } catch (ClientException CliExp) {
            System.out.println(CliExp.getMessage());
        }
        return serverAnswer;
    }

    public boolean registerLogin(String selectedOption)
            throws NoSuchPaddingException, InterruptedException, IOException, ClientException {
        boolean serverAnswer = false;
        switch (selectedOption.toLowerCase()) {
            case "1" -> {
                if (inputUsernamePassword("REGISTER")) {
                    serverAnswer = true;
                }
            }
            case "2" -> {
                if (inputUsernamePassword("LOGIN")) {
                    serverAnswer = true;
                }
            }
            case "exit" -> {

            }
            default -> {
                System.out.println("Incorrect option");
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
            username = terminal.username();
            if (username.equals("back")) {
                return false;
            }
            password = terminal.password();
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
