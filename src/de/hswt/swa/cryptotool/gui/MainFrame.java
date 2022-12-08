package de.hswt.swa.cryptotool.gui;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * This Class..
 *
 * @author Adrian Wild
 * @version 1.0
 */
public class MainFrame extends Application {

    private MainController controller;
    private Stage mainStage;

    private ListView statusLines;


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

        // a file menu in the menu bar
        Menu fileMenu = new Menu("File");

        // first item: import a file
        MenuItem importItem = new MenuItem("Import");
        fileMenu.getItems().add(importItem);
        // register the controller as listener
        importItem.setOnAction(controller.getEventHandler(MainController.EventType.IMPORT));


        // add the file menu to the menubar
        menu.getMenus().addAll(fileMenu);

        // add the menu bar to the window
        root.setTop(menu);

        Button button1 = new Button("Reset Fields");

        TextArea input = new TextArea("Lorem ipsum dolor sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,    sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,    sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,    sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,    sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,    sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,    sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,    sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,    sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,    sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,    sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,    sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,    sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoldolor sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoldolor sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoldolor sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoldolor sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoldolor sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  sitdolor sit amet.");
        input.setEditable(false);
        input.setWrapText(true);

        TextArea output = new TextArea("PETERorem ipsuor sit amet,  orem ipsutdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit ar sit amet,  orem ipsutdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit ar sit amet,  orem ipsutdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit ar sit amet,  orem ipsutdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit ar sit amet,  orem ipsutdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit ar sit amet,  orem ipsutdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit ar sit amet,  orem ipsutdoloror sit amet,  sitdoloror sit amet,  sitdoloror sit amet,  ");
        output.setEditable(false);
        output.setWrapText(true);

        Label inputLabel = new Label("Input");
        inputLabel.setFont(new Font(20));

        Label outputLabel = new Label("Output");
        outputLabel.setFont(new Font(20));

        VBox topBox  = new VBox(inputLabel, input);
        VBox bottomBox = new VBox(outputLabel, output);
        VBox centerPane = new VBox();
        centerPane.setSpacing(10);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.getChildren().add(topBox);
        centerPane.getChildren().add(bottomBox);
        input.prefWidthProperty().bind(centerPane.widthProperty().multiply(0.6));
        output.prefWidthProperty().bind(centerPane.widthProperty().multiply(0.6));


        root.setRight(button1);
        root.setCenter(centerPane);


        // at the bottom add a status display
        ScrollPane statusPane = new ScrollPane();
        statusPane.setFitToHeight(true);
        statusPane.setFitToWidth(true);
        statusPane.setPrefHeight(20);
        statusLines = new ListView<>();
        statusLines.getItems().addListener((ListChangeListener<String>) c->{
            statusLines.scrollTo(statusLines.getItems().size()-1);
        });
        statusPane.setContent(statusLines);

        root.setBottom(statusPane);
        stage.setScene(scene);
        stage.show();
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


    public void addStatus(String s) {
    }
}
