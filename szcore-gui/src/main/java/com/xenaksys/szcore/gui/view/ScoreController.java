package com.xenaksys.szcore.gui.view;


import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.osc.AddPartsEvent;
import com.xenaksys.szcore.event.osc.SendServerIpBroadcastEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceInstructionsEvent;
import com.xenaksys.szcore.gui.SzcoreClient;
import com.xenaksys.szcore.gui.model.Participant;
import com.xenaksys.szcore.gui.model.WebscoreInstructions;
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
import com.xenaksys.szcore.score.OverlayType;
import com.xenaksys.szcore.util.NetUtil;
import com.xenaksys.szcore.util.Util;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.EMPTY;

public class ScoreController {
    static final Logger LOG = LoggerFactory.getLogger(ScoreController.class);

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
    private Button disconnectSelectedParticipantsBtn;
    @FXML
    private Button banSelectedParticipantsBtn;
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
    private TableView<Participant> participantsTableView;
    @FXML
    private TableColumn<Participant, String> clientIdColumn;
    @FXML
    private TableColumn<Participant, String> hostAddressColumn;
    @FXML
    private TableColumn<Participant, Integer> inPortColumn;
    @FXML
    private TableColumn<Participant, Double> pingColumn;
    @FXML
    private TableColumn<Participant, String> instrumentColumn;
    @FXML
    private TableColumn<Participant, String> expiredColumn;
    @FXML
    private TableColumn<Participant, Boolean> selectColumn;
    @FXML
    private TableColumn<Participant, Boolean> isWebClientColumn;
    @FXML
    private TableColumn<Participant, Boolean> readyColumn;
    @FXML
    private TableColumn<Participant, Boolean> bannedColumn;
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
    @FXML
    private Slider tempoModifierSldr;
    @FXML
    private ChoiceBox<String> randomStrategyChob;
    @FXML
    private CheckBox usePageRandomisationChb;
    @FXML
    private CheckBox useContinousPageChb;
    @FXML
    private Slider dynamicsSldr;
    @FXML
    private Label dynamicsValLbl;
    @FXML
    private CheckBox useDynamicsOverlayChb;
    @FXML
    private CheckBox useDynamicsLineChb;
    @FXML
    private CheckBox sendToAllChb;
    @FXML
    private Slider pressureSldr;
    @FXML
    private Label pressureValLbl;
    @FXML
    private CheckBox usePressureOverlayChb;
    @FXML
    private CheckBox usePressureLineChb;
    @FXML
    private Slider speedSldr;
    @FXML
    private Label speedValLbl;
    @FXML
    private CheckBox useSpeedOverlayChb;
    @FXML
    private CheckBox useSpeedLineChb;
    @FXML
    private Slider positionSldr;
    @FXML
    private Label positionValLbl;
    @FXML
    private CheckBox usePositionOverlayChb;
    @FXML
    private CheckBox usePositionLineChb;
    @FXML
    private Slider contentSldr;
    @FXML
    private Label contentValLbl;
    @FXML
    private CheckBox useContentOverlayChb;
    @FXML
    private CheckBox useContentLineChb;
    @FXML
    private ChoiceBox<String> presetsChob;
    @FXML
    private TextField instL1Txt;
    @FXML
    private TextField instL2Txt;
    @FXML
    private TextField instL3Txt;
    @FXML
    private Button sendWebscoreInstructionsBtn;
    @FXML
    private Button clearWebscoreInstructionsBtn;

    private SzcoreClient mainApp;
    private EventService publisher;
    private InetAddress serverAddress;
    //    private PlayPosition playPosition = new PlayPosition();
    private Clock clock;
    private ObservableList<String> instrumentsList = FXCollections.observableArrayList();
    private ObservableList<Integer> pagesList = FXCollections.observableArrayList();
    private ObservableList<Integer> barsList = FXCollections.observableArrayList();
    private ObservableList<Integer> beatsList = FXCollections.observableArrayList();
    private ObservableList<Double> tempoMultipliers = FXCollections.observableArrayList();
    private ObservableList<String> randomisationStrategies = FXCollections.observableArrayList();
    private ObservableList<String> presets = FXCollections.observableArrayList();

    private boolean isPageSetCall = false;
    private boolean isBarSetCall = false;
    private boolean isBeatSetCall = false;

    private Map<String, Id> instrumentNameId = new HashMap<>();
    private ObservableList<Participant> selectedParticipants = FXCollections.observableArrayList();
    private ObservableList<Participant> participants;
    private WebscoreInstructions webscoreInstructions;

    private long positionMillis = 0L;

    public void setMainApp(SzcoreClient mainApp) {
        this.mainApp = mainApp;
    }

