package de.hswt.swa.cryptotool.logic;

import de.hswt.swa.cryptotool.data.CryptoModel;
import de.hswt.swa.cryptotool.data.CryptoModelObserver;
import de.hswt.swa.cryptotool.gui.MainController.EventType;

import java.io.File;

public class BusinessLogic {


    private CryptoModel model = new CryptoModel();

    public BusinessLogic() {

    }

    public void readTextFile(File file, EventType eventType) {model.readTextFile(file.getAbsolutePath(), eventType);}

    public void resetCryptoObject() {model.resetCryptoObject();}
    public void readCryptoObject(File file) {
    }

    public void saveAsTextFile(File file, EventType eventType) {model.saveAsTextFile(file.getAbsolutePath(), eventType);}

    public boolean localEncode() {return model.localEncode();}

    public boolean localDecode(String pw) {return model.localDecode(pw);}

    public boolean isPlainTextSet() {return model.isPlainTextSet();}

    public boolean isPasswordSet() {return model.isPasswordSet();}

    public boolean isCipherSet() {return model.isCipherSet();}

    public void setPassword(String pw) {model.setPassword(pw);}

    public void registerCryptoModelObserver(CryptoModelObserver cryptoView) {
        model.registerObserver(cryptoView);
    }

}
