/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.curso.chatclient;

import com.curso.exceptions.ClientException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javax.crypto.NoSuchPaddingException;
import net.bytebuddy.asm.Advice;

/**
 *
 * @author jramir14
 */
public class Main implements Serializable {

    public static void main(String[] args) throws IOException, ClientException, InterruptedException, NoSuchAlgorithmException, ClassNotFoundException, NoSuchPaddingException {
        Interface menu = new Interface();
        // Run for a client
        menu.run(1);      
    }
}
