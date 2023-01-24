package de.hswt.swa.cryptotool.gui;

import de.hswt.swa.cryptotool.data.Crypto;
import de.hswt.swa.cryptotool.data.CryptoModelObserver;
import de.hswt.swa.cryptotool.utils.EventType;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

/**
 * This Class is the main application for the crypto tool. It creates the whole gui.
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

    /**
     * This method gets called when the main method is being executed.<br>
     * It creates a window with a menubar at the top and 2 text fields in the center. At the bottom is a status field
     * and on the right side a button to reset all fields.
     * @param stage Stage object.
     * @throws Exception Error occurred.
     */
    public void start(Stage stage) throws Exception {
        controller = new MainController(this);

        stage.setTitle("HSWT Crypto Tool");
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
        // third item: locally encrypt the plain text
        MenuItem localEncryptItem = new MenuItem("Local encrypt");
        localEncryptItem.setOnAction(controller.getEventHandler(EventType.LOCAL_ENCRYPT));
        // fourth item: locally encrypt the plain text
        MenuItem externalEncryptItem = new MenuItem("External encrypt");
        externalEncryptItem.setOnAction(controller.getEventHandler(EventType.EXTERNAL_ENCRYPT));
        // fifth item: encrypt the plain text via socket connection
        MenuItem socketEncryptItem = new MenuItem("Socket encrypt");
        socketEncryptItem.setOnAction(controller.getEventHandler(EventType.SOCKET_ENCRYPT));
        // sixth item: encrypt the plain text via rmi connection
        MenuItem rmiEncryptItem = new MenuItem("RMI encrypt");
        rmiEncryptItem.setOnAction(controller.getEventHandler(EventType.RMI_ENCRYPT));
        // seventh item: encrypt the plain text via rest api
        MenuItem apiEncryptItem = new MenuItem("API encrypt");
        apiEncryptItem.setOnAction(controller.getEventHandler(EventType.API_ENCRYPT));

        textMenu.getItems().add(importTextItem);
        textMenu.getItems().add(saveTextItem);
        textMenu.getItems().add(localEncryptItem);
        textMenu.getItems().add(externalEncryptItem);
        textMenu.getItems().add(socketEncryptItem);
        textMenu.getItems().add(rmiEncryptItem);
        textMenu.getItems().add(apiEncryptItem);


        // a cipher menu in the menu bar
        Menu cipherMenu = new Menu("Cipher");
        // first item: import a text file containing plain text
        MenuItem importCipherItem = new MenuItem("Import");
        importCipherItem.setOnAction(controller.getEventHandler(EventType.IMPORT_CIPHER));
        // second item: save the cipher to the file system
        MenuItem saveCipherItem = new MenuItem("Save");
        saveCipherItem.setOnAction(controller.getEventHandler(EventType.SAVE_CIPHER));
        // third item: locally decrypt the cipher
        MenuItem localDecryptItem = new MenuItem("Local decrypt");
        localDecryptItem.setOnAction(controller.getEventHandler(EventType.LOCAL_DECRYPT));
        // fourth item: externally decrypt the cipher
        MenuItem externalDecryptItem = new MenuItem("External decrypt");
        externalDecryptItem.setOnAction(controller.getEventHandler(EventType.EXTERNAL_DECRYPT));
        // fifth item: decrypt the cipher via socket connection
        MenuItem socketDecryptItem = new MenuItem("Socket decrypt");
        socketDecryptItem.setOnAction(controller.getEventHandler(EventType.SOCKET_DECRYPT));
        // sixth item: decrypt the cipher via rmi connection
        MenuItem rmiDecryptItem = new MenuItem("RMI decrypt");
        rmiDecryptItem.setOnAction(controller.getEventHandler(EventType.RMI_DECRYPT));
        // seventh item: decrypt the cipher via rest api
        MenuItem apiDecryptItem = new MenuItem("API decrypt");
        apiDecryptItem.setOnAction(controller.getEventHandler(EventType.API_DECRYPT));

        cipherMenu.getItems().add(importCipherItem);
        cipherMenu.getItems().add(saveCipherItem);
        cipherMenu.getItems().add(localDecryptItem);
        cipherMenu.getItems().add(externalDecryptItem);
        cipherMenu.getItems().add(socketDecryptItem);
        cipherMenu.getItems().add(rmiDecryptItem);
        cipherMenu.getItems().add(apiDecryptItem);


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


        // add the menus to the menubar
        menu.getMenus().addAll(textMenu);
        menu.getMenus().addAll(cipherMenu);
        menu.getMenus().addAll(cryptoMenu);

        // add the menu bar to the window
        root.setTop(menu);

        // create button to reset text fields
        Button resetFieldsButton = new Button("Reset Fields");
        resetFieldsButton.setOnAction(controller.getEventHandler(EventType.RESET_FIELDS));

        // create button to display encrytion date
        Button showEncryptionDate = new Button("Encryption Date");
        showEncryptionDate.setOnAction(controller.getEventHandler(EventType.DISPLAY_DATE));

        VBox rightTopBox = new VBox();
        rightTopBox.getChildren().addAll(resetFieldsButton, showEncryptionDate);
        rightTopBox.setSpacing(10);

        // create copyright text
        VBox rightBottomBox = new VBox();
        Text copyright = new Text("©Copyright");
        Text author = new Text("Adrian Wild");
        Text date = new Text("2023");

        // set reset button and copyright to the right side of the view
        rightBottomBox.getChildren().addAll(copyright, author, date);
        BorderPane rightPane = new BorderPane();
        rightPane.setTop(rightTopBox);
        rightPane.setBottom(rightBottomBox);
        rightPane.setPadding(new Insets(10, 10, 10, 10));

        // create the two text fields
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
        mainBox.setPadding(new Insets(0, 0, 0, 50));

        root.setRight(rightPane);
        root.setCenter(mainBox);

        // at the bottom add a status display
        ScrollPane statusPane = new ScrollPane();
        statusPane.setFitToHeight(true);
        statusPane.setFitToWidth(true);
        statusPane.setPrefHeight(20);
        statusLines = new ListView<>();
        statusLines.getItems().addListener((ListChangeListener<String>)c->{
            statusLines.scrollTo(statusLines.getItems().size()-1);
            statusPane.setContent(statusLines);
        });
        statusPane.setContent(statusLines);
        root.setBottom(statusPane);

        stage.setScene(scene);

        // set image from file system as application icon
        String imagePath = Paths.get("assets/Favicon.png")
                .toAbsolutePath()
                .toString();
        stage.getIcons().add(new Image("file:" + imagePath));
        stage.show();

        controller.registerCryptoModelObserver(this);
    }

    /**
     * Opens a file chooser window where the user can select a file.
     * @param title The title of the window.
     * @param ext The user can only select files with this extension.
     * @param dir The directory where the file chooser opens.
     * @param open Boolean. If true file chooser is used to select file. If false file chooser is used to save file.
     * @return File object.
     */
    public File openFileChooser(String title, FileChooser.ExtensionFilter ext, File dir, boolean open) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(dir);
        fileChooser.getExtensionFilters().add(ext);
        if (open) {
            return fileChooser.showOpenDialog(mainStage);
        } else {
            return fileChooser.showSaveDialog(mainStage);
        }
    }

    public void addStatus(String msg) {
        statusLines.getItems().add(msg);
    }

    /**
     * This function opens a javafx alert field that displays a warning to the user.
     *
     * @param msg The message that should be displayed to the user.
     */
    public void openAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Something went wrong");
        alert.setContentText(msg);
        alert.show();
    }

    /**
     * This function opens a javafx alert field that displays the encryption date to the user.
     * If a text has not been encrypted yet, then a generic message will be displayed instead.
     *
     * @param date The message that should be displayed to the user.
     */
    public void openDateDialog(Date date) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Encryption Date");
        alert.setHeaderText(null);
        if (date != null) {
            alert.setContentText("Encryption date: " + date.toString());
        } else {
            alert.setContentText("Text has not been encrypted yet");
        }
        alert.show();
    }

    /**
     * This function opens a javafx dialog field where the user has to enter a password and press enter.
     *
     * @param mode Decides the title of the dialog. Should be 1, if the dialog is used for entering the password to decrypt a cipher.
     * @return The password as Optional type.
     */
    public Optional<String> openPasswordDialog(Integer mode) {
        String title = "Password field";
        String msg;
        if (mode == 1) {
            msg = "Type in password to decrypt cipher.";
        }
        else {
            msg = "Set password to encrypt text." + System.getProperty("line.separator") + "Umlauts aren't allowed.";
        }
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(msg);
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

        // disable okButton, if password field is empty or longer than 16
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


    /**
     * This method updates the 2 text areas in the main frame.
     * @param crypto Crypto object.
     */
    public void update(Crypto crypto) {
        System.out.println("MainFrame update");
        this.plainTextArea.setText(crypto.getPlainText());
        this.cipherTextArea.setText(crypto.getCipher());
    }
}
