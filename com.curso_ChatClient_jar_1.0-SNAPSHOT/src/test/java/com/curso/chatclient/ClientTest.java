/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.curso.chatclient;

import com.curso.exceptions.ClientException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.*;

import static org.mockito.Mockito.when;

/**
 *
 * @author juacuadr
 */
public class ClientTest {


    // CREATE LOGGER
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    
    public ClientTest() {
    }

    /**
     * Test of Send Message method by using Mockito.
     *
     * @throws IOException when an I/O error occurs.
     */
    @Test
    public void testSendMessage() throws IOException {
        // GIVEN
        Socket testSocket = Mockito.mock(Socket.class);
        BufferedReader testReader = Mockito.mock(BufferedReader.class);
        PrintWriter testWriter = Mockito.mock(PrintWriter.class);

        Client client = new Client(testSocket, testWriter, testReader);

        // THEN
        client.sendMessage("This is my message");

        // EXPECT
        verify(testWriter, times(1)).println("This is my message");

        try {
            verify(testReader, times(0)).readLine();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
            throw ex;
        }
    }

    /**
     * Test of Get Message method by using Mockito.
     * 
     * @throws IOException 
     */
    @Test
    public void testGetMessage() throws IOException, ClientException {
        // GIVEN
        Socket testSocket = Mockito.mock(Socket.class);
        BufferedReader testReader = Mockito.mock(BufferedReader.class);
        PrintWriter testWriter = Mockito.mock(PrintWriter.class);

        try {
            // Mock behaviour
            when(testReader.readLine()).thenReturn("cosica");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
            throw ex;
        }

        Client client = new Client(testSocket, testWriter, testReader);

        // THEN
        String msg;
        msg = client.getMessage();

        // EXPECT
        assertEquals(msg, "cosica");
    }

}
