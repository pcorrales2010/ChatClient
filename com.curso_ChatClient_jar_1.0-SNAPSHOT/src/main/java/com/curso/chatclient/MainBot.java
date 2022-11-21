/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.curso.chatclient;

import com.curso.exceptions.ClientException;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author juacuadr
 */
public class MainBot {
    private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());

    public static void main(String[] args) throws ClientException, InterruptedException, NoSuchAlgorithmException,
            IOException, NoSuchPaddingException {
        Interface terminal = new Interface();
        boolean running = true;
        Connection conct = null;
        Socket socket = null;
        Client bot = null;
        String port;
        String ip;

        // Stablish socket connection
        while (running) {
            try {
                ip = "127.0.0.1";
                port = "49080";
              
                conct = new Connection(ip, Integer.parseInt(port));
                socket = conct.stablishConnection(ip, port);
                running = false;
            } catch (ClientException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }
        }
        // Initialize new instance of Client named sender
        try {
            bot = new Bot(socket);
        } catch (ClientException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        // Run for a client
        bot.run();
        conct.close();

    }
}
