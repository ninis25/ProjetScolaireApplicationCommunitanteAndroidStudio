package com.example.chatfouk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class AddFriend extends AppCompatActivity {

    private LinearLayout friendsListLayout;
    private EditText searchFriendEditText;
    private Button searchButton;
    private TextView searchResultsTextView;
    private Button addFriendButton;

    private static final int SERVER_PORT = 6012; // Port du serveur UDP
    private static final String SERVER_IP = "192.168.1.13"; // Remplacez par l'adresse IP de votre serveur

    private DatagramSocket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajouter_ami); // Remplacez par le nom de votre fichier XML

        String username = getUsername(this);

        // Initialisation des vues
        friendsListLayout = findViewById(R.id.friendsList);
        searchFriendEditText = findViewById(R.id.searchFriend);
        searchButton = findViewById(R.id.searchButton);
        searchResultsTextView = findViewById(R.id.searchResults);
        addFriendButton = findViewById(R.id.addFriendButton);

        // Initialisation du socket UDP
        try {
            socket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Simulation d'une liste d'amis déjà existante
        List<String> friendList = getFriendList();
        displayFriendList(friendList);

        // Bouton pour rechercher un ami
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchedFriend = searchFriendEditText.getText().toString();
                searchResultsTextView.setText("Résultats de recherche : " + searchedFriend);
            }
        });

        // Bouton pour ajouter un ami
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendToAdd = searchFriendEditText.getText().toString();
                addFriendToList(friendToAdd);
                searchFriendEditText.setText(""); // Efface le champ de recherche après l'ajout
                new AddFriendTask().execute(friendToAdd);
            }
        });
    }

    private String getUsername(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("username", "");
    }

    private List<String> getFriendList() {
        List<String> friendList = new ArrayList<>();
        friendList.add("Ami 1");
        friendList.add("Ami 2");
        friendList.add("Ami 3");
        return friendList;
    }

    private void displayFriendList(List<String> friendList) {
        for (String friend : friendList) {
            TextView friendTextView = new TextView(this);
            friendTextView.setText(friend);
            friendsListLayout.addView(friendTextView);
        }
    }

    private void addFriendToList(String friend) {
        TextView friendTextView = new TextView(this);
        friendTextView.setText(friend);
        friendsListLayout.addView(friendTextView);
    }

    private class AddFriendTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String friendName = params[0];
                String username = getUsername(AddFriend.this);
                String message = "ajoute_ami " + username + friendName;
                byte[] buffer = message.getBytes();
                InetAddress address = InetAddress.getByName(SERVER_IP);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, SERVER_PORT);
                socket.send(packet);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
