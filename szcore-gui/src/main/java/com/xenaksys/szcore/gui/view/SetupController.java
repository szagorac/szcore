package com.xenaksys.szcore.gui.view;


import com.xenaksys.szcore.gui.SzcoreClient;
import com.xenaksys.szcore.gui.model.Participant;
import com.xenaksys.szcore.model.EventService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public class SetupController {
    static final Logger LOG = LoggerFactory.getLogger(SetupController.class);

    private final static String NL = "\n";

    private SzcoreClient mainApp;
    private EventService publisher;
    private InetAddress localAddress;

    @FXML
    private TableView<Participant> participantsTable;
    @FXML
    private TableColumn<Participant, String> hostAddressColumn;
    @FXML
    private TableColumn<Participant, Integer> inPortColumn;
    @FXML
    private TableColumn<Participant, Integer> outPortColumn;
    @FXML
    private TableColumn<Participant, Integer> errPortColumn;
    @FXML
    private TableColumn<Participant, Integer> pingColumn;
    @FXML
    private TableColumn<Participant, String> instrumentColumn;

    @FXML
    Label localInetAddr;

    public void setMainApp(SzcoreClient mainApp) {
        this.mainApp = mainApp;
        participantsTable.setItems(mainApp.getParticipants());
    }

    @FXML
    private void initialize() {
//        hostAddressColumn.setCellValueFactory(cellData -> cellData.getValue().getHostAddressProperty());
//        inPortColumn.setCellValueFactory(cellData -> cellData.getValue().getPortInProperty().asObject());
//        outPortColumn.setCellValueFactory(cellData -> cellData.getValue().getPortOutProperty().asObject());
//        errPortColumn.setCellValueFactory(cellData -> cellData.getValue().getPortErrProperty().asObject());
//        pingColumn.setCellValueFactory(cellData -> cellData.getValue().getPingProperty().asObject());
//        instrumentColumn.setCellValueFactory(cellData -> cellData.getValue().getInstrumentProperty());
    }

    public void setPublisher(EventService publisher) {
//        this.publisher = publisher;
//        this.localAddress = publisher.getAddress();
//        localInetAddr.setText(localAddress.getHostAddress());
    }
}

