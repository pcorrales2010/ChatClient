/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.curso.chatclient;

import static org.mockito.ArgumentMatchers.matches;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CheckedInputStream;

import com.curso.exceptions.ClientException;

/**
 * Class that connects a Client to a Server.
 *
 * @author gruital.
 *
 *
 */
public class Connection {

    private String host = "";
    private int port;
    private Socket socket;
    private final static Logger LOGGER = Logger.getLogger(Connection.class.getName());


    /**
     * Constructor to change values of host and port.
     *
     * @param host.
     * @param port.
     */
    public Connection(String host, int port) {
        this.host = host;
        this.port = port;
        LOGGER.setLevel(Level.ALL);
    }

    public Socket stablishConnection() throws ClientException, IOException {

        socket = connect();
        // Check if socket is connected successfully
        if (socket != null) {
            if (socket.isConnected()) {
                return socket;
            } else {
                throw new ClientException("Error: Socket connection could not be stablished.");
            }
        } else {
            throw new ClientException("Error: Server is not running.");

        }
    }

    /**
     * Constructor to creare a new object given a socket .
     *
     * @param newSocket .
     */
    public Connection(Socket newSocket) {
        socket = newSocket;
    }

    /**
     * 
     * @return
     */
    public Socket getMySocket() {
        return socket;
    }

    /**
     * This method returns the values of host in the object.
     *
     * @return host of the object.
     */
    public String getHost() {
        return host;
    }

    /**
     * This method returns the values of port in the object.
     *
     * @return port of the object.
     */
    public int getPort() {
        return port;
    }

    /**
     * 
     * @param mySocket
     */
    public void setMySocket(Socket mySocket) {
        this.socket = mySocket;
    }

    /**
     * this method connects the user with its socket to the server.
     *
     * @return Sockt that client is gonna use for the connection.
     */
    public Socket connect() {
        if (socket == null) {
            try {
                socket = new Socket(getHost(), getPort());

            } catch (SecurityException | IllegalArgumentException | IOException ex) {
                LOGGER.log(Level.SEVERE, ex.toString(), ex);
            }
        }
        return socket;
    }

    /**
     *
     * @return true if the socket was able to be closed.
     */
    public boolean close() {
        if (socket != null) {
            try {
                socket.close();
                return true;
            } catch (SecurityException | IllegalArgumentException | IOException ex) {
                LOGGER.log(Level.SEVERE, ex.toString(), ex);
            }

        }
        return false;
    }

    public static boolean checkIp(String ip) {
        String regex = "((\\d{1,2}|(0|1)\\"
                + "d{2}|2[0-4]\\d|25[0-5])\\.){3}"
                + "(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])";
        return ip.matches(regex);
    }

    public static boolean checkPort(String port) {
        return port.matches("[0-9]+");
    }

}
