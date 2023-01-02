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
                            out.println(ConnectionState.SERVER_ENCODE_ACCEPT);
                            out.flush();
                            handleEncodeRequest();
                        case CLIENT_DECODE_REQUEST:
                            out.println(ConnectionState.SERVER_DECODE_ACCEPT);
                            out.flush();
                            //handleDecodeRequest()
                        case CLIENT_CONNECTION_END:
                            out.println(ConnectionState.SERVER_CONNECTION_END);
                            out.flush();
                            client.close();
                            break;
                        default:
                            break;

                    }
                } catch (Exception e) {
                    out.println(ConnectionState.SERVER_CONNECTION_END);
                    out.flush();
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
            out.println(ConnectionState.SERVER_PLAIN_TEXT_REQUEST);
            out.flush();
            readMessageFromClient(); // plain text
            StringBuilder plainText = new StringBuilder();
            String seperator = "";
            while (messageFromClient != null && !messageFromClient.equals(ConnectionState.CLIENT_PLAIN_TEXT_DONE.name())) {
                plainText.append(seperator);
                seperator = System.getProperty("line.separator");
                plainText.append(messageFromClient);
                readMessageFromClient();
            }
            System.out.println("PlainText");
            System.out.println(plainText);
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
                    System.out.println("plainText " + plainText);
                    System.out.println("password " + password);
                    boolean successfulEncode = encoder.encode(outByte, plainText.toString().getBytes(), password.toString());
                    System.out.println(successfulEncode);
                    if (successfulEncode) {
                        String s = Base64.getEncoder().encodeToString(outByte.toByteArray());
                        sendMessage(ConnectionState.SERVER_ENCODE_SUCCESS.name());
                        sendMessage(s);
                    } else {
                        sendMessage(ConnectionState.SERVER_ENCODE_FAILURE.name());
                    }
                } else {
                    sendMessage(ConnectionState.SERVER_ENCODE_FAILURE.name());
                }
            } else {
                sendMessage(ConnectionState.SERVER_ENCODE_FAILURE.name());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessageFromClient() throws IOException {
        String msg = in.readLine();
        System.out.println("Message from client: " + msg);
        messageFromClient = msg;
    }

    private void sendMessage(String msg) {
        out.println(msg);
        out.flush();
    }

}