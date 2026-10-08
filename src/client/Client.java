package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;

        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    public void sendMessage() {
        try {
            out.write(username);
            out.newLine();
            out.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String msgToSend = scanner.nextLine();
                out.write(username + ": " + msgToSend);
                out.newLine();
                out.flush();
                if (msgToSend.equals("EXIT")) {
                    closeEverything(socket, in, out);
                    System.exit(0);
                }
            }
        } catch (IOException e) {

            closeEverything(socket, in, out);
        }
    }

    public void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void listenForMsg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGC;
                while (socket.isConnected()) {
                    try {
                        msgFromGC = in.readLine();
                        if (msgFromGC == null) {
                            System.out.println("Disconnected from server.");
                            closeEverything(socket, in, out);
                            break;
                        }
                        System.out.println(msgFromGC);
                    } catch (IOException e) {
                        closeEverything(socket, in, out);
                    }
                }
            }
        }).start();

    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter username to enter chat: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username);
        client.listenForMsg();
        client.sendMessage();

    }

}
