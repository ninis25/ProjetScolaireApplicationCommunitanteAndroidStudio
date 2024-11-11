package com.example.chatfouk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        final Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editTextUsername = findViewById(R.id.editTextLoginUsername);
                final EditText editTextPassword = findViewById(R.id.editTextLoginPassword);

                final String username = editTextUsername.getText().toString();
                final String password = editTextPassword.getText().toString();

                new LoginTask().execute(username, password);
            }
        });
    }

    private void saveUsername(Context context, String username) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.apply();
    }

    private class LoginTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            //save the string username in String.xml :

            String password = params[1];

            try (DatagramSocket socket = new DatagramSocket()) {
                InetAddress address = InetAddress.getByName("192.168.1.13"); // Remplacez par l'adresse IP de votre serveur
                String message = "connexion " + username + " " + password;
                byte[] buf = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 6012);
                socket.send(packet);

                // Attendre la réponse du serveur
                buf = new byte[1024];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String response = new String(packet.getData(), 0, packet.getLength());
                return response.contains("Connexion réussie");
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Intent intent = new Intent(LoginActivity.this, MessageActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();

                EditText editTextUsername = findViewById(R.id.editTextLoginUsername);
                EditText editTextPassword = findViewById(R.id.editTextLoginPassword);
                editTextUsername.setText("");
                editTextPassword.setText("");
            }
        }
    }
}
