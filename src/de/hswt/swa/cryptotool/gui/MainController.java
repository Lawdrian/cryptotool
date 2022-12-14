package de.hswt.swa.cryptotool.gui;

import de.hswt.swa.cryptotool.data.CryptoModelObserver;
import de.hswt.swa.cryptotool.logic.BusinessLogic;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;

import static de.hswt.swa.cryptotool.gui.MainController.EventType.*;

public class MainController {


    private MainFrame view;


    private BusinessLogic logic = new BusinessLogic();

    public MainController(MainFrame mainFrame) {
        view = mainFrame;
    }


    public enum EventType {
        IMPORT_TEXT,
        SAVE_TEXT,
        LOCAL_ENCODE,
        IMPORT_CIPHER,
        SAVE_CIPHER,
        LOCAL_DECODE,
        IMPORT_CRYPTO,
        SAVE_CRYPTO,
        RESET_FIELDS
    }

    public EventHandler<ActionEvent> getEventHandler(EventType eventType) {
        switch (eventType) {
            case IMPORT_TEXT, IMPORT_CIPHER, IMPORT_CRYPTO: return new ImportFileHandler(eventType);
            case RESET_FIELDS:return new ResetFieldsHandler();
            case SAVE_TEXT, SAVE_CIPHER, SAVE_CRYPTO: return new SaveHandler(eventType);
            case LOCAL_ENCODE: return new EncodeHandler(eventType);
            case LOCAL_DECODE: return new DecodeHandler(eventType);
            default: return null;
        }

    }


    class ImportFileHandler implements EventHandler<ActionEvent> {
        private EventType eventType;
        ImportFileHandler(EventType eventType) {
            this.eventType = eventType;
        }

        /*
        public Optional<String> getFileExtension(String filename) {
            return Optional.ofNullable(filename)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(filename.lastIndexOf(".") + 1));
        }
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
                        logic.readTextFile(file, eventType);
                        break;
                    case IMPORT_CRYPTO:
                        logic.readCryptoObject(file);
                        break;
                }
                view.addStatus("File " + file.getAbsolutePath() + " imported sucessfully");
            }
            else {
                view.addStatus("Error file import");
            }

        }

    }


    class SaveHandler implements EventHandler<ActionEvent> {
        private EventType eventType;

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
            File file = view.openFileChooser("Save Sequences", new FileChooser.ExtensionFilter(extension, extension), new File("."), false);
            if (file != null) {
                switch (eventType) {
                    case SAVE_TEXT:
                    case SAVE_CIPHER:
                        logic.saveAsTextFile(file, eventType);
                        break;
                    case SAVE_CRYPTO:
                        //logic.saveAsCryptoFile()
                 }
            } else {
                view.addStatus("Error in file name in save dialog");
            }
        }

    }




    class EncodeHandler implements EventHandler<ActionEvent> {

        private EventType eventType;

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
                // Create dialog component where the user can type in a password and start the encoding.
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Set password to encode text");
                ButtonType okButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

                PasswordField password = new PasswordField();
                password.setPromptText("Password");

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.add(new Label("Password:"), 0, 0);
                grid.add(password, 1, 0);

                dialog.getDialogPane().setContent(grid);

                Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
                okButton.setDisable(true);

                // Disable okButton, if password field is empty
                password.textProperty().addListener((observable, oldValue, newValue) -> {
                    okButton.setDisable(newValue.trim().isEmpty());
                });
                System.out.println("Aloa");
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == okButtonType) {
                        return password.getText();
                    } else {
                        return null;
                    }
                });
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(pw -> {
                    logic.setPassword(pw);
                    if (logic.isPasswordSet()) {
                        System.out.println("Password has been set");
                        switch (eventType) {
                            case LOCAL_ENCODE:
                                if (logic.localEncode()) {
                                    view.addStatus("Text has been successfully encoded!");
                                }
                                else {
                                    view.addStatus("Text couldn't be encoded!");
                                }
                                break;
                            default:
                                view.addStatus("This encoding has not been implemented yet!");
                                break;
                        }
                    }
                });
            }
        }
    }

    class DecodeHandler implements EventHandler<ActionEvent> {

        private EventType eventType;

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

                Optional<String> result = view.openPasswordDialog();
                result.ifPresent(pw -> {
                    switch (eventType) {
                        case LOCAL_DECODE:
                            if (logic.localDecode(pw)) {
                                view.addStatus("Text has been successfully decoded!");
                            }
                            else {
                                view.addStatus("Wrong password!");
                            }
                            break;
                        default:
                            view.addStatus("This encoding has not been implemented yet!");
                            break;
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


