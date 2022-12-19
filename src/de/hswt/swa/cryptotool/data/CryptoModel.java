package de.hswt.swa.cryptotool.data;

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
            String text = new String(Files.readAllBytes(Paths.get(filepath)));
            System.out.println(eventType);
            switch (eventType) {
                case IMPORT_TEXT:
                    resetCryptoObject();
                    crypto.setPlainText(text);
                    break;
                case IMPORT_CIPHER:
                    resetCryptoObject();
                    crypto.setCipher(text);
                    break;
                default:
                    System.out.println("Error occoured during file import");
                    break;
            }
            // Inform observer that the state changed
            this.fireUpdate();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("Error in readTextFile: ");
            e.printStackTrace();
        }
    }

    public void readCryptoFile(File file) {
        try {
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(fin);
            // Read objects
            crypto = (Crypto) oin.readObject();
            this.fireUpdate();
        } catch (Exception e) {
            System.out.println("Exception when saving file");
            e.printStackTrace();
        }
    }

    public boolean saveAsTextFile(String filepath, EventType eventType) {
        try {
            switch (eventType) {
                case SAVE_TEXT:
                    if (crypto.getPlainText() != null) {
                        Files.writeString(Paths.get(filepath), crypto.getPlainText());
                        return true;
                    }
                    return false;
                case SAVE_CIPHER:
                    if (crypto.getCipher() != null) {
                        Files.writeString(Paths.get(filepath), crypto.getCipher());
                    }
                    return false;
                default:
                    System.out.println("Error occoured during file save");
                    return false;
            }

        } catch (Exception e) {
            System.out.println("Exception when saving file");
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveAsCryptoFile(File file) {
        try {
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(crypto);
            oos.flush();
            oos.close();
            return true;
        } catch (Exception e) {
            System.out.println("Exception when saving crypto object to file");
            e.printStackTrace();
            return false;
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
