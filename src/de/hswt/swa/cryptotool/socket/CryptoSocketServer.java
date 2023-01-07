package de.hswt.swa.cryptotool.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author AdrianWild
 * @version 1.0
 * */

public class CryptoSocketServer {

    private ServerSocket server;

    /**
     * Constructor: Creates a new server object running on the provided port.
     *
     * @param port: the port, which the server is running on.
     */
    public CryptoSocketServer(int port) throws IOException {
        // Using port @port:
        server = new ServerSocket(port);

        System.out.println(server.getLocalSocketAddress());
        System.out.println("Server successfully started on port " + port + ".");

        // Waiting for clients to contact the server.
        while (true) {
            // Wait for a client request:
            Socket clientRequest = server.accept();

            // Process client request:
            CryptoSocketWorker worker = new CryptoSocketWorker(clientRequest);
            worker.start();
        }
    }


    /**
     * This method starts the socket server.
     */
    public static void main(String[] args) {
        // Starting server
        try {
            new CryptoSocketServer(3008);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
