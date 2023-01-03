package de.hswt.swa.cryptotool.rmi;

import de.hswt.swa.cryptotool.data.Crypto;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;

public interface CryptoRmiServerInterface extends Remote {

    Crypto encrypt(Crypto crypto) throws RemoteException;

    Crypto decrypt(Crypto crypto) throws RemoteException, InvalidKeyException;

}
