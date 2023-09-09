package com.example.mobileapp;
import java.io.*;
import java.net.*;
import android.util.Log;
public class connectionmanager {
    private static final String ipAddress = "192.168.100.19"; // Replace with your device's IP address
    private static final int port = 8810; // Replace with the appropriate port number

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private boolean isConnected;

    private static connectionmanager instance;

    private connectionmanager() {
        // Private constructor to prevent instantiation
    }

    public static connectionmanager getInstance() {
        if (instance == null) {
            instance = new connectionmanager();
        }
        return instance;
    }

    public void connect() throws IOException {
        socket = new Socket(ipAddress, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        isConnected = true;
    }

    public void disconnect() throws IOException {
        writer.close();
        reader.close();
        socket.close();
        isConnected = false;
    }

    public void sendCommand(String command) throws IOException {
        writer.println(command);
        writer.flush();

        // Print the sent command
        Log.d("connectionmanager", "Sent command: " + command);

        // If you expect a response from the Raspberry Pi, you can read it using the reader:
        String response = reader.readLine();

        // Print the received response
        Log.d("connectionmanager", "Received response: " + response);

        // Process the response if needed
    }

    public String receiveResponse() throws IOException {
        // Read the response from the server
        String response = reader.readLine();

        // Print the received response
        Log.d("connectionmanager", "Received response: " + response);

        return response;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
