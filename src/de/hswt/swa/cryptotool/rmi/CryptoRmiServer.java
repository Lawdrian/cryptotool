package de.hswt.swa.cryptotool.rmi;

import de.hswt.swa.cryptotool.data.Crypto;
import de.hswt.swa.cryptotool.utils.CryptoTool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.util.Base64;

/**
 * @author AdrianWild
 * @version 1.0
 */
public class CryptoRmiServer extends UnicastRemoteObject implements CryptoRmiServerInterface {

    public CryptoRmiServer() throws Exception {}


    /**
     * Encrypts the plainText variable of a Crypto object with the password set in the crypto object. It saves the output in the cipher variable of the object and returns it.
     * @param crypto Crypto object
     * @return Crypto object
     * @throws RemoteException Server unreachable.
     * @throws InvalidKeyException Illegal password.
     */
    public synchronized Crypto encrypt(Crypto crypto) throws RemoteException, InvalidKeyException {
        CryptoTool cryptoTool = new CryptoTool();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // encrypt the plain text with the password and save the encrypted text in the cipher variable
        boolean successfulEncrypt = cryptoTool.encrypt(out, crypto.getPlainText().getBytes(), crypto.getPassword());
        if (successfulEncrypt) {
            String s = Base64.getEncoder().encodeToString(out.toByteArray());
            crypto.setCipher(s);
            return crypto;
        } else {
            throw new InvalidKeyException();
        }
    }

    /**
     * Decrypts the cipher variable of a Crypto object with the password set in the crypto object.
     * It saves the output in the plainText variable of the object and returns it.
     * @param crypto Crypto object
     * @return Crypto object
     * @throws RemoteException Server unreachable.
     * @throws InvalidKeyException Invalid password.
     */
    public synchronized Crypto decrypt(Crypto crypto) throws RemoteException, InvalidKeyException {
        CryptoTool cryptoTool = new CryptoTool();
        // decrypt the cipher with the given password and save the plain text in the plainText variable
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(crypto.getCipher());
            InputStream is = new ByteArrayInputStream(bytes);
            byte[] plainText = cryptoTool.decrypt(is, crypto.getPassword());
            if (plainText != null) {
                crypto.setPlainText(new String(plainText));
            } else {
                throw new InvalidKeyException();
            }
            return crypto;
        } catch (Exception e) {
            throw new InvalidKeyException(e);
        }

    }

    /**
     * The main method starts the server at localhost with the port 3009
     *
     */
    public static void main(String[] args) {
        try {
            String hostName = "localhost";
            if (args.length > 0) {
                hostName = args[0];
            }
            // create the broker
            try {
                // if the rmi server is being run on another host, then the hostname needs to be changed
                System.setProperty("java.rmi.server.hostname",hostName);
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
