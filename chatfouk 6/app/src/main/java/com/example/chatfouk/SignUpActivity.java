package com.example.chatfouk;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextUsername = findViewById(R.id.editTextUsername);
                EditText editTextPassword = findViewById(R.id.editTextPassword);
                EditText editTextEmail = findViewById(R.id.editTextEmail);

                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                String email = editTextEmail.getText().toString();

                if (validateRegistration(username, password, email)) {
                    new RegistrationTask().execute(username, password, email);
                }
            }
        });
    }

    private class RegistrationTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String email = params[2];

            try (DatagramSocket socket = new DatagramSocket()) {
                InetAddress address = InetAddress.getByName("192.168.1.13"); // Remplacez par l'adresse IP de votre serveur
                String message = "creation " + username + " " + password + " " + email;
                byte[] buf = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 6012);
                socket.send(packet);

                // Attendre la réponse du serveur
                buf = new byte[1024];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String response = new String(packet.getData(), 0, packet.getLength());
                return response.contains("Inscription réussie");
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(SignUpActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                // Vous pouvez également rediriger l'utilisateur vers la page de connexion ou une autre activité ici
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateRegistration(String username, String password, String email) {
        if (username.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Email cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
