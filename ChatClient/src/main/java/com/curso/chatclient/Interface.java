/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.curso.chatclient;

import com.curso.exceptions.ClientException;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;

/**
 * This class works getting data in and out of the clients
 *
 * @author josemrm30
 */
public class Interface {

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

    public Interface() {
        sc = new Scanner(System.in);
        LOGGER.setLevel(Level.ALL);
    }

    /**
     *
     */
    public void entryMessageByUser() throws NoSuchPaddingException {

        while (reading && logged) {

            System.out.print("> ");

            // Check message mode
            try {
                msg = sc.nextLine();
            } catch (NoSuchElementException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }

            // If user writes exit
            if (msg.toLowerCase().equals("exit")) {
                reading = false;

                // Close socket connection
                conct.close();

                // Stop boolean variable and then, interrupt the thread execution
                listener.stopThread();
                listener.interrupt();

            } else {
                //sender.sendMessage(msg);
                sender.sendMessage(msg);
            }

        }
    }

    /**
     *
     * @return @throws ClientException
     */
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

    /**
     * Method used for all inputs and outputs.Defines the .start of this thread
     * The Thread keeps reading the next input that we recive from the server
     * and prints the message that gets
     *
     * To end the loop insert 'exit'
     *
     * @param isClient
     * @throws com.curso.exceptions.ClientException
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     * @throws java.security.NoSuchAlgorithmException
     */
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
        
        if (logged) {
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
    }

    /**
     * Method used for all inputs and outputs.Defines the .start of this thread
     * The Thread keeps reading the next input that we recive from the server
     * and prints the message that gets
     *
     * To end the loop insert 'exit'
     *
     * @param isClient
     * @return
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    public boolean runAuthentication(int isClient) throws InterruptedException, IOException, NoSuchPaddingException {
        String serverAnswer = "";
        String selectedOption = "";

        try {

            while (!serverAnswer.equals("true")) {

                // Check if it is a client
                if (isClient == 1) {
                    System.out.println("Choose an option.\n'exit' for end the application.");
                    System.out.println("1. Sign up");
                    System.out.println("2. Log in");

                    try {
                        selectedOption = sc.nextLine();
                    } catch (NoSuchElementException e) {
                        LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                } else {
                    // 2 option if it is a bot
                    selectedOption = "2";
                }

                // Checking selectedOption value
                switch (selectedOption.toLowerCase()) {
                    case "1" -> {
                        if (inputUsernamePassword("REGISTER", isClient)) {
                            serverAnswer = "true";
                            logged = true;
                        }
                    }
                    case "2" -> {
                        if (inputUsernamePassword("LOGIN", isClient)) {
                            serverAnswer = "true";
                            logged = true;
                        }
                    }
                    case "exit" -> {
                        serverAnswer = "true";
                        logged = false;
                    }
                    default -> {
                        System.out.println("Incorrect option");
                        logged = false;
                    }
                }
            }
        } catch (ClientException CliExp) {
            System.out.println(CliExp.getMessage());
        }
        return logged;
    }

    /**
     *
     *
     * @param mode
     * @param isClient
     * @return
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     * @throws com.curso.exceptions.ClientException
     */
    public boolean inputUsernamePassword(String mode, int isClient) throws InterruptedException, IOException, ClientException, NoSuchPaddingException {
        String username;
        String password;
        boolean connect = false;

        while (!connect) {
            if (isClient == 1) {
                System.out.print("Username: ");
                username = sc.nextLine();
                if (username.equals("exit")) {
                    return false;
                }
                System.out.print("Password: ");
                password = sc.nextLine();
                if (password.equals("exit")) {
                    return false;
                }
            } else {
                Thread.sleep(500);
                username = "bot";
                Thread.sleep(500);
                password = "bot";
            }
            connect = sendCredentials(username, password, mode);
            if (!connect) {
                System.out.println("The username or password are incorrect, please try again or 'exit' to return back");
            }
        }
        return connect;
    }

    public boolean sendCredentials(String username, String password, String mode) throws IOException, ClientException, InterruptedException, NoSuchPaddingException {
        // Server asks for username
        sender.getMessage();
        sender.sendMessage(mode);
        String server_message = sender.getMessage();
        if (server_message.toUpperCase().trim().equals("USER:")) {
            sender.sendMessage(username);
        }

        // Server asks for password
        server_message = sender.getMessage();
        if (server_message.toUpperCase().trim().equals("PASSWORD:")) {
            sender.sendMessage(password);
        }

        // Server answers 'successful' or 'Error'
        server_message = sender.getMessage();
        return server_message.trim().toUpperCase().equals("SUCCESSFUL");
    }
}
