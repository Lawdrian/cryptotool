package de.hswt.swa.cryptotool.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Eine Klasse zur Demonstration der Client Technologie bei einer Socket-Verbindung.
 *
 */
public class CryptoSocketClient {

    private Socket server;
    private BufferedReader in;
    private PrintWriter out;
    private String messageFromServer;

    // ------------- Hauptfunktion -----------------------------

    public CryptoSocketClient() {
    }

    /**
     *
     *
     */
    public static void main(String[] args) {

        String serverHost = "localhost";
        int port = 2200;
        CryptoSocketClient client = new CryptoSocketClient();
        client.contactServer(serverHost, port);
    }

    //----------------- Anfrage -----------------------------

    /**
     * Methode, die die Kommunikation mit dem Server realisiert.
     *
     * @param serverHost der DNS Name des Rechner smit dem Server-Programm
     * @param port der Port auf dem das Server-Programm lauscht.
     */
    public boolean contactServer(String serverHost, int port) {
        try {
            // Contact the server
            server = new Socket(serverHost, port);
            System.out.println("Contacting server socket....");

            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            out = new PrintWriter(server.getOutputStream());

            // 1.) Request connection with server
            sendMessage(ConnectionState.CLIENT_CONNECTION_REQUEST.name());
            waitForMessage(ConnectionState.SERVER_CONNECTION_ACCEPT.name());
            System.out.println("Connection with server established.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String encode(String text, String password) {
        try {
            sendMessage(ConnectionState.CLIENT_ENCODE_REQUEST.name());
            readMessageFromServer();
            if (messageFromServer.equals(ConnectionState.SERVER_ENCODE_ACCEPT.name())) {
                waitForMessage(ConnectionState.SERVER_PLAIN_TEXT_REQUEST.name());
                sendMessage(text);
                sendMessage(ConnectionState.CLIENT_PLAIN_TEXT_DONE.name());

                waitForMessage(ConnectionState.SERVER_PASSWORD_REQUEST.name());
                sendMessage(password);
                sendMessage(ConnectionState.CLIENT_PASSWORD_DONE.name());
                waitForMessage(ConnectionState.SERVER_ENCODE_SUCCESS.name());
                readMessageFromServer(); // cipher
                return messageFromServer;

            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void waitForMessage(String msg) throws IOException {
        while (messageFromServer == null || !messageFromServer.equals(msg)) {
            readMessageFromServer();
        }
    }

    private void readMessageFromServer() throws IOException {
        String msg = in.readLine();
        System.out.println("Message from server: " + msg);
        messageFromServer = msg;
    }

    private void sendMessage(String msg) {
        out.println(msg);
        out.flush();
    }
}
