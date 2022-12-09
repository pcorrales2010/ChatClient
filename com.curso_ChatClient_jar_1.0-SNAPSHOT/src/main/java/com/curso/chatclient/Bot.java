/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.curso.chatclient;

import com.curso.exceptions.ClientException;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import org.json.*;

/**
 *
 * @author juacuadr
 */
public class Bot extends Client {

    public Bot(Socket newSocket) throws ClientException, NoSuchAlgorithmException {
        super(newSocket);
    }

    @Override
    public void run() {

        // Client authentication
        while (!logged) {
            logged = sendCredentials("bot", "bot", "REGISTER");
            if (!logged) {
                logged = sendCredentials("bot", "bot", "LOGIN");
            }
        }

        // Initialize a subroutine for receiving messages
        Thread listener = new Thread(listening);
        listener.start();

        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // while cola_de_mensajes no está vacía
            while (!messages.isEmpty()) {
                String message = messages.poll();
                if (message != null)
                    message = message.split("] ")[1];
                var command = Command.parseCommand(message);
                switch (command) {
                    case MENU:
                        botMenu();
                        break;
                    case HEADORTAILS:
                        headsOrTails();
                        break;
                    case JOKE:
                        jokes();
                        break;
                    case EVENT:
                        events();
                        break;
                    case NOOP:
                        break;
                }
                System.out.println(message);
            }
        }
    }

    public void botMenu() {
        sendMessage(
                "ChatBot menu: \n\'/joke\': serve a random joke \n \'/event\': serve a random event of the same day of another year  \n \'/headsortails\'");
    }

    public void jokes() {
        URL url = null;
        String read = null;
        HttpURLConnection urlc = null;

        try {
            url = new URL("https://v2.jokeapi.dev/joke/Any");
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            urlc = (HttpURLConnection) url.openConnection();

            try {
                urlc.setRequestMethod("GET");
            } catch (ProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            urlc.setRequestProperty("Content-Type", "application/json");
            urlc.setRequestProperty("X-JokesOne-Api-Secret", "YOUR API KEY HERE");
            System.out.println("Connect to: " + url.toString());
            urlc.setAllowUserInteraction(false);
            urlc.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            StringBuffer responseStr = new StringBuffer();
            while ((read = br.readLine()) != null) {
                responseStr.append(read);
            }
            br.close();
            urlc.disconnect();

            JSONObject myJson = new JSONObject(responseStr.toString());
            try {
                if (myJson.get("type").equals("twopart")) {
                    sendMessage(myJson.get("setup").toString() + "\n" + myJson.get("delivery").toString());
                } else if (myJson.get("type").equals("single")) {
                    sendMessage(myJson.get("joke").toString());
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void events() {
        URL urlForGetReq = null;
        String read = null;
        HttpURLConnection connection = null;
        Date d1 = new Date();
        Random random = new Random();

        try {
            urlForGetReq = new URL(
                    "https://byabbe.se/on-this-day/" + (d1.getMonth() + 1) + "/" + d1.getDate() + "/events.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            connection = (HttpURLConnection) urlForGetReq.openConnection();

            try {
                connection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }

            InputStreamReader isrObj = new InputStreamReader(connection.getInputStream());
            BufferedReader bf = new BufferedReader(isrObj);
            // to store the response from the servers
            StringBuffer responseStr = new StringBuffer();
            while ((read = bf.readLine()) != null) {
                responseStr.append(read);
            }
            // closing the BufferedReader
            bf.close();
            // disconnecting the connection
            connection.disconnect();
            // print the response

            JSONObject myJson = new JSONObject(responseStr.toString());
            JSONArray myJsonArray = new JSONArray(myJson.getJSONArray("events").toString());
            int numberRandom = random.nextInt(myJsonArray.length());

            // Obtener llave especifica de un objeto JSON
            sendMessage("Year: " + myJsonArray.getJSONObject(numberRandom).get("year") + ". "
                    + myJsonArray.getJSONObject(numberRandom).get("description"));

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public void headsOrTails() {
        Random random = new Random();
        String headortails;

        if (random.nextInt(2) == 0) {
            headortails = "head";
        } else
            headortails = "tail";
        sendMessage(headortails);
    }
    // documentacion de clasese
}
