package com.example.chatfouk;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientUDP implements Runnable {
    static final int port = 6012 ;
    DatagramSocket socket;
    DatagramPacket envoye, recu;
    InetAddress address;
    public void run() {
        try {
            Log.d("test", "client run");
            address = InetAddress.getByName("192.168.1.13") ;
            socket = new DatagramSocket() ;
            scenario();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    void envoi(String msg) throws UnknownHostException, SocketException, IOException {
        int msglen = msg.length() ;
        byte [] message = new byte [msglen] ;
        message = msg.getBytes() ;
        envoye = new DatagramPacket(message, msglen, address, port) ;
        socket.send(envoye) ;
        System.out.println("msg envoye "+msg);
    }
    String recu() throws UnknownHostException, SocketException, IOException {
        byte[] buf = new byte[1000];
        recu = new DatagramPacket(buf, buf.length);
        socket.receive(recu);
        String rcvd = "rcvd from " + recu.getAddress() + ", " + recu.getPort() + ": "
                + new String(recu.getData(), 0, recu.getLength());
        System.out.println(rcvd);
        return new String(recu.getData(), 0, recu.getLength());
    }
    void scenario() throws SocketException, IOException {
        envoi("coucou");
        System.out.println("recu="+recu());
    }
}