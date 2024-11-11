package com.example.chatfouk;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class messageITEM extends AppCompatActivity implements Runnable {
    public final static int PORT = 6012;
    private final static int BUFFER = 1024;
    private final static int MAX_USERS = 100;

    private DatagramSocket socket;
    private ArrayList<InetAddress> clientAddresses = new ArrayList<>();
    private ArrayList<Integer> clientPorts = new ArrayList<>();
    private TextView textViewChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_item);

        textViewChat = findViewById(R.id.textViewChat);

        // Démarrer le serveur
        start();
    }

    private void start() {
        try {
            socket = new DatagramSocket(PORT);
            Thread serverThread = new Thread((Runnable) this);
            serverThread.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[BUFFER];
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(buffer, 0, packet.getLength());

                // Traitement du message et envoi aux utilisateurs connectés
                broadcastMessage(packet, message);
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    private void updateChatView(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String currentMessages = textViewChat.getText().toString();
                String updatedMessages = currentMessages + "\n" + message;
                textViewChat.setText(updatedMessages);
            }
        });
    }

    private void broadcastMessage(DatagramPacket packet, String message) throws IOException {
        String senderInfo = packet.getAddress().toString() + "|" + packet.getPort();
        System.out.println(senderInfo + " : " + message);

        byte[] data = (senderInfo + " : " + message).getBytes();
        for (int i = 0; i < clientAddresses.size(); i++) {
            InetAddress clientAddress = clientAddresses.get(i);
            int clientPort = clientPorts.get(i);
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, clientAddress, clientPort);
            socket.send(sendPacket);
        }

        // Afficher le message reçu dans la vue
        updateChatView(message);
    }
}
