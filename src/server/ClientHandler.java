package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;
    private String clientUsername;

    // create client with reader and writer and add them to clients
    public ClientHandler(Socket socket) {
        try {
            this.clientSocket = socket;
            this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.clientUsername = in.readLine();
            clients.add(this);
            broadcastMessage("Server " + clientUsername + " has entered the chat!");

        } catch (IOException e) {
            closeEverything(clientSocket, in, out);
        }

    }

    // listen constantly for new messages
    @Override
    public void run() {
        String messageFromClient;

        while (clientSocket.isConnected()) {
            try {
                messageFromClient = in.readLine();
                broadcastMessage(messageFromClient);

            } catch (IOException e) {
                closeEverything(clientSocket, in, out);
                break;
            }

        }
    }

    // Send message to every client
    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clients) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.out.write(messageToSend);
                    clientHandler.out.newLine();
                    clientHandler.out.flush();
                }
            } catch (IOException e) {
                closeEverything(clientSocket, in, out);
            }
        }
    }

    // Remove this client
    public void removeClientHandler() {
        clients.remove(this);
        broadcastMessage("Server: " + clientUsername + " has left the chat!");
    }

    // Close socket, reader and writer
    public void closeEverything(Socket clientSocket, BufferedReader in, BufferedWriter out) {
        removeClientHandler();
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
