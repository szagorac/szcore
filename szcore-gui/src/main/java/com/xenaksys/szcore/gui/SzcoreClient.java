package com.xenaksys.szcore.gui;


import com.aquafx_project.AquaFx;
import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.gui.PrecountInfo;
import com.xenaksys.szcore.event.gui.ScoreInfoEvent;
import com.xenaksys.szcore.event.gui.ScoreMovementInfoEvent;
import com.xenaksys.szcore.event.gui.ScoreSectionInfoEvent;
import com.xenaksys.szcore.event.gui.WebAudienceClientInfoUpdateEvent;
import com.xenaksys.szcore.event.gui.WebScoreClientInfoUpdateEvent;
import com.xenaksys.szcore.gui.event.ClientIncomingEventReceiver;
import com.xenaksys.szcore.gui.event.ClientScoreEngineEventReceiver;
import com.xenaksys.szcore.gui.model.Participant;
import com.xenaksys.szcore.gui.processor.ClientEventProcessor;
import com.xenaksys.szcore.gui.processor.GuiLoggerProcessor;
import com.xenaksys.szcore.gui.view.DialogsScoreController;
import com.xenaksys.szcore.gui.view.LoggerController;
import com.xenaksys.szcore.gui.view.RootLayoutController;
import com.xenaksys.szcore.gui.view.ScoreController;
import com.xenaksys.szcore.gui.view.SettingsController;
import com.xenaksys.szcore.gui.view.SymphoneaScoreController;
import com.xenaksys.szcore.model.EventService;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.MovementInfo;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreService;
import com.xenaksys.szcore.model.SectionInfo;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.id.OscListenerId;
import com.xenaksys.szcore.model.id.StrId;
import com.xenaksys.szcore.score.OverlayType;
import com.xenaksys.szcore.server.SzcoreServer;
import com.xenaksys.szcore.time.clock.SimpleClock;
import com.xenaksys.szcore.web.WebClientInfo;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SzcoreClient extends Application {
    static final Logger LOG = LoggerFactory.getLogger(SzcoreClient.class);

    private final ScheduledExecutorService scheduleExecutor = Executors.newSingleThreadScheduledExecutor();

    private Stage primaryStage;
    private BorderPane rootLayout;

    private RootLayoutController rootController;
    private LoggerController loggerController;

    private Properties properties;
    private EventService eventService;
    private ScoreService scoreService;

    private ClientEventProcessor clientEventProcessor;
    private GuiLoggerProcessor loggerProcessor;

    private SymphoneaScoreController symphoneaScoreController;
    private DialogsScoreController dialogsScoreController;
    private ScoreController scoreController;
    private SettingsController settingsController;

    private ObservableList<Participant> participants = FXCollections.observableArrayList(param -> new Observable[] {param.getSelectProperty()});

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            this.primaryStage = primaryStage;
            this.primaryStage.setTitle("ZSCORE");
            this.primaryStage.getIcons().add(new Image("file:resources/images/Address_Book.png"));
            AquaFx.style();

            bootstrap();
        } catch (Exception e) {
            LOG.error("start: Failed to start application", e);
        }
    }

    private void bootstrap(){
        startBackEnd();

        initRootLayout();
        initLoggerTab();
        initSettingsTab();
        initDialogTab();
        initSymphoneaTab();
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

    public void initSettingsTab() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SzcoreClient.class.getResource("/SettingsTabLayout.fxml"));
            BorderPane setup = (BorderPane) loader.load();

            Tab setupTab = rootController.getSettingsTab();

            setupTab.setContent(setup);
            settingsController = loader.getController();
            settingsController.setMainApp(this);
            settingsController.setScoreService(scoreService);
            settingsController.setPublisher(eventService);

            settingsController.populate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public void initDialogTab() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SzcoreClient.class.getResource("/DialogsTabLayout.fxml"));
            BorderPane scorePane = (BorderPane) loader.load();

            Tab scoreTab = rootController.getDialogsScoreTab();

            scoreTab.setContent(scorePane);
            dialogsScoreController = loader.getController();

            dialogsScoreController.setMainApp(this);
            dialogsScoreController.setScoreService(scoreService);
            dialogsScoreController.setPublisher(eventService);

            dialogsScoreController.populate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initSymphoneaTab() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SzcoreClient.class.getResource("/SymphoneaTabLayout.fxml"));
            BorderPane scorePane = (BorderPane) loader.load();

            Tab symphoneaTab = rootController.getSymphoneaScoreTab();

            symphoneaTab.setContent(scorePane);
            symphoneaScoreController = loader.getController();

            symphoneaScoreController.setMainApp(this);
            symphoneaScoreController.setScoreService(scoreService);
            symphoneaScoreController.setPublisher(eventService);

            symphoneaScoreController.populate();
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

    public void addParticipant(Participant participant) {
        if (participant == null) {
            return;
        }
        Platform.runLater(() -> {
            checkSectionOwner(participant, false);
            if (participants.contains(participant)) {
                updateParticipant(participant);
                return;
            }

            LOG.info("Adding Participant: " + participant);
            participants.add(participant);
        });
    }

    public void updateParticipant(Participant participant) {
        Participant toUpdate = getParticipant(participant.getHostAddress(), participant.getPortIn());
        if (toUpdate == null) {
            return;
        }
        Platform.runLater(() -> {
//            LOG.info("Updating Participant: " + toUpdate);
            toUpdate.setClientId(participant.getClientId());
            toUpdate.setInstrument(participant.getInstrument());
            toUpdate.setPing(participant.getPing());
            toUpdate.setPortErr(participant.getPortErr());
            toUpdate.setPortOut(participant.getPortOut());
            toUpdate.setSelect(participant.getSelect());
            toUpdate.setIsReady(participant.getIsReady());
            toUpdate.setBanned(participant.isBanned());
        });
    }

    public Participant getParticipant(String hostAddress, int port) {
        if (hostAddress == null) {
            return null;
        }

        for (Participant participant : participants) {
            if (hostAddress.equals(participant.getHostAddress()) && port == participant.getPortIn()) {
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
        scoreController.onTransportPositionChange(transportId, beatNo);
    }

    public void onTempoEvent(Id transportId, Tempo tempo) {
        scoreController.onTempoEvent(transportId, tempo);
        dialogsScoreController.onTempoEvent(tempo);
    }

    public void processWebAudienceClientInfos(WebAudienceClientInfoUpdateEvent event) {
        settingsController.processWebClientInfos(event);
    }

    public void processWebScoreClientInfos(WebScoreClientInfoUpdateEvent event) {
        ArrayList<WebClientInfo> webClientInfos = event.getWebClientInfos();
        boolean isFullUpdate = event.isFullUpdate();
        ArrayList<Participant> toRemove = new ArrayList<>();

        if (isFullUpdate) {
            for (Participant participant : participants) {
                String host = participant.getHostAddress();
                int port = participant.getPortIn();
                boolean isPresent = false;
                for (WebClientInfo clientInfo : webClientInfos) {
                    String chost = clientInfo.getHost();
                    int cport = clientInfo.getPort();
                    if (host.equals(chost) && port == cport) {
                        isPresent = true;
                        continue;
                    }
                }
                if (!isPresent && participant.getIsWebClient()) {
                    toRemove.add(participant);
                }
            }
        }

        for (WebClientInfo clientInfo : webClientInfos) {
            Participant participant = new Participant();
            InetAddress addr = null;
            participant.setClientId(clientInfo.getClientId());
            try {
                addr = InetAddress.getByName(clientInfo.getHost());
            } catch (UnknownHostException e) {
            }
            participant.setInetAddress(addr);
            participant.setHostAddress(clientInfo.getHost());
            participant.setPortIn(clientInfo.getPort());
            String instrument = Consts.NAME_NA;
            if (clientInfo.getInstrument() != null) {
                instrument = clientInfo.getInstrument();
            }
            participant.setInstrument(instrument);
            participant.setIsWebClient(true);
            participant.setIsReady(clientInfo.isReady());
            participant.setBanned(clientInfo.isBanned());
            addParticipant(participant);
        }

        if (isFullUpdate) {
            for (Participant participant : toRemove) {
                Platform.runLater(new ParticipantRemover(participant));
            }
        }
    }

    private void checkSectionOwner(Participant participant, boolean isRemove) {
        if(isRemove) {
            dialogsScoreController.onParticipantRemove(participant);
        } else {
            dialogsScoreController.onParticipantUpdate(participant);
        }
    }

    public void processScoreMovementInfos(ScoreMovementInfoEvent event) {
        StrId scoreId = (StrId)event.getScoreId();
        String currentScore = scoreController.getScoreName();
        if(!scoreId.getName().equals(currentScore)) {
            return;
        }
        List<MovementInfo> movementInfos = event.getMovementInfos();
        String currentSection = event.getCurrentSection();
        String nextSection = event.getNextSection();
        String currentMovement = event.getCurrentMovement();
        String nextMovement = event.getNextMovement();
        symphoneaScoreController.onMovementInfo(movementInfos, currentMovement, nextMovement, currentSection, nextSection);
    }

    public void processScoreSectionInfos(ScoreSectionInfoEvent event) {
        StrId scoreId = (StrId)event.getScoreId();
        String currentScore = scoreController.getScoreName();
        if(!scoreId.getName().equals(currentScore)) {
            return;
        }
        List<String> sectionOrder = event.getSectionOrder();
        List<SectionInfo> sectionInfos = event.getSectionInfos();
        boolean isReady = event.isReady();
        String currentSection = event.getCurrentSection();
        String nextSection = event.getNextSection();
        dialogsScoreController.onSectionInfo(sectionInfos, sectionOrder, isReady, currentSection, nextSection);
    }

    public void processScoreInfo(ScoreInfoEvent event) {
        PrecountInfo precountInfo = event.getPrecountInfo();
        if(precountInfo != null) {
            processPrecountInfo(precountInfo);
        }
        boolean isStop = event.isStop();
        if(isStop) {
            Platform.runLater(this::onStop);
        }
    }


    public void processPrecountInfo(PrecountInfo precountInfo) {
        Platform.runLater(new PrecountUpdater(precountInfo));
    }

    private void onStop() {
        scoreController.showSemaphore(1, Color.RED);
        dialogsScoreController.showSemaphore(1, Color.RED);
        symphoneaScoreController.onStop();
    }

    private void updatePrecount(PrecountInfo precountInfo) {
        int beaterNo = precountInfo.getBeaterNo();
        int colId = precountInfo.getColourId();
        boolean isOn = precountInfo.isPrecountOn();
        if(isOn) {
            scoreController.showSemaphore(beaterNo, resolveColour(colId));
            dialogsScoreController.showSemaphore(beaterNo, resolveColour(colId));
            symphoneaScoreController.showSemaphore(beaterNo, resolveColour(colId));
        } else {
            scoreController.showSemaphore(4, Color.TRANSPARENT);
            dialogsScoreController.showSemaphore(4, Color.TRANSPARENT);
            symphoneaScoreController.showSemaphore(4, Color.TRANSPARENT);
        }
    }

    private Color resolveColour(int colId) {
        switch (colId) {
            case Consts.OSC_COLOUR_GREEN:
                return Color.GREEN;
            case Consts.OSC_COLOUR_YELLOW:
                return Color.YELLOW;
            case Consts.OSC_COLOUR_ORANGE:
                return Color.ORANGE;
            case Consts.OSC_COLOUR_RED:
                return Color.RED;
        }
        return null;
    }

    public void setPage(int startPage) {
        if(scoreController == null) {
            return;
        }
        scoreController.setPage(startPage);
    }

    public void resetScore() {
        if(scoreController != null) {
            scoreController.reset();
        }
        if(dialogsScoreController != null) {
            dialogsScoreController.reset();
        }
        if(symphoneaScoreController != null) {
            symphoneaScoreController.reset();
        }
    }

    public void onScoreLoad(Score score) {
        if(dialogsScoreController != null) {
            dialogsScoreController.onScoreLoad(score);
        }
        if(symphoneaScoreController != null) {
            symphoneaScoreController.onScoreLoad(score);
        }
    }

    public void sendPosition() {
        if(scoreController == null) {
            return;
        }
        scoreController.sendPosition();
    }

    public void playSection() {
        if(scoreController == null) {
            return;
        }
        scoreController.playSection();
    }

    public void stopSection() {
        if(scoreController == null) {
            return;
        }
        scoreController.stopSection();
    }

    public List<Id> getAllInstruments() {
        if(scoreController == null) {
            return null;
        }
        return scoreController.getAllInstruments();
    }

    public List<Id> getSelectedInstruments() {
        if(scoreController == null) {
            return null;
        }
        return scoreController.getSelectedInstruments();
    }

    public void sendDynamicsValueChange(long newVal, List<Id> instrumentIds) {
        if(scoreController == null) {
            return;
        }
        scoreController.sendDynamicsValueChange(newVal , instrumentIds);
    }

    public void sendUseDynamicsOverlay(Boolean newValue, int alpha, List<Id> instrumentIds) {
        if(scoreController == null) {
            return;
        }
        scoreController.sendUseDynamicsOverlay(newValue, alpha, instrumentIds);
    }

    public void sendUseDynamicsLine(Boolean newValue, List<Id> instrumentIds) {
        if(scoreController == null) {
            return;
        }
        scoreController.sendUseDynamicsLine(newValue, instrumentIds);
    }

    public void sendPitchValueChange(long value, List<Id> instrumentIds) {
        if (scoreController == null) {
            return;
        }
        scoreController.sendContentValueChange(value, instrumentIds);
    }

    public void sendPitchText(String l1, String l2, String l3, boolean isVisible, List<Id> instrumentIds) {
        if (scoreController == null) {
            return;
        }
        scoreController.sendOverlayText(OverlayType.PITCH, l1, l2, l3, isVisible, instrumentIds);
    }

    public void sendUsePitchOverlay(Boolean newValue, int alpha, List<Id> instrumentIds) {
        if (scoreController == null) {
            return;
        }
        scoreController.sendUseContentOverlay(newValue, alpha, instrumentIds);
    }

    public void sendUsePitchStaveOverlay(Boolean newValue, int alpha, List<Id> instrumentIds) {
        if (scoreController == null) {
            return;
        }
        scoreController.sendUsePitchStaveOverlay(newValue, alpha, instrumentIds);
    }

    public void sendUsePitchLine(Boolean newValue, List<Id> instrumentIds) {
        if(scoreController == null) {
            return;
        }
        scoreController.sendUseContentLine(newValue , instrumentIds);
    }

    public void sendTimbreValueChange(long value, List<Id> instrumentIds) {
        if(scoreController == null) {
            return;
        }
        scoreController.sendTimbreValueChange(value , instrumentIds);
    }

    public void sendUseTimbreOverlay(Boolean newValue, int alpha, List<Id> instrumentIds) {
        if(scoreController == null) {
            return;
        }
        scoreController.sendUseTimbreOverlay(newValue, alpha, instrumentIds);
    }

    public void sendUseTimbreLine(Boolean newValue, List<Id> instrumentIds) {
        if(scoreController == null) {
            return;
        }
        scoreController.sendUseTimbreLine(newValue , instrumentIds);
    }

    public void scheduleTask(Runnable task, long delay, TimeUnit tu) {
        scheduleExecutor.schedule(task, delay, tu);
    }

    class PrecountUpdater implements Runnable {
        private final PrecountInfo precountInfo;
        public PrecountUpdater(PrecountInfo precountInfo) {
            this.precountInfo = precountInfo;
        }
        @Override
        public void run() {
            updatePrecount(precountInfo);
        }
    }

    class ParticipantRemover implements Runnable {
        private final Participant participant;
        public ParticipantRemover(Participant participant) {
            this.participant = participant;
        }

        @Override
        public void run() {
            if (participants.contains(this.participant)) {
                participants.remove(this.participant);
            }
            checkSectionOwner(participant, true);
        }
    }

}
