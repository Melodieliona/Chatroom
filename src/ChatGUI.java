
import client.Client;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ChatGUI extends JFrame {

    private JTextArea messageArea;
    private JTextField textField;
    private Client client;
    private String username;

    public ChatGUI() {
        super("Chat");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        this.username = JOptionPane.showInputDialog(this, "Enter your username: ", "Name Entry",
                JOptionPane.PLAIN_MESSAGE);
        this.setTitle(username + " Chat");
        textField = new JTextField();
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String msg = username + ": " + textField.getText();
                client.sendMessage(msg);
                textField.setText("");
            }
        });
        add(textField, BorderLayout.SOUTH);

        this.client = new Client("localhost", 1234, this::onMessageReceived, username);
        client.listenForMsg();

    }

    private void onMessageReceived(String message) {
        SwingUtilities.invokeLater(() -> messageArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatGUI().setVisible(true);
        });
    }

}
