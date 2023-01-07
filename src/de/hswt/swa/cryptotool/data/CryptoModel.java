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

/**
 * @author AdrianWild
 * @version 1.0
 */
public class CryptoModel implements CryptoModelObservable{

    private Crypto crypto = new Crypto();
    private final Collection<CryptoModelObserver> observers = new ArrayList<CryptoModelObserver>();

    /**
     * Reads the content of a text file and depending on the @eventType value saves it either in
     * the @plainText or @cipher variable of the crypto object.
     * @param filepath The path where the file is stored.
     * @param eventType EventType object.
     * @return Boolean, true if everything worked fine, false if an exception occurred.
     */
    public boolean readTextFile(String filepath, EventType eventType) {

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
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reads the content of a serialized Crypto object and sets it as the current @crypto object.
     * @param file The file where the Crypto object is saved in.
     * @return Boolean, true if everything worked fine, false if an exception occurred.
     */
    public boolean readCryptoFile(File file) {
        try {
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(fin);
            // Read objects
            crypto = (Crypto) oin.readObject();
            this.fireUpdate();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves either the @plainText or @cipher variable to a file depending on the @eventType value.
     * @param filepath Path of the file where the text should be saved.
     * @param eventType EventType object.
     * @return Boolean, true if everything worked fine, false if an exception occurred.
     */
    public boolean saveAsTextFile(String filepath, EventType eventType) {
        try {
            PrintWriter out = new PrintWriter(filepath);
            switch (eventType) {
                case SAVE_TEXT:
                    if (crypto.getPlainText() != null) {
                        out.println(crypto.getPlainText());
                        out.close();
                        return true;
                    }
                    return false;
                case SAVE_CIPHER:
                    if (crypto.getCipher() != null) {
                        out.println(crypto.getCipher());
                        out.close();
                        return true;
                    }
                    return false;
                default:
                    System.out.println("Error occoured during file save");
                    return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves the current @crypto object to a file.
     * @param file The file where the Crypto object should be saved.
     * @return Boolean, true if everything worked fine, false if an exception occurred.
     */
    public boolean saveAsCryptoFile(File file) {
        try {
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(crypto);
            oos.flush();
            oos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Encrypts the @plainText value with the @password value locally using the CryptoTool class.
     * @return Boolean, true if everything worked fine, false if an exception occurred.
     */
    public boolean localEncrypt() {
        try {
            CryptoTool cryptoTool = new CryptoTool();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // encrypt the plain text with the password and save the encrypted text in the cipher variable
            boolean successfulEncrypt = cryptoTool.encrypt(out, crypto.getPlainText().getBytes(), crypto.getPassword());
            if (successfulEncrypt) {
                String s = Base64.getEncoder().encodeToString(out.toByteArray());
                crypto.setCipher(s);
            } else {
                crypto.setCipher(null);
            }
            this.fireUpdate();
            return successfulEncrypt;
        } catch (Exception e) {
            e.printStackTrace();
            crypto.setCipher(null);
            this.fireUpdate();
            return false;
        }
    }

    /**
     * Encrypts the @plainText value with the @password value using an external Java program (.jar).
     * @param cmd The name of the external Java program.
     * @param dir The directory where the program is located.
     * @throws IOException
     * @throws InterruptedException
     */
    public void externalEncrypt(String cmd, String dir) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("java", "-jar", dir + cmd, "0", crypto.getPlainText(),crypto.getPassword());
        Process process = builder.start();
        process.waitFor();
        System.out.println("External encrypt exit value: " + process.exitValue());
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

    /**
     * Encrypts the @plainText value with the @password value using a socket. For this method to work the socket server
     * needs to be running.
     * @param hostName The address of the socket server.
     * @param port The port, where the socket is running on.
     * @throws SocketException
     */
    public void socketEncrypt(String hostName, int port) throws SocketException {
        try {
            CryptoSocketClient client = new CryptoSocketClient();
            client.contactServer(hostName, port);
            String cipher = client.encrypt(crypto.getPlainText(), crypto.getPassword());
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

    /**
     * Encrypts the @plainText value with the @password value using a rmi. For this method to work the rmi server
     * needs to be running.
     * @param hostName The address of the rmi server.
     * @param port The port, where the rmi is running on.
     * @throws RemoteException
     */
    public void rmiEncrypt(String hostName, int port) throws RemoteException {
        try {
            CryptoRmiClient client = new CryptoRmiClient(hostName, port);
            System.out.println(crypto.getCipher());
            crypto = client.encrypt(crypto);
            System.out.println(crypto.getCipher());
            this.fireUpdate();
        } catch (Exception e) {
            crypto.setCipher(null);
            this.fireUpdate();
            throw new RemoteException();
        }
    }

    /**
     * Decrypts the @cipher value with the @password value locally using the CryptoTool class.
     * @return Boolean, true if everything worked fine, false if an exception occurred.
     */
    public boolean localDecrypt() {
        try {
            CryptoTool cryptoTool = new CryptoTool();
            // decrypt the cipher with the given password and save the plain text in the plainText variable
            byte[] bytes = Base64.getDecoder().decode(crypto.getCipher());
            InputStream is = new ByteArrayInputStream(bytes);
            byte[] plainText = cryptoTool.decrypt(is, crypto.getPassword());
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

    /**
     * Decrypts the @cipher value with the @password value using an external Java program (.jar).
     * @param cmd The name of the external Java program.
     * @param dir The directory where the program is located.
     * @throws IOException
     * @throws InterruptedException
     */
    public void externalDecrypt(String cmd, String dir) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("java", "-jar", dir + cmd, "1", crypto.getCipher(),crypto.getPassword());
        Process process = builder.start();
        process.waitFor();
        System.out.println("External decrypt exit value: " + process.exitValue());
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

    /**
     * Decrypts the @cipher value with the @password value using a socket. For this method to work the socket server
     * needs to be running.
     * @param hostName The address of the socket server.
     * @param port The port, where the socket is running on.
     * @throws SocketException
     * @throws InvalidKeyException
     */
    public void socketDecrypt(String hostName, int port) throws SocketException, InvalidKeyException {
            CryptoSocketClient client = new CryptoSocketClient();
            client.contactServer(hostName, port);
            String plainText = client.decrypt(crypto.getCipher(), crypto.getPassword());
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

    /**
     * Decrypts the @cipher value with the @password value using a rmi. For this method to work the rmi server
     * needs to be running.
     * @param hostName The address of the rmi server.
     * @param port The port, where the rmi is running on.
     * @throws RemoteException
     */
    public void rmiDecrypt(String hostName, int port) throws RemoteException, InvalidKeyException {
        try {
            System.out.println("rmiDecrypt start");
            CryptoRmiClient client = new CryptoRmiClient(hostName, port);
            crypto = client.decrypt(crypto);
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
