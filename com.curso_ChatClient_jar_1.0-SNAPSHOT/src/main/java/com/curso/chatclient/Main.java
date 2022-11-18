/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.curso.chatclient;

import com.curso.exceptions.ClientException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author jramir14
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());

    public static void main(String[] args) throws IOException, ClientException, InterruptedException,
            NoSuchAlgorithmException, ClassNotFoundException, NoSuchPaddingException {
        Interface terminal = new Interface();
        boolean running = true;
        Connection conct = null;
        Socket socket = null;
        Client sender = null;
        String port;
        String ip;

        // Stablish socket connection
        while (running) {
            try {
                terminal.output("Introduce hostname:");
                ip = terminal.input();
                terminal.output("Introduce port:");
                port = terminal.input();

                if (port.matches("[0-9]+")) {
                    conct = new Connection(ip, Integer.parseInt(port));
                } else {
                    throw new ClientException("Error: Incorrect port format.");
                }
                socket = conct.stablishConnection(ip, port);
                running = false;
            } catch (ClientException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }
        }
        // Initialize new instance of Client named sender
        try {
            sender = new Client(socket);
        } catch (ClientException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        // Run for a client
        sender.run();
        conct.close();
    }
}
