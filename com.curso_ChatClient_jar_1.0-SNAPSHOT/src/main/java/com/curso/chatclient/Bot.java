/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.curso.chatclient;

import com.curso.exceptions.ClientException;
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

    private boolean runningBot;

    public Bot(Socket newSocket) throws ClientException, NoSuchAlgorithmException {
        super(newSocket);
    }

    public void listeningMessages() throws ClientException, NoSuchPaddingException {

        while (runningBot) {

            String msgReaded = getMessage();
            System.out.println(msgReaded);
            String code = decodingMessage(msgReaded);
            System.out.println(code);
            String res = "";

            if (code != null) {

                switch (code) {
                    // Showing menu
                    case "/MENU":
                        res = botMenu();
                        break;

                    // Dumb option
                    case "/DUMB":
                        res = dumb();
                        break;

                    // Compatibility option
                    case "/COMPATIBILITY":
                        res = compatibility();
                        break;

                    // Death date
                    case "/DEATHDATE":
                        res = deathDate();
                        break;

                    // Head or tail
                    case "/HEADSORTAILS":
                        res = headsOrTails();
                        break;
                }
                // Sending message response
                if (!"".equals(res)) {
                    sendMessage(res);
                }
            }
        }
    }

    public String decodingMessage(String msg) {
        String[] splitted = msg.split("] ");
        return splitted[1];
    }

    public String botMenu() {
        return "ChatBot menu: /DUMB, /COMPATIBILITY, /DEATHDATE or /HEADSORTAILS";
    }

    public String dumb() {
        return "You are " + new Random().nextInt(11) + " dumb on the international dumb scale. Congratulations";
    }

    public String compatibility() {
        return "You are " + new Random().nextInt(101) + "% compatible with java";
    }

    public String deathDate() {
        Random random = new Random();
        LocalDate date = LocalDate.now();
        LocalDate deathDate;
        deathDate = LocalDate.of(random.nextInt(date.getYear() + 1, 2100), random.nextInt(1, 13), random.nextInt(1, 32));

        Duration diff = Duration.between(date.atStartOfDay(), deathDate.atStartOfDay());
        long diffDays = diff.toDays();

        return "You have " + diffDays / 360 + " years to live";

    }

    public String headsOrTails() {
        Random random = new Random();

        if (random.nextInt(2) == 0) {
            return "head";
        }

        return "tail";
    }
}
