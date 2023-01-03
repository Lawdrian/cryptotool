package de.hswt.swa.cryptotool.gui;

import de.hswt.swa.cryptotool.data.CryptoModelObserver;
import de.hswt.swa.cryptotool.data.EventType;
import de.hswt.swa.cryptotool.logic.BusinessLogic;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.util.Optional;

import static de.hswt.swa.cryptotool.data.EventType.*;

public class MainController {


    private MainFrame view;


    private BusinessLogic logic = new BusinessLogic();

    public MainController(MainFrame mainFrame) {
        view = mainFrame;
    }


    public EventHandler<ActionEvent> getEventHandler(EventType eventType) {
        switch (eventType) {
            case IMPORT_TEXT, IMPORT_CIPHER, IMPORT_CRYPTO: return new ImportFileHandler(eventType);
            case RESET_FIELDS:return new ResetFieldsHandler();
            case SAVE_TEXT, SAVE_CIPHER, SAVE_CRYPTO: return new SaveHandler(eventType);
            case LOCAL_ENCODE, SOCKET_ENCODE, RMI_ENCODE: return new EncodeHandler(eventType);
            case LOCAL_DECODE, SOCKET_DECODE, RMI_DECODE: return new DecodeHandler(eventType);
            default: return null;
        }

    }


    class ImportFileHandler implements EventHandler<ActionEvent> {
        private final EventType eventType;
        ImportFileHandler(EventType eventType) {
            this.eventType = eventType;
        }

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
                        logic.readTextFile(file, eventType);
                        break;
                    case IMPORT_CRYPTO:
                        logic.readCryptoFile(file);
                        break;
                }
                view.addStatus("File " + file.getName() + " imported sucessfully.");
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

    class EncodeHandler implements EventHandler<ActionEvent> {

        private final EventType eventType;

        public EncodeHandler(EventType eventType) {
            this.eventType = eventType;
        }
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
                                if (logic.localEncode()) {
                                    view.addStatus("Text has been successfully encoded locally.");
                                } else {
                                    view.addStatus("Text couldn't be encoded locally.");
                                    view.openAlert("Text couldn't be encoded locally.");
                                }
                                break;
                            case SOCKET_ENCODE:
                                try {
                                    logic.socketEncode();
                                    view.addStatus("Text has been successfully encoded with socket.");
                                } catch (SocketException e) {
                                    e.printStackTrace();
                                    view.addStatus("Text couldn't be encoded with socket.");
                                    view.openAlert("Text couldn't be encoded with socket.");
                                }
                                break;
                            case RMI_ENCODE:
                                try {
                                    logic.rmiEncode();
                                    view.addStatus("Text has been successfully encoded with rmi.");
                                } catch (RemoteException e) {
                                    view.addStatus("Text couldn't be encoded with rmi.");
                                    view.openAlert("Text couldn't be encoded with rmi.");
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

    class DecodeHandler implements EventHandler<ActionEvent> {

        private final EventType eventType;

        public DecodeHandler(EventType eventType) {
            this.eventType = eventType;
        }
        public void handle(ActionEvent event) {
            System.out.println("DecodeHandler");
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
                                if (logic.localDecode()) {
                                    view.addStatus("Text has been successfully decoded locally.");
                                } else {
                                    view.addStatus("Wrong password.");
                                    view.openAlert("Wrong password.");
                                }
                                break;
                            case SOCKET_DECODE:
                                try {
                                    logic.socketDecode();
                                    view.addStatus("Text has been successfully decoded with socket.");
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
                                    logic.rmiDecode();
                                    view.addStatus("Text has been successfully decoded with rmi.");
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

        public void handle(ActionEvent event) {
            logic.resetCryptoObject();
        }
    }

    public void registerCryptoModelObserver(CryptoModelObserver cryptoView) {
        logic.registerCryptoModelObserver(cryptoView);
    }



}


