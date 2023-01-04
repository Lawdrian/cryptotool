package de.hswt.swa.cryptotool.gui;

import de.hswt.swa.cryptotool.data.Crypto;
import de.hswt.swa.cryptotool.data.CryptoModelObserver;
import de.hswt.swa.cryptotool.data.EventType;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

/**
 * This Class..
 *
 * @author Adrian Wild
 * @version 1.0
 */
public class MainFrame extends Application implements CryptoModelObserver {

    private MainController controller;
    private Stage mainStage;

    private ListView statusLines;

    private TextArea plainTextArea;
    private TextArea cipherTextArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        controller = new MainController(this);

        stage.setTitle("HSWT Crypotool");
        mainStage = stage;

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root,800, 600);

        // generate a menu bar
        MenuBar menu = new MenuBar();

        // a text menu in the menu bar
        Menu textMenu = new Menu("Plain Text");
        // first item: import a text file containing plain text
        MenuItem importTextItem = new MenuItem("Import");
        importTextItem.setOnAction(controller.getEventHandler(EventType.IMPORT_TEXT));
        // second item: save the plain text to the file system
        MenuItem saveTextItem = new MenuItem("Save");
        saveTextItem.setOnAction(controller.getEventHandler(EventType.SAVE_TEXT));
        // third item: locally encode the plain text
        MenuItem localEncodeItem = new MenuItem("Local encode");
        localEncodeItem.setOnAction(controller.getEventHandler(EventType.LOCAL_ENCODE));
        // fourth item: locally encode the plain text
        MenuItem externalEncodeItem = new MenuItem("External encode");
        externalEncodeItem.setOnAction(controller.getEventHandler(EventType.EXTERNAL_ENCODE));
        // fifth item: encode the plain text via socket connection
        MenuItem socketEncodeItem = new MenuItem("Socket encode");
        socketEncodeItem.setOnAction(controller.getEventHandler(EventType.SOCKET_ENCODE));
        // sixth item: encode the plain text via rmi connection
        MenuItem rmiEncodeItem = new MenuItem("Rmi encode");
        rmiEncodeItem.setOnAction(controller.getEventHandler(EventType.RMI_ENCODE));

        textMenu.getItems().add(importTextItem);
        textMenu.getItems().add(saveTextItem);
        textMenu.getItems().add(localEncodeItem);
        textMenu.getItems().add(externalEncodeItem);
        textMenu.getItems().add(socketEncodeItem);
        textMenu.getItems().add(rmiEncodeItem);


        // a cipher menu in the menu bar
        Menu cipherMenu = new Menu("Cipher");
        // first item: import a text file containing plain text
        MenuItem importCipherItem = new MenuItem("Import");
        importCipherItem.setOnAction(controller.getEventHandler(EventType.IMPORT_CIPHER));
        // second item: save the cipher to the file system
        MenuItem saveCipherItem = new MenuItem("Save");
        saveCipherItem.setOnAction(controller.getEventHandler(EventType.SAVE_CIPHER));
        // third item: locally decode the cipher
        MenuItem localDecodeItem = new MenuItem("Local decode");
        localDecodeItem.setOnAction(controller.getEventHandler(EventType.LOCAL_DECODE));
        // fourth item: externally decode the cipher
        MenuItem externalDecodeItem = new MenuItem("External decode");
        externalDecodeItem.setOnAction(controller.getEventHandler(EventType.EXTERNAL_DECODE));
        // fifth item: decode the cipher via socket connection
        MenuItem socketDecodeItem = new MenuItem("Socket decode");
        socketDecodeItem.setOnAction(controller.getEventHandler(EventType.SOCKET_DECODE));
        // sixth item: decode the cipher via rmi connection
        MenuItem rmiDecodeItem = new MenuItem("Rmi decode");
        rmiDecodeItem.setOnAction(controller.getEventHandler(EventType.RMI_DECODE));

        cipherMenu.getItems().add(importCipherItem);
        cipherMenu.getItems().add(saveCipherItem);
        cipherMenu.getItems().add(localDecodeItem);
        cipherMenu.getItems().add(externalDecodeItem);
        cipherMenu.getItems().add(socketDecodeItem);
        cipherMenu.getItems().add(rmiDecodeItem);


        // a crypto object menu in the menu bar
        Menu cryptoMenu = new Menu("Crypto Object");
        // first item: import a crypto object and display its content
        MenuItem importCryptoItem = new MenuItem("Import");
        importCryptoItem.setOnAction(controller.getEventHandler(EventType.IMPORT_CRYPTO));
        // second item: save a crypto object to the file system
        MenuItem saveCryptoItem = new MenuItem("Save");
        saveCryptoItem.setOnAction(controller.getEventHandler(EventType.SAVE_CRYPTO));

        cryptoMenu.getItems().add(importCryptoItem);
        cryptoMenu.getItems().add(saveCryptoItem);


        // add the file menus to the menubar
        menu.getMenus().addAll(textMenu);
        menu.getMenus().addAll(cipherMenu);
        menu.getMenus().addAll(cryptoMenu);

        // add the menu bar to the window
        root.setTop(menu);

        Button resetFieldsButton = new Button("Reset Fields");
        resetFieldsButton.setOnAction(controller.getEventHandler(EventType.RESET_FIELDS));
        plainTextArea = new TextArea();
        plainTextArea.setEditable(false);
        plainTextArea.setWrapText(true);

        cipherTextArea = new TextArea();
        cipherTextArea.setEditable(false);
        cipherTextArea.setWrapText(true);

        Label inputLabel = new Label("Plain Text");
        inputLabel.setFont(new Font(20));

        Label outputLabel = new Label("Cipher");
        outputLabel.setFont(new Font(20));

        VBox topBox  = new VBox(inputLabel, plainTextArea);
        VBox bottomBox = new VBox(outputLabel, cipherTextArea);
        VBox mainBox = new VBox();
        mainBox.setSpacing(10);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.getChildren().add(topBox);
        mainBox.getChildren().add(bottomBox);
        plainTextArea.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.6));
        cipherTextArea.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.6));


        root.setRight(resetFieldsButton);
        root.setCenter(mainBox);

        // at the bottom add a status display
        ScrollPane statusPane = new ScrollPane();
        statusPane.setFitToHeight(true);
        statusPane.setFitToWidth(true);
        statusPane.setPrefHeight(20);
        statusLines = new ListView<>();
        statusLines.getItems().addListener((ListChangeListener<String>)c->{
            //System.out.println(statusLines.getItems());
            //System.out.println(statusLines.getItems().size());
            statusLines.scrollTo(statusLines.getItems().size()+1);
            statusPane.setContent(statusLines);
        });
        statusPane.setContent(statusLines);

        root.setBottom(statusPane);
        stage.setScene(scene);
        stage.show();

        controller.registerCryptoModelObserver(this);
    }


    public File openFileChooser(String title, FileChooser.ExtensionFilter ext, File dir, boolean open) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(dir);
        fileChooser.getExtensionFilters().add(ext);
        if (open) {
            File file = fileChooser.showOpenDialog(mainStage);
            return file;
        } else {
            File file = fileChooser.showSaveDialog(mainStage);
            return file;
        }
    }


    public void addStatus(String msg) {
        statusLines.getItems().add(msg);
    }


    /**
     * This function opens a javafx alert field that displays a warning to the user.
     *
     * @param msg   the message that should be displayed to the user.
     */
    public void openAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Something went wrong");
        alert.setContentText(msg);
        alert.show();
    }

    public void openPopup(String msg) {

    }

    /**
     * This function opens a javafx dialog field where the user has to enter a password and press enter.
     *
     * @param mode  decides the title of the dialog. Should be 1, if the dialog is used for entering the pwd to decode a cipher.
     * @return      the pwd as Option type.
     */
    public Optional<String> openPasswordDialog(Integer mode) {
        String msg;
        if (mode == 1) {
            msg = "Type in password to decode cipher";
        }
        else {
            msg = "Set password to encode text";
        }
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(msg);
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
            okButton.setDisable(newValue.length() > 16);
        });
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return password.getText();
            } else {
                return null;
            }
        });
        return dialog.showAndWait();
    }


    @Override
    public void update(Crypto crypto) {
        System.out.println("MainFrame update");
        this.plainTextArea.setText(crypto.getPlainText());
        this.cipherTextArea.setText(crypto.getCipher());
        System.out.println(this.plainTextArea);
    }
}
