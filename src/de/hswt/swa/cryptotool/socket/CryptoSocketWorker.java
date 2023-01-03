package de.hswt.swa.cryptotool.socket;

import de.hswt.swa.cryptotool.tools.CryptoTool;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class CryptoSocketWorker extends Thread {

    private Socket client;

    private BufferedReader in;
    private PrintWriter out;
    private String messageFromClient;

    public CryptoSocketWorker(Socket clientRequest) {client = clientRequest;}

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
                        case CLIENT_ENCODE_REQUEST:
                            sendMessage(ConnectionState.SERVER_ENCODE_ACCEPT.name());
                            handleEncodeRequest();
                        case CLIENT_DECODE_REQUEST:
                            sendMessage(ConnectionState.SERVER_DECODE_ACCEPT.name());
                            handleDecodeRequest();
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


    private void handleEncodeRequest() {
        try {

            // expecting the next line to be the plain text
            sendMessage(ConnectionState.SERVER_PLAIN_TEXT_REQUEST.name());
            readMessageFromClient(); // plain text
            StringBuilder plainText = new StringBuilder();
            String seperator = "";
            while (messageFromClient != null && !messageFromClient.equals(ConnectionState.CLIENT_PLAIN_TEXT_DONE.name())) {
                plainText.append(seperator);
                seperator = System.getProperty("line.separator");
                plainText.append(messageFromClient);
                readMessageFromClient();
            }
            if (!plainText.isEmpty()) {
                sendMessage(ConnectionState.SERVER_PASSWORD_REQUEST.name());
                readMessageFromClient(); // password
                StringBuilder password = new StringBuilder();
                seperator = "";
                while (messageFromClient != null && !messageFromClient.equals(ConnectionState.CLIENT_PASSWORD_DONE.name())) {
                    password.append(seperator);
                    seperator = System.getProperty("line.separator");
                    password.append(messageFromClient);
                    readMessageFromClient();
                }
                if (!password.isEmpty()) {
                    CryptoTool encoder = new CryptoTool();
                    ByteArrayOutputStream outByte = new ByteArrayOutputStream();
                    boolean successfulEncode = encoder.encode(outByte, plainText.toString().getBytes(), password.toString());
                    System.out.println(successfulEncode);
                    if (successfulEncode) {
                        String s = Base64.getEncoder().encodeToString(outByte.toByteArray());
                        sendMessage(ConnectionState.SERVER_ENCODE_SUCCESS.name());
                        sendMessage(s);
                    } else {
                        sendMessage(ConnectionState.SERVER_ENCODE_FAILURE.name());
                        sendMessage(ConnectionState.SERVER_CONNECTION_CLOSE.name());
                        client.close();
                    }
                } else {
                    sendMessage(ConnectionState.SERVER_ENCODE_FAILURE.name());
                    sendMessage(ConnectionState.SERVER_CONNECTION_CLOSE.name());
                    client.close();
                }
            } else {
                sendMessage(ConnectionState.SERVER_ENCODE_FAILURE.name());
                sendMessage(ConnectionState.SERVER_CONNECTION_CLOSE.name());
                client.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDecodeRequest() {
        try {
            // expecting the next line to be the cipher
            sendMessage(ConnectionState.SERVER_CIPHER_REQUEST.name());
            readMessageFromClient(); // cipher
            StringBuilder cipher = new StringBuilder();
            String seperator = "";
            while (messageFromClient != null && !messageFromClient.equals(ConnectionState.CLIENT_CIPHER_DONE.name())) {
                cipher.append(seperator);
                seperator = System.getProperty("line.separator");
                cipher.append(messageFromClient);
                readMessageFromClient();
            }
            if (!cipher.isEmpty()) {
                sendMessage(ConnectionState.SERVER_PASSWORD_REQUEST.name());
                readMessageFromClient(); // password
                StringBuilder password = new StringBuilder();
                seperator = "";
                while (messageFromClient != null && !messageFromClient.equals(ConnectionState.CLIENT_PASSWORD_DONE.name())) {
                    password.append(seperator);
                    seperator = System.getProperty("line.separator");
                    password.append(messageFromClient);
                    readMessageFromClient();
                }
                if (!password.isEmpty()) {
                    try {
                        CryptoTool decoder = new CryptoTool();
                        byte[] bytes = Base64.getDecoder().decode(cipher.toString());
                        InputStream is = new ByteArrayInputStream(bytes);
                        byte[] plainText = decoder.decode(is, password.toString());
                        if (plainText != null) {
                            sendMessage(ConnectionState.SERVER_DECODE_SUCCESS.name());
                            sendMessage(new String(plainText));
                            sendMessage(ConnectionState.SERVER_PLAIN_TEXT_DONE.name());
                        } else {
                            sendMessage(ConnectionState.SERVER_DECODE_FAILURE.name());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    sendMessage(ConnectionState.SERVER_DECODE_FAILURE.name());
                    sendMessage(ConnectionState.SERVER_CONNECTION_CLOSE.name());
                    client.close();
                }
            } else {
                sendMessage(ConnectionState.SERVER_DECODE_FAILURE.name());
                sendMessage(ConnectionState.SERVER_CONNECTION_CLOSE.name());
                client.close();
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

    private void sendMessage(String msg) {
        out.println(msg);
        out.flush();
    }

}