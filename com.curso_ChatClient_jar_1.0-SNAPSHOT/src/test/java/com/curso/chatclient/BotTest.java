/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
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
public class BotTest {
    
    public BotTest() {
    }

    /**
     * Test of decoding messages. User sends a message like <username>: <message> and
     * the method decodes the message and 
     */
    @Test
    public void testDecodingMessage() {
        // GIVEN
        System.out.println("Test Decoding Message");
        Socket testSocket = Mockito.mock(Socket.class);
        BufferedReader testReader = Mockito.mock(BufferedReader.class);
        PrintWriter testWriter = Mockito.mock(PrintWriter.class);

        Client botClient = new Client(testSocket, testWriter, testReader);
        Bot mockedBotInstance = new Bot(botClient);
        
        // EXPECT
        assertEquals(null, mockedBotInstance.decodingMessage("juanca: hola que tal"));
        assertEquals("/menu", mockedBotInstance.decodingMessage("juanca: /menu"));
    }
}
