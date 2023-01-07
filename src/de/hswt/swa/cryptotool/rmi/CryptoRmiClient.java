package de.hswt.swa.cryptotool.rmi;

import de.hswt.swa.cryptotool.data.Crypto;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;

/**
 * @author AdrianWild
 * @version 1.0
 */
public class CryptoRmiClient {

    private CryptoRmiServerInterface server;

    /**
     * Constructor: Builds connection to the rmi server.
     * @param hostName name of the rmi server.
     * @param port port that the rmi server is running on.
     * @throws MalformedURLException
     * @throws NotBoundException
     * @throws RemoteException
     */
    public CryptoRmiClient(String hostName, int port) throws MalformedURLException, NotBoundException, RemoteException {
        server = (CryptoRmiServerInterface) Naming.lookup("rmi://" + hostName + ":" + port + "/cryptoService");
        System.out.println("RMI client");
    }

    public Crypto encrypt(Crypto crypto) throws RemoteException {
        if (server != null) {
            crypto = server.encrypt(crypto);
        }
        return crypto;
    }
    public Crypto decrypt(Crypto crypto) throws RemoteException, InvalidKeyException {
        System.out.println("RMIDECODECLIENT");
        System.out.println(crypto.getCipher());
        if (server != null) {
            System.out.println(crypto.getCipher());
            crypto = server.decrypt(crypto);
            System.out.println(crypto.getCipher());
        }
        return crypto;
    }

}
