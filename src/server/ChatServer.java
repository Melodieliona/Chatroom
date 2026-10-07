package server;

import java.io.*;
import java.net.*;

public class ChatServer {
    private final ServerSocket serverSocket;

    // Create ServerSocket
    public ChatServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        System.out.println("Server started. Waiting for clients...");
    }

    // Start Server
    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                // Wait for Client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected. " + clientSocket);

                // Create Clientthread
                ClientHandler clientThread = new ClientHandler(clientSocket);
                new Thread(clientThread).start();
            }

        } catch (IOException e) {
            closeServerSocker();
        }
    }

    // Stop Server
    public void closeServerSocker() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Create ClientThread

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        ChatServer server = new ChatServer(serverSocket);
        server.startServer();
    }
}
