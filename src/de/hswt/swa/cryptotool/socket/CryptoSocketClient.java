package de.hswt.swa.cryptotool.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author AdrianWild
 * @version 1.0
 */
public class CryptoSocketClient {

    private Socket server;
    private BufferedReader in;
    private PrintWriter out;
    private String messageFromServer;

    // ------------- Hauptfunktion -----------------------------

    public CryptoSocketClient() {}

    /**
     * Only used for testing purpose.
     */
    public static void main(String[] args) {

        String serverHost = "localhost";
        int port = 3008;
        CryptoSocketClient client = new CryptoSocketClient();
        try {
            client.contactServer(serverHost, port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    //----------------- Anfrage -----------------------------


    /**
     * Initiates a connection with a socket server using a defined protocol.
     * @param hostName name of the socket server.
     * @param port port that the socket server is running on.
     * @throws SocketException
     */
    public void contactServer(String hostName, int port) throws SocketException {
        try {
            // Contact the server
            server = new Socket(hostName, port);
            System.out.println("Contacting server socket....");

            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            out = new PrintWriter(server.getOutputStream());

            // 1.) Request connection with server
            sendMessage(ConnectionState.CLIENT_CONNECTION_REQUEST.name());
            waitForMessage(ConnectionState.SERVER_CONNECTION_ACCEPT.name());
            System.out.println("Connection with server established.");
        } catch (Exception e) {
            throw new SocketException();
        }
    }

    /**
     * This method first sends an encrypt request, before sending the plain text and then the password to the server.
     * Afterwards it retrieves the cipher from the server and returns it.
     * @param plainText The plain text that should be encrypted.
     * @param password The password used for encoding the plain text.
     * @return String cipher.
     */
    public String encrypt(String plainText, String password) {
        try {
            sendMessage(ConnectionState.CLIENT_ENCODE_REQUEST.name());
            readMessageFromServer();
            if (messageFromServer.equals(ConnectionState.SERVER_ENCODE_ACCEPT.name())) {
                // send the plain text
                waitForMessage(ConnectionState.SERVER_PLAIN_TEXT_REQUEST.name());
                sendMessage(plainText);
                sendMessage(ConnectionState.CLIENT_PLAIN_TEXT_DONE.name());
                // send the password
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

    /**
     * This method first sends an decrypt request, before sending the cipher and then the password to the server.
     * Afterwards it retrieves the plain text from the server and returns it.
     * If the decoding fails it returns null instead.
     * @param cipher The cipher that should be decrypted.
     * @param password The password used for decoding the cipher.
     * @return String plain text.
     */
    public String decrypt(String cipher, String password) {
        try {
            sendMessage(ConnectionState.CLIENT_DECODE_REQUEST.name());
            readMessageFromServer();
            if (messageFromServer.equals(ConnectionState.SERVER_DECODE_ACCEPT.name())) {
                // send the cipher
                waitForMessage(ConnectionState.SERVER_CIPHER_REQUEST.name());
                sendMessage(cipher);
                sendMessage(ConnectionState.CLIENT_CIPHER_DONE.name());
                // send the password
                waitForMessage(ConnectionState.SERVER_PASSWORD_REQUEST.name());
                sendMessage(password);
                sendMessage(ConnectionState.CLIENT_PASSWORD_DONE.name());

                waitForMessage(ConnectionState.SERVER_DECODE_SUCCESS.name());
                readMessageFromServer(); // plain text
                StringBuilder plainText = new StringBuilder();
                String seperator = "";
                while (messageFromServer != null && !messageFromServer.equals(ConnectionState.SERVER_PLAIN_TEXT_DONE.name())) {
                    plainText.append(seperator);
                    seperator = System.getProperty("line.separator");
                    plainText.append(messageFromServer);
                    readMessageFromServer();
                }
                return plainText.toString();

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
        if (msg != null && msg.equals(ConnectionState.SERVER_CONNECTION_CLOSE.name())) {
            throw new SocketException("Server closed connection!");
        }
        System.out.println("Message from server: " + msg);
        messageFromServer = msg;
    }

    private void sendMessage(String msg) {
        out.println(msg);
        out.flush();
    }
}
