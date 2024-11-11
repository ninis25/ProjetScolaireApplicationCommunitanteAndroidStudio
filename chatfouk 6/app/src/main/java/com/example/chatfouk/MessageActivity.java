package com.example.chatfouk;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    private final static int PORT = 6012;

    private DatagramSocket socket;
    private EditText sendMessageEditText;
    private Button sendButton;
    private TextView textViewChat;
    private TextView textViewMessage;
    private Button addFriendButton;

    // ArrayList pour stocker les messages
    private ArrayList<String> chat_messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);

        // Initialisation du socket et des vues
        try {
            socket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendMessageEditText = findViewById(R.id.sendMessage);
        sendButton = findViewById(R.id.send);
        textViewChat = findViewById(R.id.textViewChat);
        textViewMessage = findViewById(R.id.textViewMessages);
        addFriendButton = findViewById(R.id.btnAddFriends);

        // Initialisation de la liste des messages
        chat_messages = new ArrayList<>();

        // Envoi du message lors du clic sur le bouton "send"
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = sendMessageEditText.getText().toString();
                new SendMessageTask().execute(message);
                sendMessageEditText.setText("");
                // Ajout du message à la liste des messages
                chat_messages.add(message);
            }
        });

        // Démarrage du récepteur
        startReceiver();

        // Ajout d'un listener pour ouvrir l'AddFriendActivity
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, AddFriend.class);
                startActivity(intent);
            }
        });
    }

    private void startReceiver() {
        new Thread(new MessageReceiver()).start();
    }

    private class MessageReceiver implements Runnable {
        private DatagramSocket socket;
        private byte buffer[];

        MessageReceiver() {
            buffer = new byte[1024];
        }

        public void run() {
            try {
                socket = new DatagramSocket(PORT);
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    final String received = new String(packet.getData(), 0, packet.getLength()).trim();
                    System.out.println(received);

                    Log.d("TAG", "Message reçu : " + received); // Log de débogage

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayMessage(received);
                            viewlist();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void displayMessage(String receivedMessage) {
            textViewChat.append(receivedMessage + "\n");

            // Ajout du message à la liste des messages (avec synchronisation)
            synchronized (chat_messages) {
                chat_messages.add(receivedMessage);
            }

            // Rafraîchissement de l'interface utilisateur
            scrollToBottom(textViewChat);

            // Afficher uniquement les nouveaux messages dans textViewMessage
            if (!textViewMessage.getText().toString().contains(receivedMessage)) {
                textViewMessage.append(receivedMessage + "\n");
            }
            // Rafraîchissement de l'interface utilisateur
            scrollToBottom(textViewMessage);
        }


        private void viewlist() {
            // Afficher tous les messages dans textViewMessage (avec synchronisation)
            synchronized (chat_messages) {
                for (String message : chat_messages) {
                    textViewMessage.append(message + "\n");
                    System.out.println(message);
                }
            }
        }

        private void scrollToBottom(final TextView textView) {
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.scrollTo(0, textView.getBottom());
                }
            });
        }
    }

    private class SendMessageTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String message = params[0];
                byte[] buffer = message.getBytes();
                InetAddress address = InetAddress.getByName("192.168.1.13"); // Remplacez par l'adresse IP de votre serveur
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT);
                socket.send(packet);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
