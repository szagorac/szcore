package com.xenaksys.szcore.gui.view;


import com.xenaksys.szcore.gui.SzcoreClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class LoggerController {
    private final static String NL = "\n";

    private SzcoreClient mainApp;

    @FXML
    TextArea logTxtArea;

    public void setMainApp(SzcoreClient mainApp) {
        this.mainApp = mainApp;
    }

    public TextArea getLogTxtArea() {
        return logTxtArea;
    }

    public void writeLine(String txt){
        Platform.runLater(()-> {
            logTxtArea.appendText(txt);
            logTxtArea.appendText(NL);
        });
    }
}

