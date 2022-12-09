package com.curso.chatclient;

import javax.swing.*;

import com.curso.exceptions.ClientException;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnectionFrame {
    JFrame frame;
    Popup p1;
    Popup p2;
    UserConnectionFrame user = null;
    Socket socket;

    Toolkit t = Toolkit.getDefaultToolkit();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int ancho = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
    int alto = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());

    public ClientConnectionFrame() {

        frame = new JFrame("Connection");
        PopupFactory pfConnect = new PopupFactory();
        PopupFactory pfError = new PopupFactory();
        JPanel panelConnect = new JPanel();
        JPanel panelError = new JPanel();
        JLabel labelConnect = new JLabel("Connected");
        JLabel labelError = new JLabel("Error, try again");
        JButton buttonOk1 = new JButton("OK");
        JButton buttonOk2 = new JButton("OK");
        panelConnect.add(labelConnect);
        panelConnect.add(buttonOk1);
        panelConnect.setBackground(Color.green);
        panelError.setBackground(Color.red);
        panelError.add(labelError);
        panelError.add(buttonOk2);
        p1 = pfConnect.getPopup(frame, panelConnect, ancho/2, alto/2);
        p2 = pfError.getPopup(frame, panelError, ancho/2, alto/2);

        buttonOk1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                p1.hide();
                new UserConnectionFrame(socket);
                p1 = pfConnect.getPopup(frame, panelConnect, ancho/2, alto/2);
                frame.hide();
            }
        });

        buttonOk2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                p2.hide();
                p2 = pfError.getPopup(frame, panelError, ancho/2, alto/2);
            }
        });

        JLabel label1 = new JLabel("IP:", JLabel.RIGHT);
        label1.setBounds(0, 20, 65, 30);
        label1.setFont(new Font("Verdana", Font.PLAIN, 16));

        JLabel label2 = new JLabel("PORT:", JLabel.RIGHT);
        label2.setBounds(0, 70, 65, 30);
        label2.setFont(new Font("Verdana", Font.PLAIN, 16));

        JTextField tf1 = new JTextField();
        tf1.setBounds(70, 20, 100, 30);

        JTextField tf2 = new JTextField();
        tf2.setBounds(70, 70, 100, 30);

        JButton button = new JButton("CONNECT");
        button.setBounds(175, 20, 110, 80);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Connection conct = null;
                String ip = tf1.getText();
                String port = tf2.getText();

                try {
                    if (Connection.checkIp(ip) && Connection.checkPort(port)) {
                        conct = new Connection(ip, Integer.parseInt(port));
                        socket = conct.stablishConnection();
                        p1.show();
                    } else {
                        p2.show();
                    }

                } catch (ClientException | IOException ex) {
                    p2.show();
                    LOGGER.log(Level.SEVERE, ex.toString(), ex);
                }

            }
        });

        frame.pack();
        frame.add(tf1);
        frame.add(tf2);
        frame.add(label1);
        frame.add(label2);
        frame.add(button);
        frame.setLayout(null);
        frame.setSize(310, 160);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

    }

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientConnectionFrame.class.getName()).log(
                    java.util.logging.Level.SEVERE, null,
                    ex);
        }

        ClientConnectionFrame frame = new ClientConnectionFrame();

    }
}
