package de.hswt.swa.cryptotool.rmi;

import de.hswt.swa.cryptotool.data.Crypto;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;

/**
 * @author AdrianWild
 * @version 1.0
 */
public interface CryptoRmiServerInterface extends Remote {

    Crypto encrypt(Crypto crypto) throws RemoteException, InvalidKeyException;

    Crypto decrypt(Crypto crypto) throws RemoteException, InvalidKeyException;

}
