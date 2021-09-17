package com.xenaksys.szcore.gui.view;


import com.xenaksys.szcore.gui.SzcoreClient;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;

public class RootLayoutController {

    private SzcoreClient mainApp;

    @FXML
    private Tab loggerTab;
    @FXML
    private Tab settingsTab;
    @FXML
    private Tab dialogsScoreTab;
    @FXML
    private Tab scoreTab;

    public void setMainApp(SzcoreClient mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SZCORE");
        alert.setHeaderText("About");
        alert.setContentText("Author: Xenaksys\nWebsite: http://www.xenaksys.com");

        alert.showAndWait();
    }

    public Tab getLoggerTab() {
        return loggerTab;
    }

    public Tab getSettingsTab() {
        return settingsTab;
    }

    public Tab getScoreTab() {
        return scoreTab;
    }

    public Tab getDialogsScoreTab() {
        return dialogsScoreTab;
    }

    @FXML
    private void handleClose() {
        System.exit(0);
    }
}

