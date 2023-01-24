package de.hswt.swa.cryptotool.socket;

import de.hswt.swa.cryptotool.utils.CryptoTool;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

/**
 * @author AdrianWild
 * @version 1.0
 * This class relies on the CryptoTool class. Because of that, the project structure
 * cannot be changed without thought.
 */
public class CryptoSocketWorker extends Thread {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private String messageFromClient;

    public CryptoSocketWorker(Socket clientRequest) {client = clientRequest;}

    /**
     * This method will be executed when this worker gets started by the server.
     * First it waits for the client to initialize the handshake via ConnectionState protocol.
     * After that it waits for the client to either request an encoding or request a decoding.
     * Further methods will be called if that happens.
     * The client can also request the connection to be closed. This method closes the connection, if that happens.
     */
    public void run() {
        try {
            if (client != null) {

                in = new BufferedReader(new InputStreamReader(
                        client.getInputStream()));
                out = new PrintWriter(client.getOutputStream());
                messageFromClient = in.readLine();
                while (!messageFromClient.equals(ConnectionState.CLIENT_CONNECTION_REQUEST.name())) {
                    messageFromClient = in.readLine();
                }
                System.out.println("Connection established");
                out.println(ConnectionState.SERVER_CONNECTION_ACCEPT);
                out.flush();

                // Expecting a message with what the client wants to do
                ConnectionState state;
                messageFromClient = in.readLine();
                System.out.println(messageFromClient);
                try {
                    state = ConnectionState.valueOf(messageFromClient);
                    switch (state) {
                        case CLIENT_ENCRYPT_REQUEST:
                            sendMessage(ConnectionState.SERVER_ENCRYPT_ACCEPT.name());
                            handleEncryptRequest();
                            break;
                        case CLIENT_DECRYPT_REQUEST:
                            sendMessage(ConnectionState.SERVER_DECRYPT_ACCEPT.name());
                            handleDecryptRequest();
                            break;
                        case CLIENT_CONNECTION_CLOSE:
                            sendMessage(ConnectionState.SERVER_CONNECTION_CLOSE.name());
                            client.close();
                            break;
                        default:
                            break;

                    }
                } catch (IOException e) {
                    sendMessage(ConnectionState.SERVER_CONNECTION_CLOSE.name());
                    client.close();
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles server side encoding.<br>
     * This method first requests the plain text from the client. After receiving the plain text it requests the password to
     * encrypt the plain text with. It then tries to encrypt the plain text. If it was successful, it sends the cipher to the client.
     * If not it sends an error message and closes the connection.
     */
    private void handleEncryptRequest() {
        try {
            sendMessage(ConnectionState.SERVER_PLAIN_TEXT_REQUEST.name());
            readMessageFromClient(); // plain text
            StringBuilder plainText = new StringBuilder();
            String seperator = "";
            if (messageFromClient == null) {
                handleEncryptFailure();
            }
            while (messageFromClient != null && !messageFromClient.equals(ConnectionState.CLIENT_PLAIN_TEXT_DONE.name())) {
                plainText.append(seperator);
                seperator = System.getProperty("line.separator");
                plainText.append(messageFromClient);
                readMessageFromClient();
            }
            sendMessage(ConnectionState.SERVER_PASSWORD_REQUEST.name());
            readMessageFromClient(); // password
            if (messageFromClient == null) {
                handleEncryptFailure();
            }
            StringBuilder password = readPassword();
            CryptoTool cryptoTool = new CryptoTool();
            ByteArrayOutputStream outByte = new ByteArrayOutputStream();
            boolean successfulEncrypt = cryptoTool.encrypt(outByte, plainText.toString().getBytes(), password.toString());
            if (successfulEncrypt) {
                String s = Base64.getEncoder().encodeToString(outByte.toByteArray());
                sendMessage(ConnectionState.SERVER_ENCRYPT_SUCCESS.name());
                sendMessage(s);
                client.close();
                outByte.close();
            } else {
                outByte.close();
                handleEncryptFailure();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles server side decoding.<br>
     * This method first requests the cipher from the client. After receiving the cipher it requests the password to
     * decrypt the cipher with. It then tries to decrypt the cipher. If it was successful, it sends the plain text to the client.
     * If not it sends an error message and closes the connection.
     */
    private void handleDecryptRequest() {
        try {
            sendMessage(ConnectionState.SERVER_CIPHER_REQUEST.name());
            readMessageFromClient(); // cipher
            if (messageFromClient == null) {
                handleDecryptFailure();
            }
            StringBuilder cipher = new StringBuilder();
            String seperator = "";
            while (messageFromClient != null && !messageFromClient.equals(ConnectionState.CLIENT_CIPHER_DONE.name())) {
                cipher.append(seperator);
                seperator = System.getProperty("line.separator");
                cipher.append(messageFromClient);
                readMessageFromClient();
            }
            sendMessage(ConnectionState.SERVER_PASSWORD_REQUEST.name());
            readMessageFromClient(); // password
            if (messageFromClient == null) {
                handleDecryptFailure();
            }
            StringBuilder password = readPassword();
            try {
                CryptoTool cryptoTool = new CryptoTool();
                byte[] bytes = Base64.getDecoder().decode(cipher.toString());
                InputStream is = new ByteArrayInputStream(bytes);
                byte[] plainText = cryptoTool.decrypt(is, password.toString());
                is.close();
                if (plainText != null) {
                    sendMessage(ConnectionState.SERVER_DECRYPT_SUCCESS.name());
                    sendMessage(new String(plainText));
                    sendMessage(ConnectionState.SERVER_PLAIN_TEXT_DONE.name());
                } else {
                    handleDecryptFailure();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessageFromClient() throws IOException {
        String msg = in.readLine();
        if (msg != null && msg.equals(ConnectionState.CLIENT_CONNECTION_CLOSE.name())) {
            System.out.println("Client wants to close connection!");
            client.close();
        }
        System.out.println("Message from client: " + msg);
        messageFromClient = msg;
    }

    /**
     * This method is a helper method that reads the password from the client.
     * @return StringBuilder password.
     * @throws IOException Error occurred when reading line.
     */
    private StringBuilder readPassword() throws IOException {
        String seperator;
        StringBuilder password = new StringBuilder();
        seperator = "";
        while (messageFromClient != null && !messageFromClient.equals(ConnectionState.CLIENT_PASSWORD_DONE.name())) {
            password.append(seperator);
            seperator = System.getProperty("line.separator");
            password.append(messageFromClient);
            readMessageFromClient();
        }
        return password;
    }

    private void sendMessage(String msg) {
        out.println(msg);
        out.flush();
    }

    private void handleEncryptFailure() throws IOException {
        sendMessage(ConnectionState.SERVER_ENCRYPT_FAILURE.name());
        sendMessage(ConnectionState.SERVER_CONNECTION_CLOSE.name());
        this.client.close();
    }

    private void handleDecryptFailure() throws IOException {
        sendMessage(ConnectionState.SERVER_DECRYPT_FAILURE.name());
        sendMessage(ConnectionState.SERVER_CONNECTION_CLOSE.name());
        this.client.close();
    }


}