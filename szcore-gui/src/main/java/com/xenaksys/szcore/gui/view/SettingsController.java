package com.xenaksys.szcore.gui.view;


import com.xenaksys.szcore.event.WebClientInfoUpdateEvent;
import com.xenaksys.szcore.gui.SzcoreClient;
import com.xenaksys.szcore.gui.model.AudienceClient;
import com.xenaksys.szcore.gui.model.IpAddress;
import com.xenaksys.szcore.model.EventService;
import com.xenaksys.szcore.model.ScoreService;
import com.xenaksys.szcore.web.WebClientInfo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.util.converter.NumberStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    private TableView<AudienceClient> networkClientsTableView;
    @FXML
    private TableColumn<AudienceClient, String> hostAddressColumn;
    @FXML
    private TableColumn<AudienceClient, String> hostNameColumn;
    @FXML
    private TableColumn<AudienceClient, Integer> portColumn;
    @FXML
    private TableColumn<AudienceClient, String> browserColumn;
    @FXML
    private TableColumn<AudienceClient, Boolean> mobileColumn;
    @FXML
    private TableColumn<AudienceClient, String> osColumn;
    @FXML
    public Button setBroadcastAddressBtn;
    @FXML
    public Button setSubnetMaskBtn;
    @FXML
    private Label audienceWebServerStatusLbl;
    @FXML
    private ToggleButton audienceWebServerOnTgl;
    @FXML
    private ToggleButton audienceWebServerOffTgl;

    private ToggleGroup audienceWebServerStatusTglGroup = new ToggleGroup();
    private IpAddress broadCastAddress = new IpAddress();
    private IpAddress subnetMask = new IpAddress();
    private ObservableList<AudienceClient> audienceClients = FXCollections.observableArrayList();
    private ObservableSet<AudienceClient> audienceClientSet = FXCollections.observableSet(new HashSet<>());

    public void populate() {
        if (serverAddress != null) {
            serverInetAddrLbl.setText(serverAddress.getHostAddress());
        }
        inscoreClientPortLbl.setText(Integer.toString(scoreService.getInscorePort()));

        populateBroadcastAddress();
        populateSubnetMask();
        populateDetectedBroadcastAddresses();

        networkClientsTableView.setItems(audienceClients);

        audienceClientSet.addListener((SetChangeListener.Change<? extends AudienceClient> c) -> {
            if (c.wasAdded()) {
                audienceClients.add(c.getElementAdded());
            }
            if (c.wasRemoved()) {
                audienceClients.remove(c.getElementRemoved());
            }
        });
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
        portColumn.setCellValueFactory(cellData -> cellData.getValue().getPortProperty().asObject());
        browserColumn.setCellValueFactory(cellData -> cellData.getValue().browserProperty());
        mobileColumn.setCellValueFactory(cellData -> cellData.getValue().isMobileProperty());
        osColumn.setCellValueFactory(cellData -> cellData.getValue().osProperty());

        audienceWebServerOnTgl.setToggleGroup(audienceWebServerStatusTglGroup);
        audienceWebServerOffTgl.setToggleGroup(audienceWebServerStatusTglGroup);
        detectAudienceWebServerStatus(null);
    }

    @FXML
    private void detectAudienceWebServerStatus(ActionEvent event) {
        Service<Void> retrieveWebServerStatus = createRetrieveAudienceWebServerStatus();
        retrieveWebServerStatus.start();
    }

    @FXML
    private void setAudienceWebServerOn(ActionEvent event) {
        scoreService.startWebServer();
        detectAudienceWebServerStatus(null);
    }

    @FXML
    private void setAudienceWebServerOff(ActionEvent event) {
        scoreService.stopWebServer();
        detectAudienceWebServerStatus(null);
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
                        HashMap<String, AudienceClient> clientUpdates = new HashMap<>();
                        ;

                        return null;
                    }
                };
            }
        };
    }

    private Service<Void> createRetrieveAudienceWebServerStatus() {
        return new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        final boolean isAudienceWebServerRunning = scoreService.isAudienceWebServerRunning();
                        final CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(() -> {
                            try {
                                if (isAudienceWebServerRunning) {
                                    audienceWebServerOnTgl.setSelected(true);
                                    audienceWebServerStatusLbl.setText("Running");
                                    LOG.info("Web server running. Setting label green");
                                    audienceWebServerStatusLbl.getStyleClass().remove("label-red");
                                    audienceWebServerStatusLbl.getStyleClass().add("label-green");
                                } else {
                                    audienceWebServerOffTgl.setSelected(true);
                                    audienceWebServerStatusLbl.setText("Stopped");
                                    LOG.info("Web server stopped. Setting label red");
                                    audienceWebServerStatusLbl.getStyleClass().remove("label-green");
                                    audienceWebServerStatusLbl.getStyleClass().add("label-red");
                                }
                            } finally {
                                latch.countDown();
                            }
                        });
                        latch.await();
                        return null;
                    }
                };
            }
        };
    }

    public void processWebClientInfos(WebClientInfoUpdateEvent event) {
        if (event == null) {
            return;
        }
        HashMap<String, AudienceClient> clientUpdates = new HashMap<>();
        ;
        try {
            ArrayList<WebClientInfo> webClientInfos = event.getWebClientInfos();
            for (WebClientInfo clientInfo : webClientInfos) {
                String addr = clientInfo.getClientAddr();
                AudienceClient audienceClient = new AudienceClient(addr);
                audienceClient.setHostAddress(addr);
                audienceClient.setHostName(clientInfo.getHost());
                audienceClient.setPort(clientInfo.getPort());
                if (clientInfo.getBrowserType() != null) {
                    audienceClient.setBrowser(clientInfo.getBrowserType().name());
                }
                audienceClient.setIsMobile(clientInfo.isMobile());
                if (clientInfo.getOs() != null) {
                    audienceClient.setOs(clientInfo.getOs().name());
                }
                clientUpdates.put(addr, audienceClient);
            }
        } catch (Exception e) {
            LOG.error("Failed to process web vlient infos", e);
        }
        Platform.runLater(() -> {
            ArrayList<AudienceClient> toRemove = new ArrayList<>();
            for (AudienceClient client : audienceClientSet) {
                if (!clientUpdates.containsKey(client.getId())) {
                    toRemove.add(client);
                } else {
                    AudienceClient update = clientUpdates.get(client.getId());
                    client.setHostAddress(update.getHostAddress());
                    client.setPort(update.getPort());
                    client.setBrowser(update.getBrowser());
                    client.setIsMobile(update.getIsMobile());
                    client.setOs(update.getOs());
                }
            }
            for (AudienceClient client : toRemove) {
                audienceClientSet.remove(client);
            }
            audienceClientSet.addAll(clientUpdates.values());
            detectConnectedClientsBtn.setDisable(false);
        });
    }
}

