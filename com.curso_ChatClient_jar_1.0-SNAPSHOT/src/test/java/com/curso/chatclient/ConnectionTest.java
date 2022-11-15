/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.curso.chatclient;

import java.io.IOException;
import java.net.Socket;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author juacuadr
 */
public class ConnectionTest {
    
    public ConnectionTest() {
    }
    
    /**
    * Test of client socket connection with server host and server port.
    * 
    * @throws IOException when 
    */
    @Test
    public void testStartConnection() {
        // GIVEN
        System.out.println("Get Connection Test");
        Connection newConnection = new Connection();
        
        // THEN
        Socket clientSocket = newConnection.connect();
        
        // EXPECT
        assertNotNull(clientSocket);
    }
    
    /**
    * Test of close client socket with server host and server port.
    */
    @Test
    public void testCloseConnection() {
        // GIVEN
        System.out.println("Close Connection Test");
        Connection newConnection = new Connection();
        Socket clientSocket = newConnection.connect();
        
        // THEN
        boolean res = newConnection.close();
        
        // EXPECT
        assertEquals(true, res);
    }
}
