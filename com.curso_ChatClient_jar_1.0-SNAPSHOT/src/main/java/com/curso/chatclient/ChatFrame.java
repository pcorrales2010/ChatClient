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
            String messageOutput;
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                while (!sender.messages.isEmpty()) {
                    messageOutput = "< " + "[" + new Date() + "]: " + sender.messages.poll().trim() + "\n";
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

        JScrollPane scrollableTextArea1 = new JScrollPane(textAreaOutput);
        scrollableTextArea1.setBounds(10, 10, 600, 450);

        JTextField textAreaInput = new JTextField();
        textAreaInput.setBounds(10, 475, 600, 50);
        textAreaInput.setFont(new Font("Verdana", Font.PLAIN, 12));

        JTextArea users = new JTextArea();
        users.setFont(new Font("Verdana", Font.PLAIN, 12));

        JScrollPane scrollableTextArea2 = new JScrollPane(users);
        scrollableTextArea2.setBounds(620, 10, 290, 450);

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
        frame.setSize(940, 575);
        frame.getContentPane().add(scrollableTextArea1);
        frame.add(textAreaInput);
        frame.add(scrollableTextArea2);
        frame.add(button);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Client");
        frame.setLayout(null);
        frame.setVisible(true);
    }
}