    public void populate() {
        participants = mainApp.getParticipants();
        instrumentsListView.setItems(instrumentsList);
        pageNoCbx.setItems(pagesList);
        barNoCbx.setItems(barsList);
        beatNoCbx.setItems(beatsList);
        participantsTableView.setItems(participants);
        tempoModifierChob.setItems(tempoMultipliers);
        randomStrategyChob.setItems(randomisationStrategies);
        presetsChob.setItems(presets);
        usePageRandomisationChb.setSelected(true);
        useContinousPageChb.setSelected(false);
        useDynamicsOverlayChb.setSelected(false);
        useDynamicsLineChb.setSelected(false);
        sendToAllChb.setSelected(true);
        usePressureOverlayChb.setSelected(false);
        usePressureLineChb.setSelected(false);
        useSpeedOverlayChb.setSelected(false);
        useSpeedLineChb.setSelected(false);
        usePositionOverlayChb.setSelected(false);
        usePositionLineChb.setSelected(false);
        useContentOverlayChb.setSelected(false);
        useContentLineChb.setSelected(false);

        participants.addListener((ListChangeListener<Participant>) p -> {
            while (p.next()) {
                if (p.wasUpdated()) {
                    if (participants.get(p.getFrom()).getSelect()) {
                        selectedParticipants.add(participants.get(p.getFrom()));
                    } else {
                        selectedParticipants.remove(participants.get(p.getFrom()));
                    }
                } else if (p.wasRemoved()) {
                    for (Participant removed : p.getRemoved()) {
                        selectedParticipants.remove(removed);
                    }
                }
            }
        });

        tempoMultipliers.addAll(getTempoMultiplierValues());
        tempoModifierChob.getSelectionModel().select(Consts.ONE_D);
        tempoModifierChob.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            Double out  = newSelection;
            if (newSelection == null) {
                out = oldSelection;
            }

            onTempoModifierChobChange(out);
        });
        tempoModifierSldr.valueProperty().addListener((ov, old_val, new_val) -> {
//            LOG.debug("old_val: {}, new_val: {}", old_val, new_val);
            onTempoModifierChobChange(new_val.doubleValue());
        });

        randomisationStrategies.addAll(getPageRandomisationStrategisValues());
        randomStrategyChob.getSelectionModel().select(Consts.RND_STRATEGY_2);
        randomStrategyChob.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            String out  = newSelection;
            if (newSelection == null) {
                out = oldSelection;
            }

            onRandomStrategyChobChange(out);
        });

        presets.addAll(getPresetValues());
        presetsChob.getSelectionModel().select(Consts.PRESET_ALL_OFF);
        presetsChob.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            String out  = newSelection;
            if (newSelection == null) {
                out = oldSelection;
            }

            onPresetsChobChange(out);
        });

        usePageRandomisationChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUsePageRandomisationChange(newValue));

        useContinousPageChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUseContinuousPageChange(newValue));

        useDynamicsOverlayChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUseDynamicsOverlay(newValue));

        useDynamicsLineChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUseDynamicsLine(newValue));

        usePressureOverlayChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUsePressureOverlay(newValue));

        usePressureLineChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUsePressureLine(newValue));

        useSpeedOverlayChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUseSpeedOverlay(newValue));

        useSpeedLineChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUseSpeedLine(newValue));

        usePositionOverlayChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUsePositionOverlay(newValue));

        usePositionLineChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUsePositionLine(newValue));

        useContentOverlayChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUseContentOverlay(newValue));

        useContentLineChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUseContentLine(newValue));

        dynamicsSldr.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n < 10.0) return "ppp";
                if (n < 25.0) return "pp";
                if (n < 50.0) return "p";
                if (n < 75.0) return "mf";
                if (n < 85.0) return "f";
                if (n < 90.0) return "ff";

                return "fff";
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "ppp":
                        return 10d;
                    case "pp":
                        return 25d;
                    case "p":
                        return 35d;
                    case "mf":
                        return 50d;
                    case "f":
                        return 75d;
                    case "ff":
                        return 85d;
                    case "fff":
                        return 100d;

                    default:
                        return 50d;
                }
            }
        });

        pressureSldr.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n < 20.0) return "light";
                if (n < 40.0) return "low";
                if (n < 60.0) return "ord";
                if (n < 80.0) return "high";
                return "heavy";
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "light":
                        return 0d;
                    case "low":
                        return 30d;
                    case "ord":
                        return 50d;
                    case "high":
                        return 70d;
                    case "heavy":
                        return 100d;
                    default:
                        return 50d;
                }
            }
        });

        speedSldr.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n < 20.0) return "vslow";
                if (n < 40.0) return "slow";
                if (n < 60.0) return "ord";
                if (n < 80.0) return "fast";
                return "vfast";
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "vslow":
                        return 0d;
                    case "slow":
                        return 30d;
                    case "ord":
                        return 50d;
                    case "fast":
                        return 70d;
                    case "vfast":
                        return 100d;
                    default:
                        return 50d;
                }
            }
        });

        positionSldr.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n < 10.0) return "tp";
                if (n < 32.0) return "bb";
                if (n < 38.0) return "ob";
                if (n < 42.0) return "msp";
                if (n < 70.0) return "sp";
                if (n < 80.0) return "ord";
                if (n < 90.0) return "st";
                return "mst";
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "tp":
                        return 0d;
                    case "bb":
                        return 30d;
                    case "ob":
                        return 40d;
                    case "msp":
                        return 45d;
                    case "sp":
                        return 60d;
                    case "ord":
                        return 75d;
                    case "st":
                        return 85d;
                    case "mst":
                        return 100d;
                    default:
                        return 70d;
                }
            }
        });

        dynamicsSldr.valueProperty().addListener((ov, old_val, new_val) -> {
//            LOG.debug("old_val: {}, new_val: {}", old_val, new_val);
            long newVal = Math.round(new_val.doubleValue());
            onDynamicsValueChange(newVal);
            String lblVal = String.valueOf(newVal);
            String out = fixedLengthString(lblVal, 3);
            dynamicsValLbl.setText(out);
        });

        pressureSldr.valueProperty().addListener((ov, old_val, new_val) -> {
//            LOG.debug("old_val: {}, new_val: {}", old_val, new_val);
            long newVal = Math.round(new_val.doubleValue());
            onPressureValueChange(newVal);
            String lblVal = String.valueOf(newVal);
            String out = fixedLengthString(lblVal, 3);
            pressureValLbl.setText(out);
        });

        speedSldr.valueProperty().addListener((ov, old_val, new_val) -> {
//            LOG.debug("old_val: {}, new_val: {}", old_val, new_val);
            long newVal = Math.round(new_val.doubleValue());
            onSpeedValueChange(newVal);
            String lblVal = String.valueOf(newVal);
            String out = fixedLengthString(lblVal, 3);
            speedValLbl.setText(out);
        });

        positionSldr.valueProperty().addListener((ov, old_val, new_val) -> {
//            LOG.debug("old_val: {}, new_val: {}", old_val, new_val);
            long newVal = Math.round(new_val.doubleValue());
            onPositionValueChange(newVal);
            String lblVal = String.valueOf(newVal);
            String out = fixedLengthString(lblVal, 3);
            positionValLbl.setText(out);
        });

        contentSldr.valueProperty().addListener((ov, old_val, new_val) -> {
//            LOG.debug("old_val: {}, new_val: {}", old_val, new_val);
            long newVal = Math.round(new_val.doubleValue());
            onContentValueChange(newVal);
            String lblVal = String.valueOf(newVal);
            String out = fixedLengthString(lblVal, 3);
            contentValLbl.setText(out);
        });

        dynamicsValLbl.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    setDynamicsDefaultValue();
                }
            }
        });

        pressureValLbl.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    setPressureDefaultValue();
                }
            }
        });

        speedValLbl.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    setSpeedDefaultValue();
                }
            }
        });

        positionValLbl.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    setPositionDefaultValue();
                }
            }
        });

        contentValLbl.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    setPitchDefaultValue();
                }
            }
        });

        webscoreInstructions.setLine1(EMPTY);
        webscoreInstructions.setLine2(EMPTY);
        webscoreInstructions.setLine3(EMPTY);
    }

    public void setScoreService(ScoreService scoreService) {
        this.scoreService = scoreService;
        this.serverAddress = scoreService.getServerAddress();
    }

    public void setPublisher(EventService publisher) {
        this.publisher = publisher;
        this.clock = publisher.getClock();
    }

    @FXML
    private void initialize() {

        participantsTableView.setSelectionModel(null);
        participantsTableView.setEditable(true);

        clientIdColumn.setCellValueFactory(cellData -> cellData.getValue().clientIdProperty());
        hostAddressColumn.setCellValueFactory(cellData -> cellData.getValue().getHostAddressProperty());
        inPortColumn.setCellValueFactory(cellData -> cellData.getValue().getPortInProperty().asObject());
        pingColumn.setCellValueFactory(cellData -> cellData.getValue().getPingProperty().asObject());
        instrumentColumn.setCellValueFactory(cellData -> cellData.getValue().getInstrumentProperty());
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().getSelectProperty());
        expiredColumn.setCellValueFactory(cellData -> cellData.getValue().getLastPingMillisProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setEditable(true);

        isWebClientColumn.setCellValueFactory(cellData -> cellData.getValue().isWebClientProperty());
        isWebClientColumn.setCellFactory(col -> new TableCell<Participant, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item ? Consts.NAME_WEB : Consts.NAME_OSC);
            }
        });

        readyColumn.setCellValueFactory(cellData -> cellData.getValue().readyProperty());
        readyColumn.setCellFactory(col -> new TableCell<Participant, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item ? "Y" : "N");
                if (item == null || empty) {
                    setText(EMPTY);
                    setStyle(EMPTY);
                } else if (item) {
                    setText(Consts.NAME_YES);
                    setStyle(EMPTY);
                    setTextFill(Color.CHOCOLATE);
                } else {
                    setText(Consts.NAME_NO);
                    setTextFill(Color.BLACK);
                    setStyle("-fx-background-color: red");
                }
            }
        });

        bannedColumn.setCellValueFactory(cellData -> cellData.getValue().bannedProperty());
        bannedColumn.setCellFactory(col -> new TableCell<Participant, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item ? Consts.NAME_YES : Consts.NAME_NO);
            }
        });

        instrumentColumn.setCellFactory(column -> {
            return new TableCell<Participant, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(EMPTY);
                        setStyle(EMPTY);
                    } else if (Consts.NAME_NA.equals(item)) {
                        setText(Consts.NAME_NA);
                        setTextFill(Color.BLACK);
                        setStyle("-fx-background-color: red");
                    } else {
                        // Format date.
                        setText(item);
                        setStyle(EMPTY);
                        setTextFill(Color.CHOCOLATE);
                    }
                }
            };
        });

        expiredColumn.setCellFactory(column -> {
            return new TableCell<Participant, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    TableRow<Participant> currentRow = getTableRow();

                    Participant participant = currentRow.getItem();
                    if (participant == null) {
                        return;
                    }

                    if (participant.getExpired()) {
                        currentRow.setStyle("-fx-background-color:red");
                        setText(item);
                    } else {
                        currentRow.setStyle("-fx-background-color:inherit");
                        setText(EMPTY);
                    }
                }
            };
        });

        webscoreInstructions = new WebscoreInstructions();
        instL1Txt.textProperty().bindBidirectional(webscoreInstructions.line1Property());
        instL2Txt.textProperty().bindBidirectional(webscoreInstructions.line2Property());
        instL3Txt.textProperty().bindBidirectional(webscoreInstructions.line3Property());
    }

    @FXML
    private void openScoreFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Score File");
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        if (file != null) {
            openFile(file);
//            openWebScore(file);
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


        for (Participant participant : participants) {
            sendAddParts(participant, instrumentsCsv);
        }
    }

    @FXML
    private void discoverParticipants(ActionEvent event) {
        if(serverAddress == null) {
            LOG.error("discoverParticipants: server address not available, ignore discover participants");
            return;
        }
        String serverIp = serverAddress.getHostAddress();
        sendServerIpBroadcast(serverIp);
    }

    @FXML
    private void sendPartsToSelectedParticipants(ActionEvent event) {
        String instrumentsCsv = getInstrumentsCsv();

        ObservableList<Participant> selectedParticipants = getSelectedParticipants();
        if (selectedParticipants == null) {
            return;
        }

        for (Participant participant : selectedParticipants) {
            sendAddParts(participant, instrumentsCsv);
        }
    }

    @FXML
    private void disconnectSelectedParticipants(ActionEvent event) {
        ObservableList<Participant> selectedParticipants = getSelectedParticipants();
        if (selectedParticipants == null) {
            return;
        }

        for (Participant participant : selectedParticipants) {
            disconnectParticipant(participant);
        }
    }

    @FXML
    private void banSelectedParticipants(ActionEvent event) {
        ObservableList<Participant> selectedParticipants = getSelectedParticipants();
        if (selectedParticipants == null) {
            return;
        }

        for (Participant participant : selectedParticipants) {
            banParticipant(participant);
        }
    }

    @FXML
    private void setPageNo(ActionEvent event) {
        if (isBarSetCall || isBeatSetCall) {
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

    @FXML
    private void sendWebscoreInstructions(ActionEvent event) {
        webscoreInstructions.setVisible(true);
        publishWebscoreInstructions();
    }

    @FXML
    private void clearWebscoreInstructions(ActionEvent event) {
        webscoreInstructions.setLine1(EMPTY);
        webscoreInstructions.setLine2(EMPTY);
        webscoreInstructions.setLine3(EMPTY);
        webscoreInstructions.setVisible(false);
        publishWebscoreInstructions();
    }

    private void publishWebscoreInstructions() {
        String l1 = validateWebInstruction(webscoreInstructions.getLine1());
        String l2 = validateWebInstruction(webscoreInstructions.getLine2());
        String l3 = validateWebInstruction(webscoreInstructions.getLine3());
        boolean isVisible = webscoreInstructions.getVisible();
        EventFactory eventFactory = publisher.getEventFactory();
        WebAudienceInstructionsEvent instructionsEvent = eventFactory.createWebAudienceInstructionsEvent(l1, l2, l3, isVisible, clock.getSystemTimeMillis());
        publisher.receive(instructionsEvent);
    }

    private String validateWebInstruction(String instruction) {
        if (instruction == null) {
            return EMPTY;
        }
        return instruction;
    }

    private void setPageValue() {
        if (pagesList.isEmpty()) {
            return;
        }
        LOG.info("setPageValue ####");
        Object selected = pageNoCbx.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Integer v = 1;
        if (selected instanceof Integer) {
            v = (Integer) selected;
        } else if (selected instanceof String) {
            v = Integer.valueOf((String) selected);
        }

        int pageNo = v.intValue();
        Collection<Page> pages = score.getPages();
        Page selectedPage = null;
        for (Page page : pages) {
            if (pageNo == page.getPageNo()) {
                selectedPage = page;
                break;
            }
        }
        if (selectedPage == null) {
            return;
        }

        Bar selectedBar = null;
        Integer minBarNo = Integer.MAX_VALUE;
        Collection<Bar> bars = selectedPage.getBars();
        for (Bar bar : bars) {
            if (bar.isUpbeatBar()) {
                continue;
            }
            if (bar.getBarNo() < minBarNo) {
                minBarNo = bar.getBarNo();
                selectedBar = bar;
            }
        }

        if (selectedBar == null) {
            return;
        }
        barNoCbx.getSelectionModel().select(minBarNo);

        Integer minBeatNo = Integer.MAX_VALUE;
        selectedBar.getTimeSignature();
        Collection<Beat> beats = selectedBar.getBeats();
        beatsList.clear();
        for (Beat beat : beats) {
            if (beat.isUpbeat()) {
                continue;
            }
            int beatNo = beat.getBeatNo();
            if (beatNo < minBeatNo) {
                minBeatNo = beatNo;
            }
            if (!beatsList.contains(beatNo)) {
                beatsList.add(beatNo);
            }
        }
        FXCollections.sort(beatsList);
        beatNoCbx.getSelectionModel().select(minBeatNo);
    }

    @FXML
    private void setBarNo(ActionEvent event) {
        if (isPageSetCall || isBeatSetCall) {
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

        if (barsList.isEmpty()) {
            return;
        }

        Object selected = barNoCbx.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Integer v = 1;
        if (selected instanceof Integer) {
            v = (Integer) selected;
        } else if (selected instanceof String) {
            v = Integer.valueOf((String) selected);
        }

        int barNo = v.intValue();
        Collection<Bar> bars = score.getBars();
        Bar selectedBar = null;
        List<Bar> barsWithNo = new ArrayList<>();
        for (Bar bar : bars) {
            if (bar.isUpbeatBar()) {
                continue;
            }
            if (barNo == bar.getBarNo()) {
                barsWithNo.add(bar);
            }
        }

        PageId minPageId = null;
        for (Bar bar : barsWithNo) {
            Id pid = bar.getPageId();
            if (pid == null) {
                continue;
            }

            PageId pageId = (PageId) pid;
            if (minPageId == null) {
                minPageId = pageId;
                selectedBar = bar;
                continue;
            }

            if (pageId.getPageNo() < minPageId.getPageNo()) {
                minPageId = pageId;
                selectedBar = bar;
            }
        }

        if (selectedBar == null) {
            return;
        }

        Integer pno = minPageId.getPageNo();
        pageNoCbx.getSelectionModel().select(pno);

        Integer minBeatNo = Integer.MAX_VALUE;
        Collection<Beat> beats = selectedBar.getBeats();
        beatsList.clear();
        for (Beat beat : beats) {
            int beatNo = beat.getBeatNo();
            if (beatNo < minBeatNo) {
                minBeatNo = beatNo;
            }
            if (!beatsList.contains(beatNo)) {
                beatsList.add(beatNo);
            }
        }
        beatNoCbx.getSelectionModel().select(minBeatNo);
        FXCollections.sort(beatsList);

    }

    @FXML
    private void setBeatNo(ActionEvent event) {
        if (isPageSetCall || isBarSetCall) {
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

        if (beatsList.isEmpty()) {
            return;
        }

        Object selected = beatNoCbx.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Integer v = 1;
        if (selected instanceof Integer) {
            v = (Integer) selected;
        } else if (selected instanceof String) {
            v = Integer.valueOf((String) selected);
        }

        int beatNo = v.intValue();

        Collection<Beat> beats = score.getBeats();
        List<Beat> beatsWithNo = new ArrayList<>();
        for (Beat beat : beats) {
            if (beat.isUpbeat()) {
                continue;
            }
            if (beatNo == beat.getBeatNo()) {
                beatsWithNo.add(beat);
            }
        }

        BarId selectedBarId = null;
        Integer minBarId = 0;
        for (Beat beat : beatsWithNo) {
            Id bid = beat.getBarId();
            if (bid == null) {
                continue;
            }
            BarId barId = (BarId) bid;
            if (selectedBarId == null) {
                selectedBarId = barId;
                minBarId = selectedBarId.getBarNo();
                continue;
            }

            if (barId.getBarNo() < selectedBarId.getBarNo()) {
                selectedBarId = barId;
                minBarId = selectedBarId.getBarNo();
            }
        }

        if (selectedBarId == null) {
            return;
        }
        barNoCbx.getSelectionModel().select(minBarId);

        Id pid = selectedBarId.getPageId();
        if (pid == null) {
            return;
        }

        PageId pageId = (PageId) pid;
        Integer minPageNo = pageId.getPageNo();
        pageNoCbx.getSelectionModel().select(minPageNo);
    }

    public void sendPosition(){
        sendPosition(null);
    }

    @FXML
    private void sendPosition(ActionEvent event) {
        if (beatNoCbx == null || beatNoCbx.getSelectionModel() == null || beatNoCbx.getSelectionModel().isEmpty()) {
            return;
        }
        presetsChob.getSelectionModel().select(Consts.PRESET_ALL_ON);

        Object selected = beatNoCbx.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Integer v = 1;
        if (selected instanceof Integer) {
            v = (Integer) selected;
        } else if (selected instanceof String) {
            v = Integer.valueOf((String) selected);
        }

        int beatNo = v;

        Collection<Beat> beats = score.getBeats();
        List<Beat> beatsWithNo = new ArrayList<>();
        for (Beat beat : beats) {
            if (beat.isUpbeat()) {
                continue;
            }
            if (beatNo == beat.getBeatNo()) {
                beatsWithNo.add(beat);
            }
        }


        if (beatsWithNo.isEmpty()) {
            mainApp.showDialog("Score Controller", Alert.AlertType.ERROR, "Send Position Error", "Failed to find beat: " + beatNo);
            return;
        }

        long minMillis = Long.MAX_VALUE;
        for (Beat beat : beatsWithNo) {
            long startTime = beat.getStartTimeMillis();
            if (startTime < minMillis) {
                minMillis = startTime;
            }
        }

        if (minMillis == Long.MAX_VALUE) {
            mainApp.showDialog("Score Controller", Alert.AlertType.ERROR, "Send Position Error", "Failed to find populate millis for beat: " + beatNo);
            return;
        }

        positionMillis = minMillis;
        scoreService.setPosition(positionMillis);
        updateOverlays();
    }

    private String getInstrumentsCsv() {
        StringBuilder sb = new StringBuilder();
        for (String instrumentName : instrumentsList) {
            sb.append(instrumentName);
            sb.append(Consts.COMMA);
        }

        String instrumentCsv = sb.toString();
        return Util.removeEndComma(instrumentCsv);
    }

    private void sendAddParts(Participant participant, String instrumentsCsv) {
        if (participant == null || instrumentsCsv == null || instrumentsCsv.length() < 1 || participant.getIsWebClient()) {
            return;
        }
        EventFactory eventFactory = publisher.getEventFactory();
        String destination = NetUtil.createClientId(participant.getHostAddress(), participant.getPortIn());
        AddPartsEvent addPartsEvent = eventFactory.createAddPartsEvent(instrumentsCsv, destination, clock.getSystemTimeMillis());
        publisher.publish(addPartsEvent);
    }

    private void disconnectParticipant(Participant participant) {
        if (participant == null) {
            return;
        }
        if (!participant.getIsWebClient()) {
            return;
        }
        String clientId = NetUtil.createClientId(participant.getHostAddress(), participant.getPortIn());
        List<String> connectionIds = Collections.singletonList(clientId);
        scoreService.closeScoreConnections(connectionIds);
    }

    private void banParticipant(Participant participant) {
        if (participant == null) {
            return;
        }
        if (!participant.getIsWebClient()) {
            return;
        }
        participant.setBanned(true);
        String clientId = NetUtil.createClientId(participant.getHostAddress(), participant.getPortIn());
        scoreService.banConnection(clientId);
//        disconnectParticipant(participant);
    }

    private void sendServerIpBroadcast(String serverIp) {
        if (serverIp == null) {
            return;
        }
        EventFactory eventFactory = publisher.getEventFactory();
        SendServerIpBroadcastEvent serverIpBroadcastEvent = eventFactory.createServerIpBroadcastEvent(serverIp, Consts.BROADCAST, clock.getSystemTimeMillis());
        publisher.publish(serverIpBroadcastEvent);
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
        if (!scoreService.reset()) {
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
        for (Participant participant : participants) {
            participant.setInstrument(Consts.NAME_NA);
        }

    }

    public void viewScore() {
        if (score == null) {
            return;
        }

        String name = score.getName();
        scoreNameLbl.setText(name);

        Collection<Instrument> instruments = score.getInstruments();
        if (instruments == null) {
            return;
        }
        Iterator<Instrument> instIter = instruments.iterator();
        while (instIter.hasNext()) {
            Instrument instrument = instIter.next();
            instrumentsList.add(instrument.getName());
            instrumentNameId.put(instrument.getName(), instrument.getId());
        }

        pagesList.add(Consts.ONE_I);
        Collection<Page> pages = score.getPages();
        for (Page page : pages) {
            int pageNo = page.getPageNo();
            if (!pagesList.contains(pageNo)) {
                pagesList.add(pageNo);
            }
        }
        FXCollections.sort(pagesList);
        pageNoCbx.getSelectionModel().select(Consts.ONE_I);

        barsList.add(Consts.ONE_I);
        Collection<Bar> bars = score.getBars();
        for (Bar bar : bars) {
            if (bar.isUpbeatBar()) {
                continue;
            }
            int barNo = bar.getBarNo();
            if (!barsList.contains(barNo)) {
                barsList.add(barNo);
            }
        }
        FXCollections.sort(barsList);
        barNoCbx.getSelectionModel().select(Consts.ONE_I);

        beatsList.add(Consts.ONE_I);
        Collection<Beat> beats = score.getBeats();
        for (Beat beat : beats) {
            if (beat.isUpbeat()) {
                continue;
            }
            int beatNo = beat.getBeatNo();
            if (!beatsList.contains(beatNo)) {
                beatsList.add(beatNo);
            }
        }
        FXCollections.sort(beatsList);
        beatNoCbx.getSelectionModel().select(Consts.ONE_I);

        usePageRandomisationChb.setSelected(true);
        useContinousPageChb.setSelected(false);

        resetOverlays();
        tempoModifierSldr.setValue(1.0);

        if(mainApp != null) {
            mainApp.onScoreLoad(score);
        }
    }

    public void resetOverlays() {
        setDynamicsDefaultValue();
        setPressureDefaultValue();
        setSpeedDefaultValue();
        setPositionDefaultValue();
        setPitchDefaultValue();
        presetsChob.getSelectionModel().select(Consts.PRESET_ALL_OFF);
    }

    public void setDynamicsDefaultValue() {
        dynamicsSldr.setValue(50.0);
    }

    public void setPressureDefaultValue() {
        pressureSldr.setValue(50.0);
    }

    public void setSpeedDefaultValue() {
        speedSldr.setValue(50.0);
    }

    public void setPositionDefaultValue() {
        positionSldr.setValue(72.0);
    }

    public void setPitchDefaultValue() {
        contentSldr.setValue(50.0);
    }

    private void updateBeatInfo(Id transportId, int pageNo, int barNo, int beatNo, int baseBeatNo) {
        if (this.score == null) {
            return;
        }

        pageNoLbl.setText(String.valueOf(pageNo));
        barNoLbl.setText(String.valueOf(barNo));
        beatNoLbl.setText(String.valueOf(beatNo));
    }

    private void updateTempo(Id transportId, int tempo) {
        if (this.score == null) {
            return;
        }
        tempoLbl.setText(String.valueOf(tempo));
    }

    public void onTransportBeatEvent(Id transportId, int beatNo, int baseBeatNo) {
        List<BeatId> beatIds = score.findBeatIds(transportId, baseBeatNo);
        if (beatIds == null) {
            LOG.info("BeatIds are NULL");
            return;
        }
        int pageNo = 0;
        int barNo = 0;
        for (BeatId beatId : beatIds) {
            if (beatId.getBaseBeatNo() == baseBeatNo) {
                PageId pageId = (PageId) beatId.getPageId();
                pageNo = pageId.getPageNo();
                BarId barId = (BarId) beatId.getBarId();
                barNo = barId.getBarNo();
                break;
            }
        }

        Platform.runLater(new TransportBeatUpdater(transportId, pageNo, barNo, beatNo, baseBeatNo));
    }

    public void onTransportPoisitionChange(Id transportId, int baseBeatNo) {
        List<BeatId> beatIds = score.findBeatIds(transportId, baseBeatNo);
        if (beatIds == null) {
            LOG.info("BeatIds are NULL");
            return;
        }
        int pageNo = 0;
        int barNo = 0;
        int beatNo = 0;
        for (BeatId beatId : beatIds) {
            if (beatId.getBaseBeatNo() == baseBeatNo) {
                PageId pageId = (PageId) beatId.getPageId();
                pageNo = pageId.getPageNo();
                BarId barId = (BarId) beatId.getBarId();
                barNo = barId.getBarNo();
                beatNo = beatId.getBeatNo();
                break;
            }
        }

        Platform.runLater(new TransportBeatUpdater(transportId, pageNo, barNo, beatNo, baseBeatNo));
    }

    public void onTempoEvent(Id transportId, Tempo tempo) {
        Platform.runLater(new TempoUpdater(transportId, tempo.getBpm()));
    }

    private Double[] getTempoMultiplierValues() {
        return Consts.TEMPO_MULTIPLIERS;
    }

    private String[] getPageRandomisationStrategisValues() {
        return Consts.RANDOMISATION_STRATEGIES;
    }

    private String[] getPresetValues() {
        return Consts.PRESETS;
    }

    private void onTempoModifierChobChange(Double newSelection) {
        if (score == null) {
            return;
        }
        LOG.info("Have new Modifier selection: " + newSelection);
        TempoModifier tempoModifier = new TempoModifier(newSelection);
        Collection<Id> transportIds = score.getTransportIds();
        if (transportIds == null) {
            return;
        }

        for (Id transportId : transportIds) {
            scoreService.setTempoModifier(transportId, tempoModifier);
        }
    }

    private void onRandomStrategyChobChange(String newSelection) {
        if (score == null) {
            return;
        }
        LOG.info("Have new Random Strategy selection: " + newSelection);

        List<Integer> randomisationStrategy = new ArrayList<>();
        try {
            String[] instNos = newSelection.split(Consts.COMMA);
            for (String instNo : instNos) {
                randomisationStrategy.add(Integer.parseInt(instNo));
            }
        } catch (Exception e) {
            LOG.error("Failed to process randomisation strategies selection: {}", newSelection, e);
        }

        scoreService.setRandomisationStrategy(randomisationStrategy);

    }

    private void onPresetsChobChange(String newSelection) {
        LOG.info("Have new preset selection: " + newSelection);

        switch (newSelection) {
            case Consts.PRESET_ALL_OFF:
                onUseAllLines(false);
                onUseAllOverlays(false);
                break;
            case Consts.PRESET_ALL_ON:
                onUseAllOverlays(true);
                onUseAllLines(false);
                break;
            case Consts.PRESET_ALL_LINES_ON:
                onUseAllOverlays(true);
                onUseAllLines(true);
                break;
            case Consts.PRESET_ALL_OFF_CONTENT_ON:
                onUseContentOnly(true);
                break;
            case Consts.PRESET_ALL_ON_CONTENT_OFF:
                onUseContentOnly(false);
                break;
            case Consts.PRESET_ALL_ON_CONTENT_POSITION_OFF:
                onUseContentAndPositionOnly(false);
                break;
            default:
                LOG.error("Unknown preset selection {}", newSelection);
        }
    }

    private void onUsePageRandomisationChange(Boolean newValue) {
        scoreService.usePageRandomisation(newValue);
    }

    private void onUseContinuousPageChange(Boolean newValue) {
        scoreService.useContinuousPageChange(newValue);
    }

    private void updateOverlays() {
        Platform.runLater(() -> {
            presetsChob.getSelectionModel().select(Consts.PRESET_ALL_OFF);
        });
    }

    private void onUseAllOverlays(Boolean newValue) {
        Platform.runLater(() -> {
            useDynamicsOverlayChb.setSelected(newValue);
            usePressureOverlayChb.setSelected(newValue);
            useSpeedOverlayChb.setSelected(newValue);
            usePositionOverlayChb.setSelected(newValue);
            useContentOverlayChb.setSelected(newValue);
        });
    }

    private void onUseAllLines(Boolean newValue) {
        Platform.runLater(() -> {
            useDynamicsLineChb.setSelected(newValue);
            usePressureLineChb.setSelected(newValue);
            useSpeedLineChb.setSelected(newValue);
            usePositionLineChb.setSelected(newValue);
            useContentLineChb.setSelected(newValue);
        });
    }

    private void onUseContentOnly(Boolean newValue) {
        Platform.runLater(() -> {
            if (newValue) {
                useDynamicsLineChb.setSelected(false);
                usePressureLineChb.setSelected(false);
                useSpeedLineChb.setSelected(false);
                usePositionLineChb.setSelected(false);
                useContentLineChb.setSelected(true);

                useDynamicsOverlayChb.setSelected(false);
                usePressureOverlayChb.setSelected(false);
                useSpeedOverlayChb.setSelected(false);
                usePositionOverlayChb.setSelected(false);
                useContentOverlayChb.setSelected(true);
            } else {
                useDynamicsLineChb.setSelected(true);
                usePressureLineChb.setSelected(true);
                useSpeedLineChb.setSelected(true);
                usePositionLineChb.setSelected(true);
                useContentLineChb.setSelected(false);

                useDynamicsOverlayChb.setSelected(true);
                usePressureOverlayChb.setSelected(true);
                useSpeedOverlayChb.setSelected(true);
                usePositionOverlayChb.setSelected(true);
                useContentOverlayChb.setSelected(false);
            }
        });
    }

    private void onUseContentAndPositionOnly(Boolean newValue) {
        Platform.runLater(() -> {
            if (newValue) {
                useDynamicsLineChb.setSelected(false);
                usePressureLineChb.setSelected(false);
                useSpeedLineChb.setSelected(false);
                usePositionLineChb.setSelected(true);
                useContentLineChb.setSelected(true);

                useDynamicsOverlayChb.setSelected(false);
                usePressureOverlayChb.setSelected(false);
                useSpeedOverlayChb.setSelected(false);
                usePositionOverlayChb.setSelected(true);
                useContentOverlayChb.setSelected(true);
            } else {
                useDynamicsLineChb.setSelected(true);
                usePressureLineChb.setSelected(true);
                useSpeedLineChb.setSelected(true);
                usePositionLineChb.setSelected(false);
                useContentLineChb.setSelected(false);

                useDynamicsOverlayChb.setSelected(true);
                usePressureOverlayChb.setSelected(true);
                useSpeedOverlayChb.setSelected(true);
                usePositionOverlayChb.setSelected(false);
                useContentOverlayChb.setSelected(false);
            }
        });
    }

    private void onUseDynamicsOverlay(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(!newValue) {
            useDynamicsLineChb.setSelected(false);
        }
        sendDynamicsValueChange(Math.round(dynamicsSldr.getValue()), instrumentIds);
        sendUseDynamicsOverlay(newValue, instrumentIds);
    }

    private void onUseDynamicsLine(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(newValue && !useDynamicsOverlayChb.isSelected()) {
            useDynamicsOverlayChb.setSelected(true);
        }
        sendDynamicsValueChange(Math.round(dynamicsSldr.getValue()), instrumentIds);
        sendUseDynamicsLine(newValue, instrumentIds);
    }

    private void onDynamicsValueChange(long newVal) {
        if(!useDynamicsOverlayChb.isSelected()) {
            return;
        }
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        sendDynamicsValueChange(newVal, instrumentIds);
    }

    private void onUsePressureOverlay(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(!newValue) {
            usePressureLineChb.setSelected(false);
        }
        sendPressureValueChange(Math.round(pressureSldr.getValue()), instrumentIds);
        sendUsePressureOverlay(newValue, instrumentIds);
    }

    private void onUsePressureLine(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(newValue && !usePressureOverlayChb.isSelected()) {
            usePressureOverlayChb.setSelected(true);
        }
        sendPressureValueChange(Math.round(pressureSldr.getValue()), instrumentIds);
        sendUsePressureLine(newValue, instrumentIds);
    }

    private void onPressureValueChange(long newVal) {
        if(!usePressureOverlayChb.isSelected()) {
            return;
        }
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        sendPressureValueChange(newVal, instrumentIds);
    }

    private void onUseSpeedOverlay(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(!newValue) {
            useSpeedLineChb.setSelected(false);
        }
        sendSpeedValueChange(Math.round(speedSldr.getValue()), instrumentIds);
        sendUseSpeedOverlay(newValue, instrumentIds);
    }

    private void onUseSpeedLine(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(newValue && !useSpeedOverlayChb.isSelected()) {
            useSpeedOverlayChb.setSelected(true);
        }
        sendSpeedValueChange(Math.round(speedSldr.getValue()), instrumentIds);
        sendUseSpeedLine(newValue, instrumentIds);
    }

    private void onSpeedValueChange(long newVal) {
        if(!useSpeedOverlayChb.isSelected()) {
            return;
        }
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        sendSpeedValueChange(newVal, instrumentIds);
    }

    private void onUsePositionOverlay(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(!newValue) {
            usePositionLineChb.setSelected(false);
        }
        sendPositionValueChange(Math.round(positionSldr.getValue()), instrumentIds);
        sendUsePositionOverlay(newValue, instrumentIds);
    }

    private void onUsePositionLine(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(newValue && !usePositionOverlayChb.isSelected()) {
            usePositionOverlayChb.setSelected(true);
        }
        sendPositionValueChange(Math.round(positionSldr.getValue()), instrumentIds);
        sendUsePositionLine(newValue, instrumentIds);
    }

    private void onPositionValueChange(long newVal) {
        if(!usePositionOverlayChb.isSelected()) {
            return;
        }
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        sendPositionValueChange(newVal, instrumentIds);
    }

    private void onUseContentOverlay(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(!newValue) {
            useContentLineChb.setSelected(false);
        }
        sendContentValueChange(Math.round(contentSldr.getValue()), instrumentIds);
        sendUseContentOverlay(newValue, instrumentIds);
    }

    private void onUseContentLine(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(newValue && !useContentOverlayChb.isSelected()) {
            useContentOverlayChb.setSelected(true);
        }
        sendContentValueChange(Math.round(contentSldr.getValue()), instrumentIds);
        sendUseContentLine(newValue, instrumentIds);
    }

    private void onContentValueChange(long newVal) {
        if(!useContentOverlayChb.isSelected()) {
            return;
        }
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        sendContentValueChange(newVal, instrumentIds);
    }

    private List<Id> getInstrumentsToSend() {
        List<Id> instrumentIds;
        if(sendToAllChb.isSelected()) {
            instrumentIds = getAllInstruments();
        } else {
            instrumentIds = getSelectedInstruments();
        }
        return instrumentIds;
    }

    public void sendUseDynamicsOverlay(Boolean newVal, List<Id> instrumentIds) {
        scoreService.onUseOverlay(OverlayType.DYNAMICS, newVal, instrumentIds);
    }

    public void sendUseDynamicsLine(Boolean newVal, List<Id> instrumentIds) {
        scoreService.onUseOverlayLine(OverlayType.DYNAMICS, newVal, instrumentIds);
    }

    public void sendDynamicsValueChange(long newVal, List<Id> instrumentIds) {
        scoreService.setOverlayValue(OverlayType.DYNAMICS, newVal, instrumentIds);
    }

    private void sendUsePressureOverlay(Boolean newVal, List<Id> instrumentIds) {
        scoreService.onUseOverlay(OverlayType.PRESSURE, newVal, instrumentIds);
    }

    private void sendUsePressureLine(Boolean newVal, List<Id> instrumentIds) {
        scoreService.onUseOverlayLine(OverlayType.PRESSURE, newVal, instrumentIds);
    }

    private void sendPressureValueChange(long newVal, List<Id> instrumentIds) {
        scoreService.setOverlayValue(OverlayType.PRESSURE, newVal, instrumentIds);
    }

    private void sendUseSpeedOverlay(Boolean newVal, List<Id> instrumentIds) {
        scoreService.onUseOverlay(OverlayType.SPEED, newVal, instrumentIds);
    }

    private void sendUseSpeedLine(Boolean newVal, List<Id> instrumentIds) {
        scoreService.onUseOverlayLine(OverlayType.SPEED, newVal, instrumentIds);
    }

    private void sendSpeedValueChange(long newVal, List<Id> instrumentIds) {
        scoreService.setOverlayValue(OverlayType.SPEED, newVal, instrumentIds);
    }

    private void sendUsePositionOverlay(Boolean newVal, List<Id> instrumentIds) {
        scoreService.onUseOverlay(OverlayType.POSITION, newVal, instrumentIds);
    }

    private void sendUsePositionLine(Boolean newVal, List<Id> instrumentIds) {
        scoreService.onUseOverlayLine(OverlayType.POSITION, newVal, instrumentIds);
    }

    private void sendPositionValueChange(long newVal, List<Id> instrumentIds) {
        scoreService.setOverlayValue(OverlayType.POSITION, newVal, instrumentIds);
    }

    public void sendUseContentOverlay(Boolean newVal, List<Id> instrumentIds) {
        scoreService.onUseOverlay(OverlayType.PITCH, newVal, instrumentIds);
    }

    public void sendUsePitchStaveOverlay(Boolean newValue, List<Id> instrumentIds) {
        scoreService.onUseOverlay(OverlayType.PITCH_STAVE, newValue, instrumentIds);
    }

    public void sendUseContentLine(Boolean newVal, List<Id> instrumentIds) {
        scoreService.onUseOverlayLine(OverlayType.PITCH, newVal, instrumentIds);
    }

    public void sendContentValueChange(long newVal, List<Id> instrumentIds) {
        scoreService.setOverlayValue(OverlayType.PITCH, newVal, instrumentIds);
    }

    public void sendTimbreValueChange(long value, List<Id> instrumentIds) {
        scoreService.setOverlayValue(OverlayType.TIMBRE, value, instrumentIds);
    }

    public void sendUseTimbreOverlay(Boolean newValue, List<Id> instrumentIds) {
        scoreService.onUseOverlay(OverlayType.TIMBRE, newValue, instrumentIds);
    }

    public void sendUseTimbreLine(Boolean newValue, List<Id> instrumentIds) {
        scoreService.onUseOverlayLine(OverlayType.TIMBRE, newValue, instrumentIds);
    }

    public List<Id> getSelectedInstruments() {
        List<Id> instrumentIds = new ArrayList<>();
        ObservableList<Participant> participants = getSelectedParticipants();
        if(participants == null) {
            return instrumentIds;
        }
        for(Participant participant : participants) {
            Id instrumentId = instrumentNameId.get(participant.getInstrument());
            if(instrumentId == null) {
                LOG.info("Could not find ID for participant instrument {}", participant.getInstrument());
                continue;
            }
            instrumentIds.add(instrumentId);
        }
        return  instrumentIds;
    }

    public List<Id> getAllInstruments() {
        List<Id> instrumentIds = new ArrayList<>();
        ObservableList<Participant> participants = getAllParticipants();
        if(participants == null) {
            return instrumentIds;
        }
        for(Participant participant : participants) {
            if(Consts.NAME_FULL_SCORE.equalsIgnoreCase(participant.getInstrument())) {
                continue;
            }
            Id instrumentId = instrumentNameId.get(participant.getInstrument());
            if(instrumentId == null) {
                LOG.info("Could not find ID for participant instrument {}", participant.getInstrument());
                continue;
            }
            instrumentIds.add(instrumentId);
        }
        return  instrumentIds;
    }

    private ObservableList<Participant> getSelectedParticipants() {
        return selectedParticipants;
//        return participantsTableView.getSelectionModel().getSelectedItems();
    }

    private ObservableList<Participant> getAllParticipants() {
        return participantsTableView.getItems();
    }

    public static String fixedLengthString(String string, int length) {
        return String.format("%1$" + length + "s", string);
    }

    public String getScoreName() {
        if(score == null) {
            return null;
        }
        return score.getName();
    }

    public void setPage(int startPage) {
        Integer selection = startPage;
        pageNoCbx.getSelectionModel().select(selection);
        setPageNo(null);
    }

    public void playSection() {
        playScore(null);
    }

    public void stopSection() {
        stopScore(null);
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

