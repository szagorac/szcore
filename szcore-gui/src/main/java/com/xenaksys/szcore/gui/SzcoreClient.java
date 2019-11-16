package com.xenaksys.szcore.gui;


import com.aquafx_project.AquaFx;
import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.gui.event.ClientIncomingEventReceiver;
import com.xenaksys.szcore.gui.event.ClientScoreEngineEventReceiver;
import com.xenaksys.szcore.gui.model.Participant;
import com.xenaksys.szcore.gui.processor.ClientEventProcessor;
import com.xenaksys.szcore.gui.processor.GuiLoggerProcessor;
import com.xenaksys.szcore.gui.view.LoggerController;
import com.xenaksys.szcore.gui.view.RootLayoutController;
import com.xenaksys.szcore.gui.view.ScoreController;
import com.xenaksys.szcore.model.EventService;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.ScoreService;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.id.OscListenerId;
import com.xenaksys.szcore.server.SzcoreServer;
import com.xenaksys.szcore.time.clock.SimpleClock;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class SzcoreClient extends Application {
    static final Logger LOG = LoggerFactory.getLogger(SzcoreClient.class);

    private Stage primaryStage;
    private BorderPane rootLayout;

    private RootLayoutController rootController;
    private LoggerController loggerController;

    private Properties properties;
    private EventService eventService;
    private ScoreService scoreService;

    private ClientEventProcessor clientEventProcessor;
    private GuiLoggerProcessor loggerProcessor;

    private ScoreController scoreController;

    private ObservableList<Participant> participants = FXCollections.observableArrayList(param -> new Observable[] {param.getSelectProperty()});

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("SZCORE");
        this.primaryStage.getIcons().add(new Image("file:resources/images/Address_Book.png"));
        AquaFx.style();

        bootstrap();
    }

    private void bootstrap(){
        startBackEnd();

        initRootLayout();
        initLoggerTab();
//        initSetupTab();
        initScoreTab();

        initProcessors();

        show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        eventService.stop();
        System.exit(1);
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    private void initProcessors() {
        clientEventProcessor = new ClientEventProcessor(this);

        SimpleClock clock = new SimpleClock();
        loggerProcessor = new GuiLoggerProcessor(loggerController, clock);

        ClientIncomingEventReceiver clientIncomingEventReceiver = new ClientIncomingEventReceiver(clientEventProcessor, eventService,
                new OscListenerId(Consts.DEFAULT_ALL_PORTS, "localhost", "ClientIncomingEventReceiver"));
        clientIncomingEventReceiver.init();

        ClientScoreEngineEventReceiver clientScoreEngineEventReceiver = new ClientScoreEngineEventReceiver(clientEventProcessor, scoreService);
        clientScoreEngineEventReceiver.init();
    }

    public void initRootLayout() {
        try {
            primaryStage.hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SzcoreClient.class.getResource("/RootLayout.fxml"));
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            rootController = loader.getController();
            rootController.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initLoggerTab() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SzcoreClient.class.getResource("/LoggerTabLayout.fxml"));
            AnchorPane logger = (AnchorPane) loader.load();

            Tab loggerTab = rootController.getLoggerTab();

            loggerTab.setContent(logger);
            loggerController = loader.getController();

            loggerController.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//
//    public void initSetupTab() {
//        try {
//
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(SzcoreClient.class.getResource("/SetupTabLayout.fxml"));
//            BorderPane setup = (BorderPane) loader.load();
//
//            Tab setupTab = rootController.getSetupTab();
//
//            setupTab.setContent(setup);
//            SetupController controller = loader.getController();
//
//            controller.setMainApp(this);
//
//            controller.setPublisher(eventService);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void initScoreTab() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SzcoreClient.class.getResource("/ScoreTabLayout.fxml"));
            BorderPane scorePane = (BorderPane) loader.load();

            Tab scoreTab = rootController.getScoreTab();

            scoreTab.setContent(scorePane);
            scoreController = loader.getController();

            scoreController.setMainApp(this);
            scoreController.setScoreService(scoreService);
            scoreController.setPublisher(eventService);

            scoreController.populate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void show() {
        if(primaryStage == null){
            LOG.error("Invalid Primary Stage");
            return;
        }

        primaryStage.show();
    }

    public Stage getPrimaryStage(){
        return primaryStage;
    }

    private void startBackEnd() {
        eventService = (SzcoreServer) SzcoreServer.buildStandAlone();
        scoreService = (ScoreService) eventService;
        SzcoreServer.createInstanceAndRun((SzcoreServer) eventService);
        properties = SzcoreServer.getProperties();
    }

    public ObservableList<Participant> getParticipants() {
        return participants;
    }

    public void addParticipant(Participant participant){
        if(participant == null){
            return;
        }

        if(participants.contains(participant)){
            LOG.warn("Participant is already registered: " + participant);
            return;
        }

        LOG.info("Adding Participant: " + participant);
        participants.add(participant);
    }

    public Participant getParticipant(String hostAddress){
        if(hostAddress == null){
            return null;
        }

        for(Participant participant : participants){
            if(hostAddress.equals(participant.getHostAddress())){
                return participant;
            }
        }

        return null;
    }

    public void logEvent(SzcoreEvent event){
        loggerProcessor.process(event);
    }

    public void showDialog(String title, Alert.AlertType alertType, String header, String content){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public void onTransportBeatEvent(Id transportId, int beatNo, int baseBeatNo) {
        scoreController.onTransportBeatEvent(transportId, beatNo, baseBeatNo);
    }

    public void onTransportPositionChange(Id transportId, int beatNo) {
        scoreController.onTransportPoisitionChange(transportId, beatNo);
    }

    public void onTempoEvent(Id transportId, Tempo tempo) {
        scoreController.onTempoEvent(transportId, tempo);
    }

}
