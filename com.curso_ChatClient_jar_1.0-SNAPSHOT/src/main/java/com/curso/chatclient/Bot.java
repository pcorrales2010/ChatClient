/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.curso.chatclient;

import com.curso.exceptions.ClientException;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Random;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author juacuadr
 */
public class Bot extends Client {

    private final static Logger LOGGER = Logger.getLogger(Interface.class.getName());

    public Bot(Socket newSocket) throws ClientException, NoSuchAlgorithmException {
        super(newSocket);
    }

    @Override
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

    @Override
    public void run() {

        // Client authentication
        while (!logged) {
            try {
                logged = sendCredentials("bot", "bot", "LOGIN");

            } catch (ClientException | NoSuchPaddingException | IOException | InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (!logged) {
                try {
                    logged = sendCredentials("bot", "bot", "REGISTER");
                } catch (NoSuchPaddingException | IOException | ClientException | InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // Initialize a subroutine for receiving messages
        Thread listener = new Thread(listening);
        listener.start();

        while (logged) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // while cola_de_mensajes no está vacía
            while (!messages.isEmpty()) {
                String message = messages.poll();
                if (message != null)
                    message = message.split("] ")[1];
                var command = Command.parseCommand(message);
                switch (command) {
                    case MENU:
                        botMenu();
                        break;
                    case DUMB:
                        dumb();
                        break;
                    case COMPATIBILITY:
                        compatibility();
                        break;
                    case HEADORTAILS:
                        headsOrTails();
                        break;
                    case DEATHDATE:
                        deathDate();
                        break;
                    case NOOP:
                        break;
                }
                terminal.output(message);
            }
            listener.interrupt();

            terminal.closeScanner();
        }
    }

    public String decodingMessage(String message) {
        String[] splitted = message.split("] ");
        return splitted[1];
    }

    public void botMenu() {
        try {
            sendMessage("ChatBot menu: /DUMB, /COMPATIBILITY, /DEATHDATE or /HEADSORTAILS");
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void dumb() {
        try {
            sendMessage(
                    "You are " + new Random().nextInt(11) + " dumb on the international dumb scale. Congratulations");
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void compatibility() {
        try {
            sendMessage("You are " + new Random().nextInt(101) + "% compatible with java");
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deathDate() {
        Random random = new Random();
        LocalDate date = LocalDate.now();
        LocalDate deathDate;
        deathDate = LocalDate.of(random.nextInt(date.getYear() + 1, 2100), random.nextInt(1, 13),
                random.nextInt(1, 32));

        Duration diff = Duration.between(date.atStartOfDay(), deathDate.atStartOfDay());
        long diffDays = diff.toDays();

        try {
            sendMessage("You have " + diffDays / 360 + " years to live");
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void headsOrTails() {
        Random random = new Random();
        String headortails;

        if (random.nextInt(2) == 0) {
            headortails = "head";
        } else
            headortails = "tail";
        try {
            sendMessage(headortails);
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //documentacion de clasese
}
