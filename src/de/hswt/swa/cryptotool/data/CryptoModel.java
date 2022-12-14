package de.hswt.swa.cryptotool.data;

import de.hswt.swa.cryptotool.gui.MainController.EventType;
import de.hswt.swa.cryptotool.tools.CryptoTool;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;

public class CryptoModel implements CryptoModelObservable{


    private Crypto crypto = new Crypto();

    private Collection<CryptoModelObserver> observers = new ArrayList<CryptoModelObserver>();

    public void readTextFile(String filepath, EventType eventType) {


        try {

            System.out.println(new String(Files.readAllBytes(Paths.get(filepath))));
            String text = new String(Files.readAllBytes(Paths.get(filepath)));
            System.out.println("Plain: " + crypto.getPlainText());
            System.out.println("Cipher: " + crypto.getCipher());
            System.out.println(eventType);
            switch (eventType) {
                case IMPORT_TEXT:
                    crypto.setPlainText(text);
                    break;
                case IMPORT_CIPHER:
                    crypto.setCipher(text);
                    break;
                default:
                    System.out.println("Error occoured during file import");
                    break;
            }
            System.out.println("Plain: " + crypto.getPlainText());
            System.out.println("Cipher: " + crypto.getCipher());

            /*
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
            //crypto.setPlainText(text.get(1));
            */
            // Inform observer that the state changed
            this.fireUpdate();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("Error in readTextFile: ");
            e.printStackTrace();
        }
    }

    public void saveAsTextFile(String filepath, EventType eventType) {
        try {
            switch (eventType) {
                case SAVE_TEXT:
                    Files.writeString(Paths.get(filepath), crypto.getPlainText());
                    break;
                case SAVE_CIPHER:
                    Files.writeString(Paths.get(filepath), crypto.getCipher());
                    break;
                default:
                    System.out.println("Error occoured during file save");
                    break;
            }

        } catch (Exception e) {
            System.out.println("Exception when saving file");
            e.printStackTrace();
        }
    }

    public boolean localEncode() {
        try {
            CryptoTool encoder = new CryptoTool();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Encode the plain text with the password and save the encoded text in the cipher variable
            Boolean successfulEncode = encoder.encode(out, crypto.getPlainText().getBytes(), crypto.getPassword());
            String s = Base64.getEncoder().encodeToString(out.toByteArray());
            crypto.setCipher(s);
            this.fireUpdate();
            return successfulEncode;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean localDecode(String pw) {
        try {
            if (isCipherSet()) {
                CryptoTool decoder = new CryptoTool();
                // Decode the cipher with the given password and save the plain text in the plainText variable
                byte[] bytes = Base64.getDecoder().decode(crypto.getCipher());
                InputStream is = new ByteArrayInputStream(bytes);
                byte[] plain = decoder.decode(is, pw);
                crypto.setPlainText(new String(plain));

                this.fireUpdate();
                return true;
            }
            else return false;
        } catch (Exception e) {
            System.out.println("Fehler");
            e.printStackTrace();
            return false;
        }
    }


    public void resetCryptoObject() {
        crypto = new Crypto();
        this.fireUpdate();
    }

    public boolean isPlainTextSet() {
        return crypto.getPlainText() != null;
    }

    public boolean isPasswordSet() {
        return crypto.getPassword() != null;
    }

    public boolean isCipherSet() {
        return crypto.getCipher() != null;
    }

    public void setPassword(String pw) {
        crypto.setPassword(pw);
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
