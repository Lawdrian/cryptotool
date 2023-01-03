package de.hswt.swa.cryptotool.logic;

import de.hswt.swa.cryptotool.data.CryptoModel;
import de.hswt.swa.cryptotool.data.CryptoModelObserver;
import de.hswt.swa.cryptotool.data.EventType;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.util.Properties;

public class BusinessLogic {


    private CryptoModel model = new CryptoModel();
    private Properties properties;
    private final String propertyFileName = "crypto.properties";

    public BusinessLogic() {
        properties = new Properties();
        try {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(propertyFileName));
            properties.load(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readTextFile(File file, EventType eventType) {model.readTextFile(file.getAbsolutePath(), eventType);}

    public void resetCryptoObject() {model.resetCryptoObject();}
    public void readCryptoFile(File file) {model.readCryptoFile(file);}

    public boolean saveAsTextFile(File file, EventType eventType) {return model.saveAsTextFile(file.getAbsolutePath(), eventType);}

    public boolean saveAsCryptoFile(File file) {return model.saveAsCryptoFile(file);}

    public boolean localEncode() {
        return model.localEncode();
    }

    public void socketEncode() throws SocketException {
        model.socketEncode(properties.getProperty("socketHostName"), Integer.parseInt(properties.getProperty("socketPort")));
    }

    public void rmiEncode() throws RemoteException {
        model.rmiEncode(properties.getProperty("rmiHostName"), Integer.parseInt(properties.getProperty("rmiPort")));
    }

    public boolean localDecode() {
        return model.localDecode();
    }

    public void socketDecode() throws SocketException, InvalidKeyException {
        model.socketDecode(properties.getProperty("socketHostName"), Integer.parseInt(properties.getProperty("socketPort")));
    }

    public void rmiDecode() throws RemoteException, InvalidKeyException {
        model.rmiDecode(properties.getProperty("rmiHostName"), Integer.parseInt(properties.getProperty("rmiPort")));
    }

    public boolean isPlainTextSet() {return model.isPlainTextSet();}

    public boolean isPasswordSet() {return model.isPasswordSet();}

    public boolean isCipherSet() {return model.isCipherSet();}

    public void setPassword(String pw) {model.setPassword(pw);}

    public void registerCryptoModelObserver(CryptoModelObserver cryptoView) {
        model.registerObserver(cryptoView);
    }

}
