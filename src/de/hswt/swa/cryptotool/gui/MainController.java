package de.hswt.swa.cryptotool.gui;

import de.hswt.swa.cryptotool.logic.BusinessLogic;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainController {


    private MainFrame view;


    private BusinessLogic logic = new BusinessLogic();

    public MainController(MainFrame mainFrame) {
        view = mainFrame;
    }


    public enum EventType {
        IMPORT,
        SAVE
    }

    public EventHandler<ActionEvent> getEventHandler(EventType eventType) {
        switch (eventType) {
            case IMPORT: return new ImportFileHandler();
            default: return null;
        }

    }


    class ImportFileHandler implements EventHandler<ActionEvent> {


        public Optional<String> getFileExtension(String filename) {
            return Optional.ofNullable(filename)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(filename.lastIndexOf(".") + 1));
        }

        public void handle(ActionEvent event) {
            List<String> extensions = new ArrayList<String>();
            extensions.add("*.fasta");
            extensions.add("*.txt");
            // open a file chooser
            File file = view.openFileChooser("Open Fasta File", new FileChooser.ExtensionFilter("Fasta files", extensions), new File("."), true);
            // call application logic to open the respective file
            if (file != null) {
                Optional extension = getFileExtension(file.getName());
                if (!extension.isEmpty()) {
                    String extensionName = extension.get().toString();
                    switch (extensionName) {
                        case "txt":
                            logic.readTextFile(file);
                        case "cypher":
                            logic.readCypherObject(file);
                        default:
                            view.addStatus("Unknown file format. Please provide a valid file");
                    }
                    view.addStatus("File " + file.getAbsolutePath() + " imported sucessfully");
                } else {
                    view.addStatus("Unknown file format. Please provide a valid file");
                }
            } else {
                view.addStatus("Error file import");
            }

        }

    }
}


