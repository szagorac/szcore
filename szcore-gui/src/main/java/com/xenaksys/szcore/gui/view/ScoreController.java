package com.xenaksys.szcore.gui.view;


import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.AddPartsEvent;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.gui.SzcoreClient;
import com.xenaksys.szcore.gui.model.Participant;
import com.xenaksys.szcore.model.Bar;
import com.xenaksys.szcore.model.Beat;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.EventService;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreService;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.TempoModifier;
import com.xenaksys.szcore.model.id.BarId;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.util.Util;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ScoreController {
    static final Logger LOG = LoggerFactory.getLogger(ScoreController.class);

    private final static String NL = "\n";
    private final static int UNSET_VALUE = 0;

    private ScoreService scoreService;

    private Score score;

    @FXML
    private Label scoreNameLbl;
    @FXML
    private Button loadScoreBtn;
    @FXML
    private Button sendPartsBtn;
    @FXML
    private Button sendSelectedPartsBtn;
    @FXML
    private Button scorePlayBtn;
    @FXML
    private Button scoreStopBtn;
    @FXML
    private ComboBox<Integer> pageNoCbx;
    @FXML
    private ComboBox<Integer> barNoCbx;
    @FXML
    private ComboBox<Integer> beatNoCbx;
    @FXML
    private ListView<String> instrumentsListView;
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
    private TableColumn<Participant, Double> pingColumn;
    @FXML
    private TableColumn<Participant, String> instrumentColumn;
    @FXML
    private Label localInetAddrLbl;
    @FXML
    private Label pageNoLbl;
    @FXML
    private Label barNoLbl;
    @FXML
    private Label beatNoLbl;
    @FXML
    private Label tickNoLbl;
    @FXML
    private Label tempoLbl;
    @FXML
    private ChoiceBox<Double> tempoModifierChob;

    private SzcoreClient mainApp;
    private EventService publisher;
    private InetAddress localAddress;
//    private PlayPosition playPosition = new PlayPosition();
    private Clock clock;
    private ObservableList<String> instrumentsList = FXCollections.observableArrayList();
    private ObservableList<Integer> pagesList = FXCollections.observableArrayList();
    private ObservableList<Integer> barsList = FXCollections.observableArrayList();
    private ObservableList<Integer> beatsList = FXCollections.observableArrayList();
    private ObservableList<Double> tempoMultipliers = FXCollections.observableArrayList();

    private boolean isPageSetCall = false;
    private boolean isBarSetCall = false;
    private boolean isBeatSetCall = false;

    private long positionMillis = 0L;

    public void setMainApp(SzcoreClient mainApp) {
        this.mainApp = mainApp;
    }

    public void populate() {
        instrumentsListView.setItems(instrumentsList);
        pageNoCbx.setItems(pagesList);
        barNoCbx.setItems(barsList);
        beatNoCbx.setItems(beatsList);
        participantsTable.setItems(mainApp.getParticipants());
        tempoModifierChob.setItems(tempoMultipliers);

        tempoMultipliers.addAll(getTempoMultiplierValues());
        tempoModifierChob.getSelectionModel().select(Consts.ONE_D);

        tempoModifierChob.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                return;
            }

            onTempoModifierChobChange(newSelection);
        });
    }

    public void setScoreService(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    public void setPublisher(EventService publisher) {
        this.publisher = publisher;
        this.clock = publisher.getClock();
        this.localAddress = publisher.getAddress();
        localInetAddrLbl.setText(localAddress.getHostAddress());
    }

    @FXML
    private void initialize() {

        participantsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        hostAddressColumn.setCellValueFactory(cellData -> cellData.getValue().getHostAddressProperty());
        inPortColumn.setCellValueFactory(cellData -> cellData.getValue().getPortInProperty().asObject());
        outPortColumn.setCellValueFactory(cellData -> cellData.getValue().getPortOutProperty().asObject());
        errPortColumn.setCellValueFactory(cellData -> cellData.getValue().getPortErrProperty().asObject());
        pingColumn.setCellValueFactory(cellData -> cellData.getValue().getPingProperty().asObject());
        instrumentColumn.setCellValueFactory(cellData -> cellData.getValue().getInstrumentProperty());

//        pageNoLbl.textProperty().bind(playPosition.pageNoProperty());
//        barNoLbl.textProperty().bind(playPosition.barNoProperty());
//        beatNoLbl.textProperty().bind(playPosition.beatNoProperty());
//        tickNoLbl.textProperty().bind(playPosition.tickNoProperty());

        instrumentColumn.setCellFactory(column -> {
            return new TableCell<Participant, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText("");
                        setStyle("");
                    } else if (Consts.NAME_NA.equals(item)) {
                        setText(Consts.NAME_NA);
                        setTextFill(Color.BLACK);
                        setStyle("-fx-background-color: red");
                    }
                    else {
                        // Format date.
                        setText(item);
                        setStyle("");
                        setTextFill(Color.CHOCOLATE);
                    }
                }
            };
        });
    }

    @FXML
    private void openScoreFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Score File");
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        if (file != null) {
            openFile(file);
        }
    }

    @FXML
    private void playScore(ActionEvent event) {
        try {
            scoreService.play(positionMillis);
        } catch (Exception e) {
            LOG.error("Failed to play score", e);
        }
    }

    @FXML
    private void stopScore(ActionEvent event) {
        try {
            scoreService.stopPlay();
        } catch (Exception e) {
            LOG.error("Failed to stop score", e);
        }
    }

    @FXML
    private void sendParts(ActionEvent event) {
        String instrumentsCsv = getInstrumentsCsv();
        ObservableList<Participant> participants = mainApp.getParticipants();


        for(Participant participant : participants){
            sendAddParts(participant, instrumentsCsv);
        }
    }

    @FXML
    private void sendPartsToSelectedParticipants(ActionEvent event) {
        String instrumentsCsv = getInstrumentsCsv();

        ObservableList<Participant> selectedParticipants = participantsTable.getSelectionModel().getSelectedItems();
        if(selectedParticipants == null){
            return;
        }

        for(Participant participant : selectedParticipants){
            sendAddParts(participant, instrumentsCsv);
        }
    }

    @FXML
    private void setPageNo(ActionEvent event) {
        if(isBarSetCall || isBeatSetCall){
            return;
        }
        isPageSetCall = true;
        try {
            setPageValue();
        } catch (Exception e) {
           LOG.error("Failed to set page number", e);
        } finally {
            isPageSetCall = false;
        }
    }

    private void setPageValue(){
        if(pagesList.isEmpty()){
            return;
        }

        Object selected = pageNoCbx.getSelectionModel().getSelectedItem();
        if(selected == null) {
            return;
        }
        Integer v = 1;
        if(selected instanceof Integer) {
              v = (Integer)selected;
        } else if (selected instanceof String) {
              v = Integer.valueOf((String)selected);
        }
        
        int pageNo = v.intValue();
        Collection<Page> pages = score.getPages();
        Page selectedPage = null;
        for(Page page : pages){
            if(pageNo == page.getPageNo()){
                selectedPage = page;
                break;
            }
        }
        if(selectedPage == null){
            return;
        }

        Bar selectedBar = null;
        Integer minBarNo = Integer.MAX_VALUE;
        Collection<Bar> bars = selectedPage.getBars();
        for(Bar bar : bars){
            if(bar.isUpbeatBar()){
                continue;
            }
            if(bar.getBarNo() < minBarNo){
                minBarNo = bar.getBarNo();
                selectedBar = bar;
            }
        }

        if(selectedBar == null){
            return;
        }
        barNoCbx.getSelectionModel().select(minBarNo);

        Integer minBeatNo = Integer.MAX_VALUE;
        selectedBar.getTimeSignature();
        Collection<Beat> beats = selectedBar.getBeats();
        beatsList.clear();
        for(Beat beat : beats){
            if(beat.isUpbeat()){
                continue;
            }
            int beatNo = beat.getBeatNo();
            if(beatNo < minBeatNo){
                minBeatNo = beatNo;
            }
            if(!beatsList.contains(beatNo)) {
                beatsList.add(beatNo);
            }
        }
        FXCollections.sort(beatsList);
        beatNoCbx.getSelectionModel().select(minBeatNo);
    }

    @FXML
    private void setBarNo(ActionEvent event) {
        if(isPageSetCall || isBeatSetCall){
            return;
        }

        isBarSetCall = true;
        try {
            setBarValue();
        } catch (Exception e) {
            LOG.error("Failed to set bar number", e);
        } finally {
            isBarSetCall = false;
        }
    }

    private void setBarValue() {

        if(barsList.isEmpty()){
            return;
        }

        Object selected = barNoCbx.getSelectionModel().getSelectedItem();
        if(selected == null) {
            return;
        }
        Integer v = 1;
        if(selected instanceof Integer) {
            v = (Integer)selected;
        } else if (selected instanceof String) {
            v = Integer.valueOf((String)selected);
        }

        int barNo = v.intValue();
        Collection<Bar> bars = score.getBars();
        Bar selectedBar = null;
        List<Bar> barsWithNo = new ArrayList<>();
        for(Bar bar : bars){
            if(bar.isUpbeatBar()){
                continue;
            }
            if(barNo == bar.getBarNo()){
                barsWithNo.add(bar);
            }
        }

        PageId minPageId = null;
        for(Bar bar : barsWithNo){
            Id pid = bar.getPageId();
            if(pid == null){
                continue;
            }

            PageId pageId = (PageId)pid;
            if(minPageId == null){
                minPageId = pageId;
                selectedBar = bar;
                continue;
            }

            if(pageId.getPageNo() < minPageId.getPageNo()){
                minPageId = pageId;
                selectedBar = bar;
            }
        }

        if(selectedBar == null){
            return;
        }

        Integer pno = minPageId.getPageNo();
        pageNoCbx.getSelectionModel().select(pno);

        Integer minBeatNo = Integer.MAX_VALUE;
        Collection<Beat> beats = selectedBar.getBeats();
        beatsList.clear();
        for(Beat beat : beats){
            int beatNo = beat.getBeatNo();
            if(beatNo < minBeatNo){
                minBeatNo = beatNo;
            }
            if(!beatsList.contains(beatNo)) {
                beatsList.add(beatNo);
            }
        }
        beatNoCbx.getSelectionModel().select(minBeatNo);
        FXCollections.sort(beatsList);

    }

    @FXML
    private void setBeatNo(ActionEvent event) {
        if(isPageSetCall || isBarSetCall){
            return;
        }
        isBeatSetCall = true;

        try {
            setBeatValue();
        } catch (Exception e) {
            LOG.error("Failed to set beat number", e);
        } finally {
            isBeatSetCall = false;
        }
    }

    private void setBeatValue() {

        if(beatsList.isEmpty()){
            return;
        }

        Object selected  = beatNoCbx.getSelectionModel().getSelectedItem();
        if(selected == null) {
            return;
        }
        Integer v = 1;
        if(selected instanceof Integer) {
            v = (Integer)selected;
        } else if (selected instanceof String) {
            v = Integer.valueOf((String)selected);
        }

        int beatNo = v.intValue();
        
        Collection<Beat> beats = score.getBeats();
        List<Beat> beatsWithNo = new ArrayList<>();
        for(Beat beat : beats){
            if(beat.isUpbeat()){
                continue;
            }
            if(beatNo == beat.getBeatNo()){
                beatsWithNo.add(beat);
            }
        }

        BarId selectedBarId = null;
        Integer minBarId = 0;
        for(Beat beat : beatsWithNo){
            Id bid = beat.getBarId();
            if(bid == null) {
                continue;
            }
            BarId barId = (BarId)bid;
            if(selectedBarId == null){
                selectedBarId = barId;
                minBarId = selectedBarId.getBarNo();
                continue;
            }

            if(barId.getBarNo() < selectedBarId.getBarNo()){
                selectedBarId = barId;
                minBarId = selectedBarId.getBarNo();
            }
        }

        if(selectedBarId == null){
            return;
        }
        barNoCbx.getSelectionModel().select(minBarId);

        Id pid = selectedBarId.getPageId();
        if(pid == null){
            return;
        }

        PageId pageId = (PageId)pid;
        Integer minPageNo = pageId.getPageNo();
        pageNoCbx.getSelectionModel().select(minPageNo);
    }

    @FXML
    private void sendPosition(ActionEvent event) {
        if(beatNoCbx == null || beatNoCbx.getSelectionModel() == null || beatNoCbx.getSelectionModel().isEmpty()){
            return;
        }

        Object selected  = beatNoCbx.getSelectionModel().getSelectedItem();
        if(selected == null) {
            return;
        }
        Integer v = 1;
        if(selected instanceof Integer) {
            v = (Integer)selected;
        } else if (selected instanceof String) {
            v = Integer.valueOf((String)selected);
        }

        int beatNo = v;

        Collection<Beat> beats = score.getBeats();
        List<Beat> beatsWithNo = new ArrayList<>();
        for(Beat beat : beats){
            if(beat.isUpbeat()){
                continue;
            }
            if(beatNo == beat.getBeatNo()){
                beatsWithNo.add(beat);
            }
        }


        if(beatsWithNo.isEmpty()){
            mainApp.showDialog("Score Controller", Alert.AlertType.ERROR, "Send Position Error", "Failed to find beat: " + beatNo);
            return;
        }

        long minMillis = Long.MAX_VALUE;
        for(Beat beat : beatsWithNo){
            long startTime = beat.getStartTimeMillis();
            if(startTime < minMillis){
                minMillis = startTime;
            }
        }

        if(minMillis == Long.MAX_VALUE){
            mainApp.showDialog("Score Controller", Alert.AlertType.ERROR, "Send Position Error", "Failed to find populate millis for beat: " + beatNo);
            return;
        }

        positionMillis = minMillis;
        scoreService.setPosition(positionMillis);
    }

    private String getInstrumentsCsv(){
        StringBuilder sb = new StringBuilder();
        for(String instrumentName : instrumentsList){
            sb.append(instrumentName);
            sb.append(Consts.COMMA);
        }

        String instrumentCsv = sb.toString();
        return Util.removeEndComma(instrumentCsv);
    }

    private void sendAddParts(Participant participant, String instrumentsCsv){
        if(participant == null || instrumentsCsv == null || instrumentsCsv.length() < 1){
            return;
        }
        EventFactory eventFactory = publisher.getEventFactory();
        String destination = participant.getHostAddress();
        AddPartsEvent addPartsEvent = eventFactory.createAddPartsEvent(instrumentsCsv, destination, clock.getSystemTimeMillis());
        publisher.publish(addPartsEvent);
    }

    private void openFile(File file) {
        try {
            reset();
            this.score = scoreService.loadScore(file);
            viewScore();
        } catch (Exception e) {
            LOG.error("Failed to open score", e);
        }
    }

    private void reset() {
        if(!scoreService.reset()){
            LOG.warn("Sever failed to reset");
            return;
        }
        this.score = null;
        instrumentsList.clear();
        pagesList.clear();
        barsList.clear();
        beatsList.clear();
        isPageSetCall = false;
        isBarSetCall = false;
        isBeatSetCall = false;
        positionMillis = 0L;
        ObservableList<Participant> participants = mainApp.getParticipants();
        for(Participant participant : participants){
            participant.setInstrument(Consts.NAME_NA);
        }

    }

    public void viewScore(){
        if(score == null){
            return;
        }

        String name = score.getName();
        scoreNameLbl.setText(name);

        Collection<Instrument> instruments = score.getInstruments();
        if(instruments == null){
            return;
        }
        Iterator<Instrument> instIter = instruments.iterator();
        while(instIter.hasNext()){
            Instrument instrument = instIter.next();
            instrumentsList.add(instrument.getName());
        }

        pagesList.add(Consts.ONE_I);
        Collection<Page> pages = score.getPages();
        for(Page page : pages){
            int pageNo = page.getPageNo();
            if(!pagesList.contains(pageNo)) {
                pagesList.add(pageNo);
            }
        }
        FXCollections.sort(pagesList);
        pageNoCbx.getSelectionModel().select(Consts.ONE_I);

        barsList.add(Consts.ONE_I);
        Collection<Bar> bars = score.getBars();
        for(Bar bar : bars){
            if(bar.isUpbeatBar()){
                continue;
            }
            int barNo = bar.getBarNo();
            if(!barsList.contains(barNo)) {
                barsList.add(barNo);
            }
        }
        FXCollections.sort(barsList);
        barNoCbx.getSelectionModel().select(Consts.ONE_I);

        beatsList.add(Consts.ONE_I);
        Collection<Beat> beats = score.getBeats();
        for(Beat beat : beats){
            if(beat.isUpbeat()){
                continue;
            }
            int beatNo = beat.getBeatNo();
            if(!beatsList.contains(beatNo)) {
                beatsList.add(beatNo);
            }
        }
        FXCollections.sort(beatsList);
        beatNoCbx.getSelectionModel().select(Consts.ONE_I);

    }


    private void updateBeatInfo(Id transportId, int pageNo, int barNo, int beatNo, int baseBeatNo) {
        if(this.score == null){
            return;
        }

        pageNoLbl.setText(String.valueOf(pageNo));
        barNoLbl.setText(String.valueOf(barNo));
        beatNoLbl.setText(String.valueOf(beatNo));
    }

    private void updateTempo(Id transportId, int tempo) {
        if(this.score == null){
            return;
        }

        tempoLbl.setText(String.valueOf(tempo));
    }

    public void onTransportBeatEvent(Id transportId, int beatNo, int baseBeatNo){
        List<BeatId> beatIds = score.findBeatIds(transportId, baseBeatNo);
        if(beatIds == null) {
            LOG.info("BeatIds are NULL");
            return;
        }
        int pageNo = 0;
        int barNo = 0;
        for(BeatId beatId : beatIds){
            if(beatId.getBaseBeatNo() == baseBeatNo){
                PageId pageId = (PageId)beatId.getPageId();
                pageNo = pageId.getPageNo();
                BarId barId = (BarId)beatId.getBarId();
                barNo = barId.getBarNo();
                break;
            }
        }

        Platform.runLater(new TransportBeatUpdater(transportId, pageNo, barNo, beatNo, baseBeatNo));
    }

    public void onTransportPoisitionChange(Id transportId, int baseBeatNo){
        List<BeatId> beatIds = score.findBeatIds(transportId, baseBeatNo);
        if(beatIds == null) {
            LOG.info("BeatIds are NULL");
            return;
        }
        int pageNo = 0;
        int barNo = 0;
        int beatNo = 0;
        for(BeatId beatId : beatIds){
            if(beatId.getBaseBeatNo() == baseBeatNo){
                PageId pageId = (PageId)beatId.getPageId();
                pageNo = pageId.getPageNo();
                BarId barId = (BarId)beatId.getBarId();
                barNo = barId.getBarNo();
                beatNo = beatId.getBeatNo();
                break;
            }
        }

        Platform.runLater(new TransportBeatUpdater(transportId, pageNo, barNo, beatNo, baseBeatNo));
    }

    public void onTempoEvent(Id transportId, Tempo tempo){
        Platform.runLater(new TempoUpdater(transportId, tempo.getBpm()));
    }

    private Double[] getTempoMultiplierValues() {
        return Consts.TEMPO_MULTIPLIERS;
    }

    private void onTempoModifierChobChange(Double newSelection) {
        if(score == null){
            return;
        }
 LOG.info("Have new Modifier selection: " + newSelection);
        TempoModifier tempoModifier = new TempoModifier(newSelection);
        Collection<Id> transportIds = score.getTransportIds();
        if(transportIds == null){
            return;
        }

        for(Id transportId : transportIds) {
            scoreService.setTempoModifier(transportId, tempoModifier);
        }
    }

    class TransportBeatUpdater implements Runnable {
        private Id transportId;
        private int pageNo;
        private int barNo;
        private int beatNo;
        private int baseBeatNo;

        public TransportBeatUpdater(Id transportId, int pageNo, int barNo, int beatNo, int baseBeatNo) {

            this.transportId = transportId;
            this.beatNo = beatNo;
            this.baseBeatNo = baseBeatNo;
            this.pageNo = pageNo;
            this.barNo = barNo;
        }

        @Override
        public void run() {
//LOG.info("Client receieved baseBeatNoBeatNo: " + baseBeatNo);
            updateBeatInfo(transportId, pageNo, barNo, beatNo, baseBeatNo);
        }
    }

    class TempoUpdater implements Runnable {
        private Id transportId;
        private int tempo;

        public TempoUpdater(Id transportId, int tempo) {
            this.transportId = transportId;
            this.tempo = tempo;
        }

        @Override
        public void run() {
//LOG.info("Client receieved tempo: " + tempo);
            updateTempo(transportId, tempo);
        }
    }

}

