package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class Client {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Consumer<String> onMsgReceived;

    public Client(String serverAdress, int serverPort, Consumer<String> onMsgReceived, String username) {
        try {
            this.socket = new Socket(serverAdress, serverPort);
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.onMsgReceived = onMsgReceived;

            out.write(username);
            out.newLine();
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(socket, in, out);
        }
    }

    public void sendMessage(String msgToSend) {
        try {
            out.write(msgToSend);
            out.newLine();
            out.flush();
            if (msgToSend.equals("EXIT")) {
                closeEverything(socket, in, out);
                System.exit(0);
            }
            // }
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
        if (socket == null) {
            System.out.println("No connection to server.");
            return;
        }
        new Thread(() -> {

            String msgFromGC;
            while (socket.isConnected()) {
                try {
                    msgFromGC = in.readLine();
                    if (msgFromGC == null) {
                        System.out.println("Disconnected from server.");
                        closeEverything(socket, in, out);
                        break;
                    }
                    // System.out.println(msgFromGC);
                    onMsgReceived.accept(msgFromGC);
                } catch (IOException e) {
                    closeEverything(socket, in, out);
                }
            }

        }).start();

    }
}
