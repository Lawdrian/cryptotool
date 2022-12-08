package de.hswt.swa.cryptotool.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CryptoModel implements CryptoModelObservable{


    private Crypto crypto = new Crypto();

    private Collection<CryptoModelObserver> observers = new ArrayList<CryptoModelObserver>();




    public void readTextFile(String filepath) {

        try {
            FileInputStream fs = new FileInputStream(filepath);
            InputStreamReader isr = new InputStreamReader(fs);
            BufferedReader input = new BufferedReader(isr);

            String line = input.readLine();
            List<String> text = new ArrayList<>();
            while  (line != null) { // noch nicht am Dateiende
                // verarbeite aktuelle Zeile
                System.out.println(line);
                text.add( " " + line);

                //lese nächste Zeile
                line = input.readLine();
            }
            System.out.println("Gesamter Text");
            System.out.println(text);
            crypto.setPlainText(text.get(1));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("Error in readTextFile: ");
            e.printStackTrace();
        }
    }




    @Override
    public void registerObserver(CryptoModelObserver observer) {
        observers.add(observer);
    }

    @Override
    public void unRegisterObserver(CryptoModelObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void fireUpdate() {
        for (CryptoModelObserver obs: observers) {
            obs.update(crypto);
        }

    }


}
