package de.hswt.swa.cryptotool.data;

import de.hswt.swa.cryptotool.rmi.CryptoRmiClient;
import de.hswt.swa.cryptotool.socket.CryptoSocketClient;
import de.hswt.swa.cryptotool.tools.CryptoTool;

import java.io.*;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
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
                        return true;
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
            boolean successfulEncode = encoder.encode(out, crypto.getPlainText().getBytes(), crypto.getPassword());
            if (successfulEncode) {
                String s = Base64.getEncoder().encodeToString(out.toByteArray());
                crypto.setCipher(s);
            } else {
                crypto.setCipher(null);
            }
            this.fireUpdate();
            return successfulEncode;
        } catch (Exception e) {
            e.printStackTrace();
            crypto.setCipher(null);
            this.fireUpdate();
            return false;
        }
    }

    public void externalEncode(String cmd, String dir) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("java", "-jar", dir + cmd, "0", crypto.getPlainText(),crypto.getPassword());
        Process process = builder.start();
        process.waitFor();
        System.out.println("External encode exit value: " + process.exitValue());
        if (process.exitValue() != 0) {
            crypto.setCipher(null);
            this.fireUpdate();
            throw new IOException();
        }
        BufferedReader instream = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder cipher = new StringBuilder();
        String seperator = "";
        String line = instream.readLine();
        if (line == null) {
            crypto.setCipher(null);
            this.fireUpdate();
            throw new IOException();
        }
        while (line != null) {
            cipher.append(seperator);
            seperator = System.getProperty("line.separator");
            cipher.append(line);
            line = instream.readLine();
        }
        instream.close();
        crypto.setCipher(cipher.toString());
        this.fireUpdate();
    }

    public void socketEncode(String hostName, int port) throws SocketException {
        try {
            CryptoSocketClient client = new CryptoSocketClient();
            client.contactServer(hostName, port);
            String cipher = client.encode(crypto.getPlainText(), crypto.getPassword());
                if (cipher != null) {
                    crypto.setCipher(cipher);
                    this.fireUpdate();
                }
        } catch (SocketException e) {
            crypto.setCipher(null);
            this.fireUpdate();
            throw new SocketException();
        }
    }

    public void rmiEncode(String hostName, int port) throws RemoteException {
        try {
            CryptoRmiClient client = new CryptoRmiClient(hostName, port);
            System.out.println(crypto.getCipher());
            crypto = client.encode(crypto);
            System.out.println(crypto.getCipher());
            this.fireUpdate();
        } catch (Exception e) {
            crypto.setCipher(null);
            this.fireUpdate();
            throw new RemoteException();
        }
    }

    public boolean localDecode() {
        try {
            CryptoTool decoder = new CryptoTool();
            // Decode the cipher with the given password and save the plain text in the plainText variable
            byte[] bytes = Base64.getDecoder().decode(crypto.getCipher());
            InputStream is = new ByteArrayInputStream(bytes);
            byte[] plainText = decoder.decode(is, crypto.getPassword());
            crypto.setPlainText(new String(plainText));
            this.fireUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            crypto.setPlainText(null);
            this.fireUpdate();
            return false;
        }
    }

    public void externalDecode(String cmd, String dir) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("java", "-jar", dir + cmd, "1", crypto.getCipher(),crypto.getPassword());
        Process process = builder.start();
        process.waitFor();
        System.out.println("External decode exit value: " + process.exitValue());
        if (process.exitValue() != 0) {
            throw new IOException();
        }
        BufferedReader instream = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder plainText = new StringBuilder();
        String seperator = "";
        String line = instream.readLine();
        if (line == null) {
            crypto.setPlainText(null);
            this.fireUpdate();
            throw new IOException();
        }
        while (line != null) {
            plainText.append(seperator);
            seperator = System.getProperty("line.separator");
            plainText.append(line);
            line = instream.readLine();
        }
        instream.close();
        crypto.setPlainText(plainText.toString());
        this.fireUpdate();
    }

    public void socketDecode(String hostName, int port) throws SocketException, InvalidKeyException {
            CryptoSocketClient client = new CryptoSocketClient();
            client.contactServer(hostName, port);
            String plainText = client.decode(crypto.getCipher(), crypto.getPassword());
            if (plainText != null) {
                crypto.setPlainText(plainText);
                this.fireUpdate();
            }
            else {
                crypto.setPlainText(null);
                this.fireUpdate();
                throw new InvalidKeyException();
            }
    }

    public void rmiDecode(String hostName, int port) throws RemoteException, InvalidKeyException {
        try {
            System.out.println("rmiDecode start");
            CryptoRmiClient client = new CryptoRmiClient(hostName, port);
            crypto = client.decode(crypto);
            this.fireUpdate();
            //return true;
        } catch (InvalidKeyException e) {
            crypto.setPlainText(null);
            this.fireUpdate();
            throw new InvalidKeyException(e);
        } catch (MalformedURLException | NotBoundException | RemoteException e) {
            crypto.setPlainText(null);
            this.fireUpdate();
            throw new RemoteException();
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
