package de.hswt.swa.cryptotool.gui;

import de.hswt.swa.cryptotool.data.CryptoModelObserver;
import de.hswt.swa.cryptotool.data.EventType;
import de.hswt.swa.cryptotool.logic.BusinessLogic;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.util.Optional;

import static de.hswt.swa.cryptotool.data.EventType.*;

/**
 * @author AdrianWild
 * @version 1.0
 */
public class MainController {

    private final MainFrame view;
    private final BusinessLogic logic = new BusinessLogic();

    public MainController(MainFrame mainFrame) {
        view = mainFrame;
    }

    /**
     * This method dispatches different methods depending on what @eventType it receives.
     * @param eventType EventType object.
     * @return EventHandler object.
     */
    public EventHandler<ActionEvent> getEventHandler(EventType eventType) {
        switch (eventType) {
            case IMPORT_TEXT:
            case IMPORT_CIPHER:
            case IMPORT_CRYPTO:
                return new ImportFileHandler(eventType);
            case RESET_FIELDS:
                return new ResetFieldsHandler();
            case SAVE_TEXT:
            case SAVE_CIPHER:
            case SAVE_CRYPTO:
                return new SaveHandler(eventType);
            case LOCAL_ENCODE:
            case EXTERNAL_ENCODE:
            case SOCKET_ENCODE:
            case RMI_ENCODE:
                return new EncryptHandler(eventType);
            case LOCAL_DECODE:
            case EXTERNAL_DECODE:
            case SOCKET_DECODE:
            case RMI_DECODE:
                return new DecryptHandler(eventType);
            default:
                return null;
        }

    }


    class ImportFileHandler implements EventHandler<ActionEvent> {
        private final EventType eventType;

        ImportFileHandler(EventType eventType) {
            this.eventType = eventType;
        }

        /**
         * Handles the file import.<br>
         * This method first lets the user select a file and then
         * dispatches a method depending on the @eventType variable value.
         * @param event ActionEvent object.
         */
        public void handle(ActionEvent event) {
            String extension;
            if (eventType==EventType.IMPORT_CRYPTO) {
                extension = "*.crypto";
            }
            else {
                extension = "*.txt";
            }
            // open a file chooser
            File file = view.openFileChooser("Open File", new FileChooser.ExtensionFilter(extension, extension), new File("."), true);
            // call application logic to open the respective file
            if (file != null) {
                switch (eventType) {
                    case IMPORT_TEXT:
                    case IMPORT_CIPHER:
                        if (logic.readTextFile(file, eventType)) {
                            view.addStatus("File " + file.getName() + " imported successfully.");
                        } else {
                            view.addStatus("File " + file.getName() + " couldn't be imported.");
                        }
                        break;
                    case IMPORT_CRYPTO:
                        if (logic.readCryptoFile(file)) {
                            view.addStatus("File " + file.getName() + " imported successfully.");
                        } else {
                            view.addStatus("File " + file.getName() + " couldn't be imported.");
                        }
                        break;
                }
            }
            else {
                view.addStatus("Error occurred during file import.");
            }
        }
    }


    class SaveHandler implements EventHandler<ActionEvent> {
        private final EventType eventType;

        public SaveHandler(EventType eventType) {
            this.eventType = eventType;
        }

        /**
         * Handles saving of a file.<br>
         * This method first lets the user select a new file and then
         * dispatches a method depending on the @eventType variable value.
         * @param event ActionEvent object.
         */
        public void handle(ActionEvent event) {
            String extension;
            if (eventType== SAVE_CRYPTO) {
                extension = "*.crypto";
            }
            else {
                extension = "*.txt";
            }
            // open a file chooser
            File file = view.openFileChooser("Save file", new FileChooser.ExtensionFilter(extension, extension), new File("."), false);
            if (file != null) {
                switch (eventType) {
                    case SAVE_TEXT:
                    case SAVE_CIPHER:
                        if (logic.saveAsTextFile(file, eventType)) {
                            view.addStatus("Save was successful.");
                        }
                        else {
                            view.addStatus("Field could not be saved");
                            view.openAlert("An empty field cannot be saved.");
                        }
                        break;
                    case SAVE_CRYPTO:
                        if (logic.saveAsCryptoFile(file)) {
                            view.addStatus("Saving the crypto object was successful.");
                        }
                 }
            } else {
                view.addStatus("Error occurred during file save.");
                view.openAlert("Error occurred during file save.");
            }
        }

    }


    class EncryptHandler implements EventHandler<ActionEvent> {

        private final EventType eventType;

        public EncryptHandler(EventType eventType) {
            this.eventType = eventType;
        }

