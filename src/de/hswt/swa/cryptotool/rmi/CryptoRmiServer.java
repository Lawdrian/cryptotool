package de.hswt.swa.cryptotool.rmi;

import de.hswt.swa.cryptotool.data.Crypto;
import de.hswt.swa.cryptotool.tools.CryptoTool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.util.Base64;

public class CryptoRmiServer extends UnicastRemoteObject implements CryptoRmiServerInterface {

    public CryptoRmiServer() throws Exception {}


    @Override
    public synchronized Crypto encrypt(Crypto crypto) throws RemoteException {
        try {
            System.out.println("Encrypt started");
            CryptoTool encoder = new CryptoTool();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Encode the plain text with the password and save the encoded text in the cipher variable
            boolean successfulEncode = encoder.encode(out, crypto.getPlainText().getBytes(), crypto.getPassword());
            if (successfulEncode) {
                String s = Base64.getEncoder().encodeToString(out.toByteArray());
                crypto.setCipher(s);
                return crypto;
            } else {
                throw new Exception("Plain text couldn't be encoded!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            crypto.setCipher(null);
            return crypto;
        }
    }

    @Override
    public synchronized Crypto decrypt(Crypto crypto) throws RemoteException, InvalidKeyException {
        CryptoTool decoder = new CryptoTool();
        // Decode the cipher with the given password and save the plain text in the plainText variable
        byte[] bytes = Base64.getDecoder().decode(crypto.getCipher());
        InputStream is = new ByteArrayInputStream(bytes);
        byte[] plainText = decoder.decode(is, crypto.getPassword());
        if (plainText != null) {
            crypto.setPlainText(new String(plainText));
        } else {
            throw new InvalidKeyException();
        }
        return crypto;
    }
    public static void main(String args[]) {
        try {

            // create the broker
            try {
                LocateRegistry.createRegistry(3009);
            } catch (RemoteException rexp) {
                rexp.printStackTrace();
            }

            CryptoRmiServer myServer = new CryptoRmiServer();

            // register server at broker
            System.out.println("Register server at broker.");
            Naming.bind("rmi://localhost:3009/cryptoService", myServer);

            System.out.println("Server successfully started.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}