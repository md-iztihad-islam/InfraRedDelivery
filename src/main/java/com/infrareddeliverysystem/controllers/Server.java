package com.infrareddeliverysystem.controllers;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {

    private static final ConcurrentHashMap<String, ClientHandler> riderMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ClientHandler> customerMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5555);
        System.out.println("Server started on port 5555");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String riderId;
        private String trackingId;
        private boolean isRider = false;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void sendMessage(String msg) {
            out.println(msg);
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // First message: identify client
                String init = in.readLine();
                if (init == null) return;

                String[] parts = init.split(":");
                if (parts[0].equals("RIDER") && parts.length == 3) {
                    isRider = true;
                    riderId = parts[1];
                    trackingId = parts[2];
                    riderMap.put(riderId, this);
                    System.out.println("Rider connected: " + riderId + " for tracking " + trackingId);
                } else if (parts[0].equals("CUSTOMER") && parts.length == 3) {
                    trackingId = parts[1];
                    riderId = parts[2];
                    customerMap.put(trackingId, this);
                    System.out.println("Customer connected: " + trackingId + " for rider " + riderId);
                } else {
                    out.println("Invalid identification");
                    socket.close();
                    return;
                }

                String msg;
                while ((msg = in.readLine()) != null) {
                    if (isRider) {
                        ClientHandler customer = customerMap.get(trackingId);
                        if (customer != null) {
                            customer.sendMessage("Rider: " + msg);
                        }
                    } else {
                        ClientHandler rider = riderMap.get(riderId);
                        if (rider != null) {
                            rider.sendMessage("Customer: " + msg);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Clean up
                if (isRider && riderId != null) riderMap.remove(riderId);
                if (!isRider && trackingId != null) customerMap.remove(trackingId);
                try { socket.close(); } catch (IOException ignored) {}
            }
        }
    }
}