        /**
         * Handles encoding of the plain text.<br>
         * If the plain text is set, the method opens a password dialog where the user has to type in a password.
         * This password will be used to encrypt the plain text. After the user typed in a password, a method will be
         * called depending on the @eventType variable value. This method catches any error that might occur during the
         * encoding process and displays a case specific error message.
         *
         * @param event ActionEvent object.
         */
        public void handle(ActionEvent event) {
            System.out.println(!logic.isPlainTextSet());
            // Check if plain text field is set
            if (!logic.isPlainTextSet()) {
                // Make the user import plain text before encoding
                ImportFileHandler fileImporter = new ImportFileHandler(IMPORT_TEXT);
                fileImporter.handle(event);
            }
            if (logic.isPlainTextSet()) {
                Optional<String> result = view.openPasswordDialog(0);
                result.ifPresent(password -> {
                    logic.setPassword(password);
                    if (logic.isPasswordSet()) {
                        System.out.println("Password has been set.");
                        switch (eventType) {
                            case LOCAL_ENCODE:
                                if (logic.localEncrypt()) {
                                    view.addStatus("Text has been successfully encrypted locally.");
                                } else {
                                    view.addStatus("Text couldn't be encrypted locally.");
                                    view.openAlert("Text couldn't be encrypted locally.");
                                }
                                break;
                            case EXTERNAL_ENCODE:
                                try {
                                    logic.externalEncrypt();
                                    view.addStatus("Text has been successfully encrypted locally.");
                                } catch (IOException e){
                                    e.printStackTrace();
                                    view.addStatus("Text couldn't be encrypted externally.");
                                    view.openAlert("Text couldn't be encrypted externally.");
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    view.addStatus("Problem with external program.");
                                    view.openAlert("Problem with external program.");
                                }
                                break;
                            case SOCKET_ENCODE:
                                try {
                                    logic.socketEncrypt();
                                    view.addStatus("Text has been successfully encrypted with socket.");
                                } catch (SocketException e) {
                                    e.printStackTrace();
                                    view.addStatus("Text couldn't be encrypted with socket.");
                                    view.openAlert("Text couldn't be encrypted with socket.");
                                }
                                break;
                            case RMI_ENCODE:
                                try {
                                    logic.rmiEncrypt();
                                    view.addStatus("Text has been successfully encrypted with rmi.");
                                } catch (RemoteException e) {
                                    view.addStatus("Text couldn't be encrypted with rmi.");
                                    view.openAlert("Text couldn't be encrypted with rmi.");
                                }
                                break;
                            default:
                                view.addStatus("This encoding has not been implemented yet.");
                                break;
                        }
                    }
                });
            }
        }
    }


    class DecryptHandler implements EventHandler<ActionEvent> {

        private final EventType eventType;

        public DecryptHandler(EventType eventType) {
            this.eventType = eventType;
        }

        /**
         * Handles decoding of the cipher.<br>
         * If the cipher is set, the method opens a password dialog where the user has to type in a password.
         * This password will be used to decrypt the cipher. After the user typed in a password, a method will be called
         * depending on the @eventType variable value. This method catches any error that might occur during the
         * decoding process and displays a case specific error message.
         *
         * @param event ActionEvent object.
         */
        public void handle(ActionEvent event) {
            System.out.println("DecryptHandler");
            // Check if cipher field is set
            if (!logic.isCipherSet()) {
                // Make the user import plain text before encoding
                ImportFileHandler fileImporter = new ImportFileHandler(IMPORT_CIPHER);
                fileImporter.handle(event);
            }
            if (logic.isCipherSet()) {
                // Create dialog component where the user can type in a password and start the encoding.
                Optional<String> result = view.openPasswordDialog(1);
                result.ifPresent(password -> {
                    logic.setPassword(password);
                    if (logic.isPasswordSet()) {
                        System.out.println("Password has been set.");
                        switch (eventType) {
                            case LOCAL_DECODE:
                                if (logic.localDecrypt()) {
                                    view.addStatus("Text has been successfully decrypted locally.");
                                } else {
                                    view.addStatus("Wrong password.");
                                    view.openAlert("Wrong password.");
                                }
                                break;
                            case EXTERNAL_DECODE:
                                try {
                                    logic.externalDecrypt();
                                    view.addStatus("Text has been successfully decrypted externally.");
                                } catch (InterruptedException e){
                                    e.printStackTrace();
                                    view.addStatus("Error occurred during program execution.");
                                    view.openAlert("Error occurred during program execution.");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    view.addStatus("Wrong password.");
                                    view.openAlert("Wrong password.");
                                }
                                break;
                            case SOCKET_DECODE:
                                try {
                                    logic.socketDecrypt();
                                    view.addStatus("Text has been successfully decrypted with socket.");
                                } catch (SocketException e){
                                    e.printStackTrace();
                                    view.addStatus("Connection with server failed.");
                                    view.openAlert("Connection with server failed.");
                                } catch (InvalidKeyException e) {
                                    e.printStackTrace();
                                    view.addStatus("Wrong password.");
                                    view.openAlert("Wrong password.");
                                }
                                break;
                            case RMI_DECODE:
                                try {
                                    logic.rmiDecrypt();
                                    view.addStatus("Text has been successfully decrypted with rmi.");
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                    view.addStatus("Connection with server failed.");
                                    view.openAlert("Connection with server failed.");
                                } catch (InvalidKeyException e ) {
                                    e.printStackTrace();
                                    view.addStatus("Wrong password.");
                                    view.openAlert("Wrong password.");
                                }
                                break;
                            default:
                                view.addStatus("This encoding has not been implemented yet.");
                                break;
                        }
                    }
                });
            }
        }
    }


    class ResetFieldsHandler implements EventHandler<ActionEvent> {

        public void handle(ActionEvent event) {logic.resetCryptoObject();}
    }

    public void registerCryptoModelObserver(CryptoModelObserver cryptoView) {
        logic.registerCryptoModelObserver(cryptoView);
    }
}
