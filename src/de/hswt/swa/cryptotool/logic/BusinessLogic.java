package de.hswt.swa.cryptotool.logic;

import de.hswt.swa.cryptotool.data.CryptoModel;
import de.hswt.swa.cryptotool.data.CryptoModelObserver;
import de.hswt.swa.cryptotool.data.EventType;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.util.Properties;

/**
 * @author AdrianWild
 * @version 1.0
 */
public class BusinessLogic {

    private final CryptoModel model = new CryptoModel();
    private final Properties properties;

    public BusinessLogic() {
        properties = new Properties();
        try {
            String propertyFileName = "crypto.properties";
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(propertyFileName));
            properties.load(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetCryptoObject() {model.resetCryptoObject();}

    public boolean readTextFile(File file, EventType eventType) {return model.readTextFile(file.getAbsolutePath(), eventType);}

    public boolean readCryptoFile(File file) {return model.readCryptoFile(file);}

    public boolean saveAsTextFile(File file, EventType eventType) {return model.saveAsTextFile(file.getAbsolutePath(), eventType);}

    public boolean saveAsCryptoFile(File file) {return model.saveAsCryptoFile(file);}

    public boolean localEncrypt() {return model.localEncrypt();}

    public void externalEncrypt() throws IOException, InterruptedException {model.externalEncrypt(properties.getProperty("externalName"), properties.getProperty("externalDir"));}

    public void socketEncrypt() throws SocketException, InvalidKeyException {
        model.socketEncrypt(properties.getProperty("socketHostName"), Integer.parseInt(properties.getProperty("socketPort")));
    }

    public void rmiEncrypt() throws RemoteException, InvalidKeyException {
        model.rmiEncrypt(properties.getProperty("rmiHostName"), Integer.parseInt(properties.getProperty("rmiPort")));
    }

    public void apiEncrypt() throws RemoteException, InvalidKeyException {
        model.apiEncrypt(properties.getProperty("apiHostName"), properties.getProperty("apiSlug"), Integer.parseInt(properties.getProperty("apiPort")));
    }

    public boolean localDecrypt() {return model.localDecrypt();
    }

    public void externalDecrypt() throws IOException, InterruptedException {model.externalDecrypt(properties.getProperty("externalName"), properties.getProperty("externalDir"));}

    public void socketDecrypt() throws SocketException, InvalidKeyException {
        model.socketDecrypt(properties.getProperty("socketHostName"), Integer.parseInt(properties.getProperty("socketPort")));
    }

    public void rmiDecrypt() throws RemoteException, InvalidKeyException {
        model.rmiDecrypt(properties.getProperty("rmiHostName"), Integer.parseInt(properties.getProperty("rmiPort")));
    }

    public void apiDecrypt() throws RemoteException, InvalidKeyException {
        model.apiDecrypt(properties.getProperty("apiHostName"), properties.getProperty("apiSlug"), Integer.parseInt(properties.getProperty("apiPort")));
    }

    public boolean isPlainTextSet() {return model.isPlainTextSet();}

    public boolean isPasswordSet() {return model.isPasswordSet();}

    public boolean isCipherSet() {return model.isCipherSet();}

    public void setPassword(String pw) {model.setPassword(pw);}

    public void registerCryptoModelObserver(CryptoModelObserver cryptoView) {model.registerObserver(cryptoView);
    }
}
