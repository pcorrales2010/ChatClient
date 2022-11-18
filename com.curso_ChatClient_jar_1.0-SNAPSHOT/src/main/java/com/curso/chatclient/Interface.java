/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.curso.chatclient;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class works getting data in and out of the clients
 *
 * @author josemrm30
 */
public class Interface {

    private final static Logger LOGGER = Logger.getLogger(Interface.class.getName());
    private Scanner sc;
    private Logger log;

    public Interface() {
        sc = new Scanner(System.in);
        LOGGER.setLevel(Level.ALL);
    }

    public String input() {
        return sc.nextLine();
    }

    public String username() {
        output("Username: ");
        return input();
    }

    public String password() {
        output("Password :");
        return input();
    }

    public void output(String message) {
        System.out.println(message);
    }

    public void outputLine(String message) {
        System.out.print(message);
    }

    public void closeScanner() {
        sc.close();
    }

}
