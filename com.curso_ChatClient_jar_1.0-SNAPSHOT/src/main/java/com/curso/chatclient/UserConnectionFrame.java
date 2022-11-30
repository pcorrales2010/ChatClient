package com.curso.chatclient;

import javax.crypto.NoSuchPaddingException;
import javax.swing.*;

import com.curso.exceptions.ClientException;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserConnectionFrame {
    private JFrame frame;
    private Popup pConnect;
    private Popup pError;
    private Client sender = null;
    PopupFactory pf;
    Popup po;

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());

    public UserConnectionFrame(Client client) {

        // Popup panels
        PopupFactory pfConnect = new PopupFactory();
        PopupFactory pfError = new PopupFactory();
        JPanel panelConnect = new JPanel();
        JPanel panelError = new JPanel();
        JLabel labelConnect = new JLabel("Connected");
        JLabel labelError = new JLabel("Error");
        JButton buttonOk1 = new JButton("OK");
        JButton buttonOk2 = new JButton("OK");
        panelConnect.add(labelConnect);
        panelConnect.add(buttonOk1);
        panelConnect.setBackground(Color.green);
        panelError.setBackground(Color.red);
        panelError.add(labelError);
        panelError.add(buttonOk2);
        pConnect = pfConnect.getPopup(frame, panelConnect, 100, 70);
        pError = pfError.getPopup(frame, panelError, 100, 70);

        buttonOk1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pConnect.hide();
                pConnect = pfConnect.getPopup(frame, panelConnect, 100, 70);
            }
        });
        buttonOk2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pError.hide();
                pError = pfError.getPopup(frame, panelError, 100, 70);
            }
        });

        // Client 
        sender = client;

        JLabel label1 = new JLabel("Username:", JLabel.RIGHT);
        label1.setBounds(0, 30, 105, 30);
        label1.setFont(new Font("Verdana", Font.PLAIN, 16));

        JLabel label2 = new JLabel("Password:", JLabel.RIGHT);
        label2.setBounds(0, 70, 105, 30);
        label2.setFont(new Font("Verdana", Font.PLAIN, 16));

        JTextField tf1 = new JTextField();
        tf1.setBounds(110, 30, 80, 30);

        JTextField tf2 = new JTextField();
        tf2.setBounds(110, 70, 80, 30);

        JButton button1 = new JButton("LOGIN");
        button1.setBounds(195, 30, 85, 30);
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (sender.sendCredentials(tf1.getText(), tf2.getText(), "LOGIN")) {
                        pConnect.show();
                        new ChatFrame(sender);
                    }
                    else 
                        pError.show();
                } catch (NoSuchPaddingException | IOException | ClientException | InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        JButton button2 = new JButton("REGISTER");
        button2.setBounds(195, 70, 85, 30);
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (sender.sendCredentials(tf1.getText(), tf2.getText(), "REGISTER")) {
                        pConnect.show();
                        new ChatFrame(sender);
                    } else
                        pError.show();
                } catch (NoSuchPaddingException | IOException | ClientException | InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        JButton button3 = new JButton("BACK");
        button3.setBounds(220, 0, 80, 20);
        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.hide();
            }
        });

        frame = new JFrame("Connection");
        frame.add(tf1);
        frame.add(tf2);
        frame.add(label1);
        frame.add(label2);
        frame.add(button1);
        frame.add(button2);
        frame.add(button3);
        frame.setLayout(null);
        frame.setSize(310, 160);
        frame.setVisible(true);
        frame.setResizable(false);

    }
}
