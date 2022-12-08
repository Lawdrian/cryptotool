package de.hswt.swa.cryptotool.logic;

import de.hswt.swa.cryptotool.data.CryptoModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BusinessLogic {


    private CryptoModel model = new CryptoModel();

    public BusinessLogic() {

    }

    public void readTextFile(File file) {model.readTextFile(file.getAbsolutePath());}

    public void readCypherObject(File file) {
    }
}
