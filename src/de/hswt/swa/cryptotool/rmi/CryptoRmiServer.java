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
     * @throws RemoteException
     */
    public Crypto encrypt(Crypto crypto) throws RemoteException {
        try {
            System.out.println("Encrypt started");
            CryptoTool cryptoTool = new CryptoTool();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // encrypt the plain text with the password and save the encrypted text in the cipher variable
            boolean successfulEncrypt = cryptoTool.encrypt(out, crypto.getPlainText().getBytes(), crypto.getPassword());
            if (successfulEncrypt) {
                String s = Base64.getEncoder().encodeToString(out.toByteArray());
                crypto.setCipher(s);
                return crypto;
            } else {
                throw new Exception("Plain text couldn't be encrypted!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            crypto.setCipher(null);
            return crypto;
        }
    }

    /**
     * Decrypts the cipher variable of a Crypto object with the password set in the crypto object. It saves the output in the plainText variable of the object and returns it.
     * @param crypto Crypto object
     * @return Crypto object
     * @throws RemoteException
     * @throws InvalidKeyException
     */
    public Crypto decrypt(Crypto crypto) throws RemoteException, InvalidKeyException {
        CryptoTool cryptoTool = new CryptoTool();
        // decrypt the cipher with the given password and save the plain text in the plainText variable
        byte[] bytes = Base64.getDecoder().decode(crypto.getCipher());
        InputStream is = new ByteArrayInputStream(bytes);
        byte[] plainText = cryptoTool.decrypt(is, crypto.getPassword());
        if (plainText != null) {
            crypto.setPlainText(new String(plainText));
        } else {
            throw new InvalidKeyException();
        }
        return crypto;
    }

    /**
     * The main method starts the server at localhost with the port 3009
     *
     */
    public static void main(String[] args) {
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
