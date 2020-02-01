package com.xenaksys.szcore.gui.view;


import com.xenaksys.szcore.gui.SzcoreClient;
import com.xenaksys.szcore.gui.model.IpAddress;
import com.xenaksys.szcore.gui.model.NetworkClient;
import com.xenaksys.szcore.gui.model.Participant;
import com.xenaksys.szcore.model.EventService;
import com.xenaksys.szcore.model.ScoreService;
import com.xenaksys.szcore.util.NetUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.converter.NumberStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SettingsController {
    static final Logger LOG = LoggerFactory.getLogger(SettingsController.class);

    private SzcoreClient mainApp;
    private EventService publisher;
    private InetAddress serverAddress;
    private ScoreService scoreService;

    @FXML
    private Label serverInetAddrLbl;
    @FXML
    private Label inscoreClientPortLbl;
    @FXML
    private TextField broadcastAddrOct1Txt;
    @FXML
    private TextField broadcastAddrOct2Txt;
    @FXML
    private TextField broadcastAddrOct3Txt;
    @FXML
    private TextField broadcastAddrOct4Txt;
    @FXML
    private TextField subnetMaskOct1Txt;
    @FXML
    private TextField subnetMaskOct2Txt;
    @FXML
    private TextField subnetMaskOct3Txt;
    @FXML
    private TextField subnetMaskOct4Txt;
    @FXML
    private Label detectedBroadcastAddrLbl;
    @FXML
    private Button detectConnectedClientsBtn;
    @FXML
    private TableView<NetworkClient> networkClientsTableView;
    @FXML
    private TableColumn<NetworkClient, String> hostAddressColumn;
    @FXML
    private TableColumn<NetworkClient, String> hostNameColumn;
    @FXML
    private TableColumn<NetworkClient, Boolean> isParticipantColumn;
    @FXML
    public Button setBroadcastAddressBtn;
    @FXML
    public Button setSubnetMaskBtn;

    private IpAddress broadCastAddress = new IpAddress();
    private IpAddress subnetMask = new IpAddress();
    private ObservableList<NetworkClient> networkClients = FXCollections.observableArrayList();

    public void populate() {
        if (serverAddress != null) {
            serverInetAddrLbl.setText(serverAddress.getHostAddress());
        }
        inscoreClientPortLbl.setText(Integer.toString(scoreService.getInscorePort()));

        populateBroadcastAddress();
        populateSubnetMask();
        populateDetectedBroadcastAddresses();

        networkClientsTableView.setItems(networkClients);
    }

    public void populateBroadcastAddress() {
        InetAddress broadcastAddr = scoreService.getBroadcastAddress();
        if (broadcastAddr != null) {
            try {
                broadCastAddress.setIpAddress(broadcastAddr.getHostAddress());
            } catch (Exception e) {
                LOG.error("Failed to set populate broadcast address", e);
            }
        }
    }

    public void populateSubnetMask() {
        String subnetMaskAddr = scoreService.getSubnetMask();
        if (subnetMaskAddr != null) {
            try {
                subnetMask.setIpAddress(subnetMaskAddr);
            } catch (Exception e) {
                LOG.error("Failed to set populate broadcast address", e);
            }
        }
    }

    public void populateDetectedBroadcastAddresses() {
        List<InetAddress> detectedBroadcastAddrs = scoreService.getDetectedBroadcastAddresses();
        String delim = "";
        StringBuilder bas = new StringBuilder();
        for (InetAddress addr : detectedBroadcastAddrs) {
            bas.append(delim).append(addr.getHostAddress());
            delim = ",";
        }

        detectedBroadcastAddrLbl.setText(bas.toString());
    }

    @FXML
    private void initialize() {
        broadcastAddrOct1Txt.textProperty().bindBidirectional(broadCastAddress.getOctet1Property(), new NumberStringConverter());
        broadcastAddrOct2Txt.textProperty().bindBidirectional(broadCastAddress.getOctet2Property(), new NumberStringConverter());
        broadcastAddrOct3Txt.textProperty().bindBidirectional(broadCastAddress.getOctet3Property(), new NumberStringConverter());
        broadcastAddrOct4Txt.textProperty().bindBidirectional(broadCastAddress.getOctet4Property(), new NumberStringConverter());
        forceIntegerInput(broadcastAddrOct1Txt);
        forceIntegerInput(broadcastAddrOct2Txt);
        forceIntegerInput(broadcastAddrOct3Txt);
        forceIntegerInput(broadcastAddrOct4Txt);

        subnetMaskOct1Txt.textProperty().bindBidirectional(subnetMask.getOctet1Property(), new NumberStringConverter());
        subnetMaskOct2Txt.textProperty().bindBidirectional(subnetMask.getOctet2Property(), new NumberStringConverter());
        subnetMaskOct3Txt.textProperty().bindBidirectional(subnetMask.getOctet3Property(), new NumberStringConverter());
        subnetMaskOct4Txt.textProperty().bindBidirectional(subnetMask.getOctet4Property(), new NumberStringConverter());
        forceIntegerInput(subnetMaskOct1Txt);
        forceIntegerInput(subnetMaskOct2Txt);
        forceIntegerInput(subnetMaskOct3Txt);
        forceIntegerInput(subnetMaskOct4Txt);

        networkClientsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        hostAddressColumn.setCellValueFactory(cellData -> cellData.getValue().getHostAddressProperty());
        hostNameColumn.setCellValueFactory(cellData -> cellData.getValue().getHostNameProperty());
        isParticipantColumn.setCellValueFactory(cellData -> cellData.getValue().getIsParticipantProperty());
        isParticipantColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isParticipantColumn));
    }

    @FXML
    private void detectClients(ActionEvent event) {
        detectConnectedClientsBtn.setDisable(true);
        Service<Void> retrieveNetworkClientsService = createRetrieveConnectedClientsService();
        retrieveNetworkClientsService.start();
    }

    @FXML
    public void setSubnetMask(ActionEvent actionEvent) {
        String subnetMask = retrieveSubnetMask();
        scoreService.setSubnetMask(subnetMask);
        scoreService.setBroadcastAddress(null);
        scoreService.initNetInfo();
        populateBroadcastAddress();
        populateDetectedBroadcastAddresses();
    }

    @FXML
    public void setBroadcastAddress(ActionEvent actionEvent) {
        try {
            InetAddress broadcastAddress = getBroadcastAddress();
            scoreService.setBroadcastAddress(broadcastAddress);
            scoreService.initNetInfo();
        } catch (Exception e) {
            LOG.error("Failed to set broadcast address", e);
            mainApp.showDialog("Settings Controller", Alert.AlertType.ERROR, "Set Broadcast Address", "Failed set broadcast address " + e.getMessage());
        }
    }

    private void forceIntegerInput(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            } else if (newValue.length() > 3) {
                textField.setText(newValue.substring(0, 3));
            }
        });
    }

    public void setPublisher(EventService publisher) {
        this.publisher = publisher;
    }


    public void setMainApp(SzcoreClient mainApp) {
        this.mainApp = mainApp;
    }

    public void setScoreService(ScoreService scoreService) {
        this.scoreService = scoreService;
        this.serverAddress = scoreService.getServerAddress();
    }

    private String retrieveSubnetMask() {
        return subnetMask.toString();
    }

    private InetAddress getBroadcastAddress() throws Exception {
        String broadcastAddrStr = retrieveBroadcastAddressString();
        return InetAddress.getByName(broadcastAddrStr);
    }

    private String retrieveBroadcastAddressString() {
        return broadCastAddress.toString();
    }

    private Service<Void> createRetrieveConnectedClientsService() {
        return new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        List<NetUtil.NetworkDevice> devices = scoreService.getParallelConnectedNetworkClients();
                        ObservableList<Participant> participants = mainApp.getParticipants();
                        List<String> participantIps = new ArrayList<>();
                        for(Participant participant : participants) {
                            participantIps.add(participant.getHostAddress());
                        }
                        final CountDownLatch latch = new CountDownLatch(1);
                        List<NetworkClient> netClients = new ArrayList<>();
                        for(NetUtil.NetworkDevice device : devices) {
                            NetworkClient networkClient = new NetworkClient();
                            networkClient.setHostAddress(device.getHostIp());
                            networkClient.setHostName(device.getHostName());
                            networkClient.setIsParticipant(participantIps.contains(device.getHostIp()));
                            netClients.add(networkClient);
                        }

                        Platform.runLater(() -> {
                            try {
                                networkClients.clear();
                                networkClients.addAll(netClients);
                            } finally {
                                latch.countDown();
                            }
                        });
                        latch.await();
                        detectConnectedClientsBtn.setDisable(false);
                        return null;
                    }
                };
            }
        };
    }

}

