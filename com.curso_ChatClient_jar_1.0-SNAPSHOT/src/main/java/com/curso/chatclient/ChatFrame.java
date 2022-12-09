package com.curso.chatclient;

import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.util.Date;

public class ChatFrame {

    Client sender = null;
    Thread listener = null;
    Thread printer = null;
    JTextArea textAreaOutput = null;

    Runnable printMessage = new Runnable() {

        @Override
        public void run() {
            // while (logged) {
            String messageOutput = null;
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                while (!sender.messages.isEmpty()) {
                    String message = sender.messages.poll().trim();                   
                    messageOutput = "< " + "[" + new Date() + "]: " + message + "\n";
                    textAreaOutput.append(messageOutput);
                }
            }
        }
    };

    public ChatFrame(Client client) {

        sender = client;

        listener = new Thread(sender.listening);
        listener.start();

        printer = new Thread(printMessage);
        printer.start();

        textAreaOutput = new JTextArea();
        textAreaOutput.setFont(new Font("Verdana", Font.PLAIN, 12));
        textAreaOutput.setEditable(false);
        textAreaOutput.append("Welcome to the chat. \n");

        JScrollPane scrollableTextArea1 = new JScrollPane(textAreaOutput);
        scrollableTextArea1.setBounds(10, 15, 900, 445);

        JTextField textAreaInput = new JTextField();
        textAreaInput.setBounds(10, 475, 600, 50);
        textAreaInput.setFont(new Font("Verdana", Font.PLAIN, 12));
        textAreaInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String messageInput = textAreaInput.getText();
                    sender.readingInput(messageInput);
                    textAreaOutput.append(">" + messageInput + "\n");
                    textAreaInput.setText("");
                } catch (NoSuchPaddingException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        JButton button = new JButton("SEND");
        button.setBounds(620, 475, 290, 50);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String messageInput = textAreaInput.getText();
                    sender.readingInput(messageInput);
                    textAreaOutput.append(">" + messageInput + "\n");
                    textAreaInput.setText("");
                } catch (NoSuchPaddingException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        JFrame frame = new JFrame();
        frame.pack();
        frame.setSize(940, 575);
        frame.getContentPane().add(scrollableTextArea1);
        frame.add(textAreaInput);
        frame.add(button);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Client");
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
