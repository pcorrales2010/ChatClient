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
    private Client sender;
    Toolkit t = Toolkit.getDefaultToolkit();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int ancho = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
    int alto = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
    PopupFactory pf;
    Popup po;

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());

    public UserConnectionFrame(Socket socket) {

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

        buttonOk1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pConnect.hide();
                new ChatFrame(sender);
                frame.setVisible(false);
                sender = null;
            }
        });
        buttonOk2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pError.hide();
            }
        });

        JLabel label1 = new JLabel("Username:", JLabel.RIGHT);
        label1.setBounds(0, 20, 105, 30);
        label1.setFont(new Font("Verdana", Font.PLAIN, 16));

        JLabel label2 = new JLabel("Password:", JLabel.RIGHT);
        label2.setBounds(0, 70, 105, 30);
        label2.setFont(new Font("Verdana", Font.PLAIN, 16));

        JTextField tf1 = new JTextField();
        tf1.setBounds(110, 20, 80, 30);

        JPasswordField pf1 = new JPasswordField();
        pf1.setBounds(110, 70, 80, 30);

        JButton button1 = new JButton("LOGIN");
        button1.setBounds(195, 20, 90, 30);
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = tf1.getText();
                String password = pf1.getText();
                pConnect = pfConnect.getPopup(frame, panelConnect, ancho / 2, alto / 2);
                pError = pfError.getPopup(frame, panelError, ancho / 2, alto / 2);

                try {
                    sender = new Client(socket);
                    if (sender.sendCredentials(username, password, "LOGIN")) {
                        pConnect.show();
                    } else
                        pError.show();
                } catch (NoSuchPaddingException | IOException | ClientException | InterruptedException
                        | NoSuchAlgorithmException ex) {
                    LOGGER.log(Level.FINE, ex.toString(), ex);
                }
            }
        });

        JButton button2 = new JButton("REGISTER");
        button2.setBounds(195, 70, 90, 30);
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pConnect = pfConnect.getPopup(frame, panelConnect, ancho / 2, alto / 2);
                pError = pfError.getPopup(frame, panelError, ancho / 2, alto / 2);

                try {
                    sender = new Client(socket);
                    if (sender.sendCredentials(tf1.getText(), pf1.getText(), "REGISTER")) {
                        pConnect.show();
                    } else
                        pError.show();
                } catch (NoSuchPaddingException | IOException | ClientException | InterruptedException
                        | NoSuchAlgorithmException ex) {
                            LOGGER.log(Level.FINE, ex.toString(), ex);
                }
            }
        });

        frame = new JFrame("Connection");
        frame.pack();
        frame.add(tf1);
        frame.add(pf1);
        frame.add(label1);
        frame.add(label2);
        frame.add(button1);
        frame.add(button2);
        frame.setLayout(null);
        frame.setSize(310, 160);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);

    }
}
