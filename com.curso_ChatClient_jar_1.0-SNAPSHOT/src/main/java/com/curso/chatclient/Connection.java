/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.curso.chatclient;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private Socket mySocket;
    private final static Logger LOGGER = Logger.getLogger(Connection.class.getName());

    /**
     * Default Constructor it assign the default host and port.
     */
    public Connection() {
        host = "192.168.3.102";
        port = 2525;
        LOGGER.setLevel(Level.ALL);
    }

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

    /**
     * Constructor to creare a new object given a socket .
     *
     * @param newSocket .
     */
    public Connection(Socket newSocket) {
        mySocket = newSocket;
    }
    
    /**
     * 
     * @return 
     */
    public Socket getMySocket() {
        return mySocket;
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
        this.mySocket = mySocket;
    }

    /**
     * this method connects the user with its socket to the server.
     *
     * @return Sockt that client is gonna use for the connection.
     */
    public Socket connect() {
        if (mySocket == null) {
            try {
                mySocket = new Socket(getHost(), getPort());

            } catch (SecurityException | IllegalArgumentException | IOException ex) {
                LOGGER.log(Level.SEVERE, ex.toString(), ex);
            }

        }
        return mySocket;
    }

    /**
     *
     * @return true if the socket was able to be closed.
     */
    public boolean close() {
        if (mySocket != null) {
            try {
                mySocket.close();
                return true;
            } catch (SecurityException | IllegalArgumentException | IOException ex) {
                LOGGER.log(Level.SEVERE, ex.toString(), ex);
            }

        }
        return false;
    }
}
