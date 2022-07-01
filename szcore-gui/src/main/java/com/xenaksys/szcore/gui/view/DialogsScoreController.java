package com.xenaksys.szcore.gui.view;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.ScoreBuilderStrategy;
import com.xenaksys.szcore.algo.ValueScaler;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.gui.StrategyEvent;
import com.xenaksys.szcore.event.gui.StrategyEventType;
import com.xenaksys.szcore.event.web.audience.WebAudienceAudioEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceAudioEventType;
import com.xenaksys.szcore.event.web.audience.WebAudienceInstructionsEvent;
import com.xenaksys.szcore.gui.SzcoreClient;
import com.xenaksys.szcore.gui.model.AudienceVote;
import com.xenaksys.szcore.gui.model.Participant;
import com.xenaksys.szcore.gui.model.Section;
import com.xenaksys.szcore.gui.model.WebscoreInstructions;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.EventService;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreService;
import com.xenaksys.szcore.model.SectionInfo;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.VoteInfo;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.score.web.audience.AudioComponentType;
import com.xenaksys.szcore.util.MathUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.MAXMSP_CMD_PLAY;
import static com.xenaksys.szcore.Consts.MAXMSP_CMD_SET_FILE;
import static com.xenaksys.szcore.Consts.MAXMSP_GRANULATOR;
import static com.xenaksys.szcore.Consts.MAXMSP_GRANULATOR_CONT;
import static com.xenaksys.szcore.Consts.MAXMSP_GRANULATOR_CONT_STOP;
import static com.xenaksys.szcore.Consts.MAXMSP_GROOVE;
import static com.xenaksys.szcore.Consts.MAXMSP_GROOVE_CONT;
import static com.xenaksys.szcore.Consts.MAXMSP_GROOVE_CONT_STOP;
import static com.xenaksys.szcore.Consts.OSC_ADDRESS_ZSCORE;
import static com.xenaksys.szcore.Consts.WEB_AUDIO_ACTION_DURATION_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_DUR_MULTIPLIER;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_FREQ_MULTIPLIER;
import static com.xenaksys.szcore.Consts.WEB_SYNTH;
import static com.xenaksys.szcore.gui.view.ScoreController.fixedLengthString;

public class DialogsScoreController {
    static final Logger LOG = LoggerFactory.getLogger(ScoreController.class);

    public static final String SCORE_NAME = "Dialogs";
    public static final String LABEL_GREEN = "label-green";
    public static final String LABEL_RED = "label-red";

    public static final String PRESENT = "Present";
    public static final String CONCUR = "Concur";
    public static final String DISSENT = "Dissent";
    public static final String ABSTAIN = "Abstain";
    public static final String SCORE = "SCORE";
    public static final String FREE = "FREE";
    public static final String WELCOME = "WELCOME";
    public static final String INTRO = "INTRO";
    public static final String PLAY = "PLAY";
    public static final String END = "END";
    public static final String CURRENT = "CURRENT";
    public static final String PITCH = "PITCH";
    public static final String RHYTHM = "RHYTHM";
    public static final String MELODY = "MELODY";
    public static final String TIMBRE = "TIMBRE";
    public static final String IMPRO = "IMPRO";
    public static final String INVALID_OWNER_PREFIX = "--";
    public static final String MAX_GRANULATOR_ADDR = OSC_ADDRESS_ZSCORE + MAXMSP_GRANULATOR;
    public static final String MAX_GRANULATOR_CONT_ADDR = OSC_ADDRESS_ZSCORE + MAXMSP_GRANULATOR_CONT;
    public static final String MAX_GRANULATOR_CONT_STOP_ADDR = OSC_ADDRESS_ZSCORE + MAXMSP_GRANULATOR_CONT_STOP;
    public static final String MAX_GROOVE_ADDR = OSC_ADDRESS_ZSCORE + MAXMSP_GROOVE;
    public static final String MAX_GROOVE_CONT_ADDR = OSC_ADDRESS_ZSCORE + MAXMSP_GROOVE_CONT;
    public static final String MAX_GROOVE_CONT_STOP_ADDR = OSC_ADDRESS_ZSCORE + MAXMSP_GROOVE_CONT_STOP;

    static final int MIN_VOTER_NO = 10;
    private final ValueScaler transparencyValueScaler = new ValueScaler(0.0, 100.0, 255.0, 0.0);

    @FXML
    private TableView<Section> sectionsTableView;
    @FXML
    private ListView<String> sectionOrderLvw;
    @FXML
    private TableColumn<Section, String> sectionColumn;
    @FXML
    private TableColumn<Section, String> ownerColumn;
    @FXML
    private TableColumn<Section, Integer> startPageColumn;
    @FXML
    private TableColumn<Section, Integer> endPageColumn;
    @FXML
    private TableColumn<Section, Integer> voteNoColumn;
    @FXML
    private Button assignOwnersBtn;
    @FXML
    private Button resetOwnersBtn;
    @FXML
    private Button shuffleOrderBtn;
    @FXML
    private Label sectionsStatusLbl;
    @FXML
    private Button setNextSectionBtn;
    @FXML
    private Button playNextSectionBtn;
    @FXML
    private Button stopNextSectionBtn;
    @FXML
    private Label nextSectionLbl;
    @FXML
    private Label playingSectionLbl;
    @FXML
    private Slider dynamicsSldr;
    @FXML
    private Label dynamicsValLbl;
    @FXML
    private CheckBox useDynamicsOverlayChb;
    @FXML
    private CheckBox useDynamicsLineChb;
    @FXML
    private Slider timbreSldr;
    @FXML
    private Label timbreValLbl;
    @FXML
    private CheckBox useTimbreOverlayChb;
    @FXML
    private CheckBox useTimbreLineChb;
    @FXML
    private Slider pitchSldr;
    @FXML
    private Label pitchValLbl;
    @FXML
    private CheckBox usePitchOverlayChb;
    @FXML
    private CheckBox usePitchLineChb;
    @FXML
    private ChoiceBox<String> presetsChob;
    @FXML
    private CheckBox sendToAllChb;
    @FXML
    private CheckBox usePitchStaveOverlayChb;
    @FXML
    private TextField instPitchLn1Txt;
    @FXML
    private TextField instPitchLn2Txt;
    @FXML
    private TextField instPitchLn3Txt;
    @FXML
    private Button sendPitchTextInstructionsBtn;
    @FXML
    private Button clearPitchTextInstructionsBtn;
    @FXML
    private CheckBox selectAllInstrumentsTxtChb;
    @FXML
    private CheckBox selectAudienceTxtChb;
    @FXML
    private CheckBox selectAllTxtRecipientsChb;
    @FXML
    private CheckBox sendTxtToPresentChb;
    @FXML
    private Label presentL1Lbl;
    @FXML
    private Label presentL2Lbl;
    @FXML
    private Label presentL3Lbl;
    @FXML
    private CheckBox sendTxtToConcurChb;
    @FXML
    private Label concurL1Lbl;
    @FXML
    private Label concurL2Lbl;
    @FXML
    private Label concurL3Lbl;
    @FXML
    private CheckBox sendTxtToDissentChb;
    @FXML
    private Label dissentL1Lbl;
    @FXML
    private Label dissentL2Lbl;
    @FXML
    private Label dissentL3Lbl;
    @FXML
    private CheckBox sendTxtToAbstainChb;
    @FXML
    private Label abstainL1Lbl;
    @FXML
    private Label abstainL2Lbl;
    @FXML
    private Label abstainL3Lbl;
    @FXML
    private CheckBox sendTxtToAudienceChb;
    @FXML
    private Label audienceL1Lbl;
    @FXML
    private Label audienceL2Lbl;
    @FXML
    private Label audienceL3Lbl;
    @FXML
    private Slider pitchOverlayTransparencySldr;
    @FXML
    private Label voteCurrentLbl;
    @FXML
    private Label voteMaxLbl;
    @FXML
    private Label voteAvgLbl;
    @FXML
    private Label voteMinLbl;
    @FXML
    private VBox voteVbox;
    @FXML
    private VBox voteUpVbox;
    @FXML
    private VBox voteDownVbox;
    @FXML
    private Circle semaphore1Crc;
    @FXML
    private Circle semaphore2Crc;
    @FXML
    private Circle semaphore3Crc;
    @FXML
    private Circle semaphore4Crc;
    @FXML
    private CheckBox adncNotesChb;
    @FXML
    private CheckBox adncAudioChb;
    @FXML
    private CheckBox adncThumbsChb;
    @FXML
    private CheckBox adncMeterChb;
    @FXML
    private CheckBox adncVoteChb;
    @FXML
    private Button adncSendViewBtn;
    @FXML
    private RadioButton presetScoreRdb;
    @FXML
    private RadioButton presetFreeRdb;
    @FXML
    private RadioButton presetWelcomeRdb;
    @FXML
    private RadioButton presetIntroRdb;
    @FXML
    private RadioButton presetAudiencePlayRdb;
    @FXML
    private RadioButton presetEndRdb;
    @FXML
    private RadioButton presetImproCurrentRdb;
    @FXML
    private RadioButton presetImproPitchRdb;
    @FXML
    private RadioButton presetImproRhythmRdb;
    @FXML
    private RadioButton presetImproMelodyRdb;
    @FXML
    private RadioButton presetImproTimbreRdb;
    @FXML
    private RadioButton presetImproImproRdb;
    @FXML
    private Button presetSendBtn;
    @FXML
    private CheckBox playAudioOnNewSectionChb;
    @FXML
    private Label synthFreqLbl;
    @FXML
    private Label synthDurLbl;
    @FXML
    private Slider adncMasterVolSldr;
    @FXML
    private Slider adncSynthVolSldr;
    @FXML
    private ChoiceBox<Double> synthFreqChob;
    @FXML
    private ChoiceBox<Double> synthDurChob;

    private SzcoreClient mainApp;
    private EventService publisher;
    private ScoreService scoreService;
    private Clock clock;
    private String currentSection;

    private WebscoreInstructions txtInstructions;
    private WebscoreInstructions presentInstructions;
    private WebscoreInstructions concurInstructions;
    private WebscoreInstructions dissentInstructions;
    private WebscoreInstructions abstainInstructions;
    private WebscoreInstructions audienceInstructions;

    private final InstrumentId presentInstId = new InstrumentId(PRESENT);
    private final InstrumentId concurInstId = new InstrumentId(CONCUR);
    private final InstrumentId abstainInstId = new InstrumentId(ABSTAIN);
    private final InstrumentId dissentInstId = new InstrumentId(DISSENT);

    private final ObservableList<Section> sections = FXCollections.observableArrayList();
    private final ObservableList<String> sectionOrder = FXCollections.observableArrayList();
    private final StringProperty nextSectionProp = new SimpleStringProperty("N/A");
    private final StringProperty playingSectionProp = new SimpleStringProperty("N/A");
    private final ObservableList<String> overlayPresets = FXCollections.observableArrayList();
    private final ObservableList<Double> synthFreq = FXCollections.observableArrayList();
    private final ObservableList<Double> synthDur = FXCollections.observableArrayList();

    private final ToggleGroup presetGroup = new ToggleGroup();
    private Tempo tempo;

    private Circle[] semaphore;

    private final AudienceVote audienceVote = new AudienceVote();

    private double lastSythFreqValue = 0.0;
    private double lastSythDurationValue = 8.0;

    public void setMainApp(SzcoreClient mainApp) {
        this.mainApp = mainApp;
    }

    public void populate() {
        sectionsTableView.setItems(sections);
        sectionOrderLvw.setItems(sectionOrder);

        useDynamicsOverlayChb.setSelected(false);
        useDynamicsLineChb.setSelected(false);
        usePitchOverlayChb.setSelected(false);
        usePitchStaveOverlayChb.setSelected(true);
        usePitchLineChb.setSelected(false);
        useTimbreOverlayChb.setSelected(false);
        useTimbreLineChb.setSelected(false);
        sendToAllChb.setSelected(true);

        usePitchOverlayChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUsePitchOverlay(newValue));
        usePitchStaveOverlayChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUsePitchStaveOverlay(newValue));
        usePitchLineChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUsePitchLine(newValue));
        useDynamicsOverlayChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUseDynamicsOverlay(newValue));
        useDynamicsLineChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUseDynamicsLine(newValue));
        useTimbreOverlayChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUseTimbreOverlay(newValue));
        useTimbreLineChb.selectedProperty().addListener((observable, oldValue, newValue) -> onUseTimbreLine(newValue));

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

        dynamicsSldr.valueProperty().addListener((ov, old_val, new_val) -> {
            long newVal = Math.round(new_val.doubleValue());
            onDynamicsValueChange(newVal);
            String lblVal = String.valueOf(newVal);
            String out = fixedLengthString(lblVal, 3);
            dynamicsValLbl.setText(out);
        });

        pitchSldr.valueProperty().addListener((ov, old_val, new_val) -> {
//            LOG.debug("old_val: {}, new_val: {}", old_val, new_val);
            long newVal = Math.round(new_val.doubleValue());
            onPitchValueChange(newVal);
            String lblVal = String.valueOf(newVal);
            String out = fixedLengthString(lblVal, 3);
            pitchValLbl.setText(out);
        });

        timbreSldr.valueProperty().addListener((ov, old_val, new_val) -> {
//            LOG.debug("old_val: {}, new_val: {}", old_val, new_val);
            long newVal = Math.round(new_val.doubleValue());
            onTimbreValueChange(newVal);
            String lblVal = String.valueOf(newVal);
            String out = fixedLengthString(lblVal, 3);
            timbreValLbl.setText(out);
        });

        timbreValLbl.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    setTimbreDefaultValue();
                }
            }
        });

        dynamicsValLbl.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    setDynamicsDefaultValue();
                }
            }
        });

        pitchValLbl.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    setPitchDefaultValue();
                }
            }
        });

        presetsChob.setItems(overlayPresets);
        overlayPresets.addAll(getOverlayPresetValues());
        presetsChob.getSelectionModel().select(Consts.PRESET_ALL_OFF);
        presetsChob.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            String out = newSelection;
            if (newSelection == null) {
                out = oldSelection;
            }
            onPresetsChobChange(out);
        });

        txtInstructions.setLine1(EMPTY);
        txtInstructions.setLine2(EMPTY);
        txtInstructions.setLine3(EMPTY);

        selectAllInstrumentsTxtChb.setSelected(true);
        selectAudienceTxtChb.setSelected(false);
        pitchOverlayTransparencySldr.setValue(100.0);
        pitchOverlayTransparencySldr.valueProperty().addListener((ov, old_val, new_val) -> {
//            LOG.debug("old_val: {}, new_val: {}", old_val, new_val);
            long newVal = Math.round(new_val.doubleValue());
            onPitchOvrlTransparencyChange(newVal);
        });

        semaphore = new Circle[]{semaphore1Crc, semaphore2Crc, semaphore3Crc, semaphore4Crc};

        presetScoreRdb.setSelected(false);
        presetFreeRdb.setSelected(false);
        presetImproCurrentRdb.setSelected(false);
        presetImproPitchRdb.setSelected(false);
        presetImproRhythmRdb.setSelected(false);
        presetImproMelodyRdb.setSelected(false);
        presetImproTimbreRdb.setSelected(false);
        presetImproImproRdb.setSelected(false);
        presetWelcomeRdb.setSelected(false);
        presetIntroRdb.setSelected(false);
        presetAudiencePlayRdb.setSelected(false);
        presetEndRdb.setSelected(false);

        playAudioOnNewSectionChb.selectedProperty().addListener((observable, oldValue, newValue) -> onPlayAudioOnNewSection(newValue));

        synthFreqChob.setItems(synthFreq);
        synthFreq.addAll(getSynthFreqValues());
        synthFreqChob.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            Double out = newSelection;
            if (newSelection == null) {
                out = oldSelection;
            }
            onSynthFreqChobChange(out);
        });
        synthDurChob.setItems(synthDur);
        synthDur.addAll(getSynthFreqValues());
        synthDurChob.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            Double out = newSelection;
            if (newSelection == null) {
                out = oldSelection;
            }
            onSynthDurChobChange(out);
        });
        synthFreqLbl.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    setSynthFreqDefaultValue();
                }
            }
        });
        setSynthFreqDefaultValue();
        synthDurLbl.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    setSynthDurDefaultValue();
                }
            }
        });
        setSynthDurDefaultValue();

        adncMasterVolSldr.valueProperty().addListener((ov, old_val, new_val) -> {
            long newVal = Math.round(new_val.doubleValue());
            long oldVal = Math.round(old_val.doubleValue());
            onAudienceMasterVolumeChange(newVal, oldVal);
        });

        adncSynthVolSldr.valueProperty().addListener((ov, old_val, new_val) -> {
            long newVal = Math.round(new_val.doubleValue());
            long oldVal = Math.round(old_val.doubleValue());
            onAudienceSynthVolumeChange(newVal, oldVal);
        });
    }

    @FXML
    private void initialize() {
        sectionsTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        sectionsTableView.setEditable(false);

        sectionOrderLvw.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        sectionOrderLvw.setEditable(false);
        sectionOrderLvw.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            String out = newSelection;
            if (newSelection == null) {
                out = oldSelection;
            }

            onSectionSelect(out);
        });

        sectionColumn.setCellValueFactory(cellData -> cellData.getValue().sectionProperty());
        ownerColumn.setCellValueFactory(cellData -> cellData.getValue().ownerProperty());
        startPageColumn.setCellValueFactory(cellData -> cellData.getValue().startPageProperty().asObject());
        endPageColumn.setCellValueFactory(cellData -> cellData.getValue().endPageProperty().asObject());
        voteNoColumn.setCellValueFactory(cellData -> cellData.getValue().avgVoteProperty().asObject());

        ownerColumn.setCellFactory(column -> new TableCell<Section, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                TableRow<Section> currentRow = getTableRow();
                Section section = currentRow.getItem();
                if (section == null) {
                    return;
                }
                if (section.isIsOwnerValid()) {
                    currentRow.setStyle("-fx-background-color:inherit");
                } else {
                    currentRow.setStyle("-fx-background-color:red");
                }
                setText(item);
            }
        });

        nextSectionLbl.textProperty().bind(nextSectionProp);
        playingSectionLbl.textProperty().bind(playingSectionProp);

        txtInstructions = new WebscoreInstructions();
        instPitchLn1Txt.textProperty().bindBidirectional(txtInstructions.line1Property());
        instPitchLn2Txt.textProperty().bindBidirectional(txtInstructions.line2Property());
        instPitchLn3Txt.textProperty().bindBidirectional(txtInstructions.line3Property());

        presentInstructions = new WebscoreInstructions();
        presentL1Lbl.textProperty().bindBidirectional(presentInstructions.line1Property());
        presentL2Lbl.textProperty().bindBidirectional(presentInstructions.line2Property());
        presentL3Lbl.textProperty().bindBidirectional(presentInstructions.line3Property());

        concurInstructions = new WebscoreInstructions();
        concurL1Lbl.textProperty().bindBidirectional(concurInstructions.line1Property());
        concurL2Lbl.textProperty().bindBidirectional(concurInstructions.line2Property());
        concurL3Lbl.textProperty().bindBidirectional(concurInstructions.line3Property());

        dissentInstructions = new WebscoreInstructions();
        dissentL1Lbl.textProperty().bindBidirectional(dissentInstructions.line1Property());
        dissentL2Lbl.textProperty().bindBidirectional(dissentInstructions.line2Property());
        dissentL3Lbl.textProperty().bindBidirectional(dissentInstructions.line3Property());

        abstainInstructions = new WebscoreInstructions();
        abstainL1Lbl.textProperty().bindBidirectional(abstainInstructions.line1Property());
        abstainL2Lbl.textProperty().bindBidirectional(abstainInstructions.line2Property());
        abstainL3Lbl.textProperty().bindBidirectional(abstainInstructions.line3Property());

        audienceInstructions = new WebscoreInstructions();
        audienceL1Lbl.textProperty().bindBidirectional(audienceInstructions.line1Property());
        audienceL2Lbl.textProperty().bindBidirectional(audienceInstructions.line2Property());
        audienceL3Lbl.textProperty().bindBidirectional(audienceInstructions.line3Property());

        selectAllInstrumentsTxtChb.selectedProperty().addListener((observable, oldValue, newValue) -> selectTxtToAllInstruments(newValue));
        selectAudienceTxtChb.selectedProperty().addListener((observable, oldValue, newValue) -> selectTxtToAudience(newValue));
        selectAllTxtRecipientsChb.selectedProperty().addListener((observable, oldValue, newValue) -> selectTxtToAll(newValue));

        voteCurrentLbl.textProperty().bind(audienceVote.voteNoProperty().asString());
        voteMaxLbl.textProperty().bind(audienceVote.maxVoteProperty().asString());
        voteMinLbl.textProperty().bind(audienceVote.minVoteProperty().asString());
        voteAvgLbl.textProperty().bind(audienceVote.avgVoteProperty().asString());

        voteUpVbox.setPrefHeight(0.0);
        voteDownVbox.setPrefHeight(0.0);

        semaphore1Crc.setFill(Color.RED);
        semaphore1Crc.setStroke(Color.BLACK);
        semaphore2Crc.setFill(Color.TRANSPARENT);
        semaphore2Crc.setStroke(Color.BLACK);
        semaphore3Crc.setFill(Color.TRANSPARENT);
        semaphore3Crc.setStroke(Color.BLACK);
        semaphore4Crc.setFill(Color.TRANSPARENT);
        semaphore4Crc.setStroke(Color.BLACK);

        presetScoreRdb.setToggleGroup(presetGroup);
        presetScoreRdb.setUserData(SCORE);
        presetFreeRdb.setToggleGroup(presetGroup);
        presetFreeRdb.setUserData(FREE);
        presetWelcomeRdb.setToggleGroup(presetGroup);
        presetWelcomeRdb.setUserData(WELCOME);
        presetIntroRdb.setToggleGroup(presetGroup);
        presetIntroRdb.setUserData(INTRO);
        presetAudiencePlayRdb.setToggleGroup(presetGroup);
        presetAudiencePlayRdb.setUserData(PLAY);
        presetEndRdb.setToggleGroup(presetGroup);
        presetEndRdb.setUserData(END);
        presetImproCurrentRdb.setToggleGroup(presetGroup);
        presetImproCurrentRdb.setUserData(CURRENT);
        presetImproPitchRdb.setToggleGroup(presetGroup);
        presetImproPitchRdb.setUserData(PITCH);
        presetImproRhythmRdb.setToggleGroup(presetGroup);
        presetImproRhythmRdb.setUserData(RHYTHM);
        presetImproMelodyRdb.setToggleGroup(presetGroup);
        presetImproMelodyRdb.setUserData(MELODY);
        presetImproTimbreRdb.setToggleGroup(presetGroup);
        presetImproTimbreRdb.setUserData(TIMBRE);
        presetImproImproRdb.setToggleGroup(presetGroup);
        presetImproImproRdb.setUserData(IMPRO);

        playAudioOnNewSectionChb.setSelected(true);

        adncMasterVolSldr.setValue(100.0);
        adncSynthVolSldr.setValue(10.0);
    }

    @FXML
    private void sendPitchTextInstructions(ActionEvent event) {
        txtInstructions.setVisible(true);
        publishWebscoreInstructions();
    }

    @FXML
    private void clearPitchTextInstructions(ActionEvent event) {
        txtInstructions.setLine1(EMPTY);
        txtInstructions.setLine2(EMPTY);
        txtInstructions.setLine3(EMPTY);
        txtInstructions.setVisible(false);
        publishWebscoreInstructions();
    }

    @FXML
    private void sendAudienceViewState(ActionEvent event) {
        boolean isNotesEnabled = adncNotesChb.isSelected();
        boolean isAudioEnabled = adncAudioChb.isSelected();
        boolean isThumbsEnabled = adncThumbsChb.isSelected();
        boolean isMeterEnabled = adncMeterChb.isSelected();
        boolean isVoteEnabled = adncVoteChb.isSelected();
        scoreService.publishAudienceViewState(isNotesEnabled, isAudioEnabled, isThumbsEnabled, isMeterEnabled, isVoteEnabled);
    }

    @FXML
    private void sendPreset(ActionEvent event) {
        Toggle selected = presetGroup.getSelectedToggle();
        if(selected == null) {
            return;
        }
        String preset = selected.getUserData().toString();
        LOG.info("sendPreset: {}", preset);
        switch (preset) {
            case SCORE:
                processPresetScore();
                break;
            case FREE:
                processPresetFree();
                break;
            case WELCOME:
                processPresetWelcome();
                break;
            case INTRO:
                processPresetIntro();
                break;
            case PLAY:
                processPresetAudiencePlay();
                break;
            case END:
                processPresetEnd();
                break;
            case CURRENT:
                processPresetImproCurrent();
                break;
            case PITCH:
                processPresetPitch();
                break;
            case RHYTHM:
                processPresetRhythm();
                break;
            case MELODY:
                processPresetMelody();
                break;
            case TIMBRE:
                processPresetTimbre();
                break;
            case IMPRO:
                processPresetImproImpro();
                break;
            default:
                LOG.error("Unkonwn preset: {}", preset);
        }
    }

    private void processPresetScore() {
        txtInstructions.setLine1(EMPTY);
        txtInstructions.setLine2(EMPTY);
        txtInstructions.setLine3(EMPTY);
        txtInstructions.setVisible(false);
        selectAllInstrumentsTxtChb.setSelected(true);
        selectAudienceTxtChb.setSelected(true);
        publishWebscoreInstructions();

        adncNotesChb.setSelected(false);
        adncAudioChb.setSelected(true);
        adncThumbsChb.setSelected(false);
        adncMeterChb.setSelected(true);
        adncVoteChb.setSelected(false);
        sendAudienceViewState(null);
        sendMaxPreset(2);
    }

    private void processPresetFree() {
        txtInstructions.setLine1("Free");
        txtInstructions.setLine2("Improvisation");
        txtInstructions.setLine3(EMPTY);
        txtInstructions.setVisible(true);
        selectAllInstrumentsTxtChb.setSelected(true);
        selectAudienceTxtChb.setSelected(false);
        publishWebscoreInstructions();
    }

    private void processPresetWelcome() {
        adncNotesChb.setSelected(false);
        adncAudioChb.setSelected(false);
        adncThumbsChb.setSelected(false);
        adncMeterChb.setSelected(true);
        adncVoteChb.setSelected(false);
        sendAudienceViewState(null);

        txtInstructions.setLine1("Welcome to");
        txtInstructions.setLine2("Socket Dialogues");
        txtInstructions.setLine3("Workshop");
        txtInstructions.setVisible(true);
        selectAllInstrumentsTxtChb.setSelected(false);
        selectAudienceTxtChb.setSelected(true);
        publishWebscoreInstructions();
    }

    private void processPresetIntro() {
        adncNotesChb.setSelected(false);
        adncAudioChb.setSelected(false);
        adncThumbsChb.setSelected(false);
        adncMeterChb.setSelected(true);
        adncVoteChb.setSelected(false);
        sendAudienceViewState(null);

        txtInstructions.setLine1("Players are now choosing");
        txtInstructions.setLine2("their roles and");
        txtInstructions.setLine3("the order of dialogues");
        txtInstructions.setVisible(true);
        selectAllInstrumentsTxtChb.setSelected(false);
        selectAudienceTxtChb.setSelected(true);
        publishWebscoreInstructions();
    }

    private void processPresetAudiencePlay() {
        adncNotesChb.setSelected(true);
        adncAudioChb.setSelected(true);
        adncThumbsChb.setSelected(false);
        adncMeterChb.setSelected(true);
        adncVoteChb.setSelected(false);
        sendAudienceViewState(null);

        txtInstructions.setLine1("Click note to play");
        txtInstructions.setLine2(EMPTY);
        txtInstructions.setLine3(EMPTY);
        txtInstructions.setVisible(true);
        selectAllInstrumentsTxtChb.setSelected(false);
        selectAudienceTxtChb.setSelected(true);
        publishWebscoreInstructions();
    }

    private void processPresetEnd() {
        adncNotesChb.setSelected(false);
        adncAudioChb.setSelected(false);
        adncThumbsChb.setSelected(false);
        adncMeterChb.setSelected(true);
        adncVoteChb.setSelected(false);
        sendAudienceViewState(null);

        txtInstructions.setLine1("The End");
        txtInstructions.setLine2("Thanks for your participation in");
        txtInstructions.setLine3("Socket Dialogues");
        txtInstructions.setVisible(true);
        selectAllInstrumentsTxtChb.setSelected(false);
        selectAudienceTxtChb.setSelected(true);
        publishWebscoreInstructions();
    }

    private void processPresetImpro() {
        txtInstructions.setLine1("Click note to play");
        txtInstructions.setLine2(EMPTY);
        txtInstructions.setLine3(EMPTY);
        txtInstructions.setVisible(true);
        selectAllInstrumentsTxtChb.setSelected(false);
        selectAudienceTxtChb.setSelected(true);
        publishWebscoreInstructions();

        txtInstructions.setLine1("Free Improvisation");
        txtInstructions.setLine2("Follow audience / MAX");
        txtInstructions.setLine3(EMPTY);
        txtInstructions.setVisible(true);
        selectAllInstrumentsTxtChb.setSelected(true);
        selectAudienceTxtChb.setSelected(false);
        publishWebscoreInstructions();

        adncNotesChb.setSelected(true);
        adncAudioChb.setSelected(true);
        adncThumbsChb.setSelected(false);
        adncMeterChb.setSelected(true);
        adncVoteChb.setSelected(false);
        sendAudienceViewState(null);
        sendMaxPreset(3);
    }

    private void processPresetImproCurrent() {
        processPresetImpro();
    }

    private void onSynthFreqChobChange(Double value) {
        if(value == lastSythFreqValue) {
            return;
        }
        Map<String, Object> overrides = Collections.singletonMap(WEB_CONFIG_FREQ_MULTIPLIER, value);
        sendAudienceSynthConfig(0, overrides);
        lastSythFreqValue = value;
    }

    private void onSynthDurChobChange(Double value) {
        if(value == lastSythDurationValue) {
            return;
        }
        Map<String, Object> overrides = Collections.singletonMap(WEB_CONFIG_DUR_MULTIPLIER, value);
        sendAudienceSynthConfig(0, overrides);
        lastSythDurationValue = value;
    }

    private void sendAudienceConfig(String configName, int presetNo, Map<String, Object> overrides) {
        scoreService.sendAudienceConfig(configName, presetNo, overrides);
    }

    private void sendAudienceSynthConfig(int presetNo, Map<String, Object> overrides) {
        sendAudienceConfig(WEB_SYNTH, presetNo, overrides);
    }

    private void sendAudiencePitchConfig(Map<String, Object> overrides) {
        sendAudienceSynthConfig(1002, overrides);
    }

    private void sendAudienceRhythmConfig(Map<String, Object> overrides) {
        sendAudienceSynthConfig( 1003, overrides);
    }

    private void sendAudienceMelodyConfig(Map<String, Object> overrides) {
        sendAudienceSynthConfig( 1004, overrides);
    }

    private void sendAudienceTimbreConfig(Map<String, Object> overrides) {
        sendAudienceSynthConfig( 1005, overrides);
    }

    private void sendAudienceImproConfig(Map<String, Object> overrides) {
        sendAudienceConfig(WEB_SYNTH, 1006, overrides);
    }

    private void sendMaxPitchFiles() {
        sendMaxFiles("DialogsPitch_b4.wav", "DialogsPitch_b8.wav");
    }

    private void sendMaxRhythmFiles() {
        sendMaxFiles("DialogsRhythm_b2.wav", "DialogsRhythm_b8.wav");
    }

    private void sendMaxMelodyFiles() {
        sendMaxFiles("DialogsMelody_b2.wav", "DialogsMelody_b8.wav");
    }

    private void sendMaxTimbreFiles() {
        sendMaxFiles("DialogsTimbre_b4.wav", "DialogsTimbre_b8.wav");
    }

    private void sendMaxImproFiles() {
        sendMaxFiles("DialogsImpro_b10.wav", "DialogsImpro_b8.wav");
    }

    private void sendMaxFiles(String granulatorFile, String grooveFile) {
        sendMaxEvent(MAX_GRANULATOR_ADDR, Arrays.asList(MAXMSP_CMD_SET_FILE, granulatorFile));
        sendMaxEvent(MAX_GROOVE_ADDR, Arrays.asList(MAXMSP_CMD_SET_FILE, grooveFile));
    }

    private void processPresetPitch() {
        sendMaxPitchFiles();
        processPresetImpro();
        sendAudiencePitchConfig(null);
    }

    private void processPresetRhythm() {
        sendMaxRhythmFiles();
        processPresetImpro();
        sendAudienceRhythmConfig(null);
    }

    private void processPresetMelody() {
        sendMaxMelodyFiles();
        processPresetImpro();
        sendAudienceMelodyConfig(null);
    }

    private void processPresetTimbre() {
        sendMaxTimbreFiles();
        processPresetImpro();
        sendAudienceTimbreConfig(null);
    }

    private void processPresetImproImpro() {
        sendMaxImproFiles();
        processPresetImpro();
        sendAudienceImproConfig(null);
    }

    private void sendMaxPreset(int preset) {
        scoreService.sendMaxPreset(preset);
    }

    private void sendMaxEventWithDelay(String target, List<Object> args, long delay, TimeUnit tu) {
        Runnable task = new MaxSender(target, args);
        mainApp.scheduleTask(task, delay, tu);
    }

    private void sendMaxEvent(String target, List<Object> args) {
        scoreService.sendMaxEvent(target, args);
    }

    private void publishWebscoreInstructions() {
        String l1 = validateWebInstruction(txtInstructions.getLine1());
        String l2 = validateWebInstruction(txtInstructions.getLine2());
        String l3 = validateWebInstruction(txtInstructions.getLine3());
        boolean isVisible = txtInstructions.getVisible();
        List<Id> instrumentIds = getInstrumentsToSendTxt();

        if (!instrumentIds.isEmpty()) {
            if (isVisible) {
                pitchOverlayTransparencySldr.setValue(0);
                if (!usePitchOverlayChb.isSelected()) {
                    usePitchOverlayChb.setSelected(true);
                }
                if (!useDynamicsOverlayChb.isSelected()) {
                    useDynamicsOverlayChb.setSelected(true);
                }
                if (!useTimbreOverlayChb.isSelected()) {
                    useTimbreOverlayChb.setSelected(true);
                }
            } else {
                if (usePitchOverlayChb.isSelected()) {
                    usePitchOverlayChb.setSelected(false);
                }
                if (useDynamicsOverlayChb.isSelected()) {
                    useDynamicsOverlayChb.setSelected(false);
                }
                if (useTimbreOverlayChb.isSelected()) {
                    useTimbreOverlayChb.setSelected(false);
                }
                pitchOverlayTransparencySldr.setValue(100);
            }
            mainApp.sendPitchText(l1, l2, l3, isVisible, instrumentIds);
        }

        boolean isAudienceSet = false;
        if (sendTxtToAudienceChb.isSelected()) {
            EventFactory eventFactory = publisher.getEventFactory();
            WebAudienceInstructionsEvent instructionsEvent = eventFactory.createWebAudienceInstructionsEvent(l1, l2, l3, isVisible, clock.getSystemTimeMillis());
            publisher.receive(instructionsEvent);
            isAudienceSet = true;
        }

        setTxtGuiVals(isAudienceSet, instrumentIds, l1, l2, l3);
    }

    private void onAudienceMasterVolumeChange(long newVal, long oldVal) {
        publishAudienceAudioVolumeChange(AudioComponentType.MASTER, convertAudioVolumeToWeb(newVal));
    }

    private void onAudienceSynthVolumeChange(long newVal, long oldVal) {
        publishAudienceAudioVolumeChange(AudioComponentType.SYNTH, convertAudioVolumeToWeb(newVal));
    }

    private double convertAudioVolumeToWeb(long value) {
        return MathUtil.convertToRange(1.0*value, Consts.WEB_SLIDER_MIN, Consts.WEB_SLIDER_MAX, Consts.WEB_AUDIO_MIN, Consts.WEB_AUDIO_MAX);
    }

    private void publishAudienceAudioVolumeChange(AudioComponentType componentType, double value) {
        EventFactory eventFactory = publisher.getEventFactory();
        WebAudienceAudioEvent instructionsEvent = eventFactory.createWebAudienceAudioEvent(componentType, WebAudienceAudioEventType.VOLUME, value, WEB_AUDIO_ACTION_DURATION_MS, clock.getSystemTimeMillis());
        publisher.receive(instructionsEvent);
    }

    private String validateWebInstruction(String instruction) {
        if (instruction == null) {
            return EMPTY;
        }
        return instruction;
    }

    private void onSectionSelect(String sectionName) {
        Section section = getSection(sectionName);
        if (section == null) {
            return;
        }
        onSectionSelect(section);
    }

    private void playAudio(String sectionName) {
        if(!playAudioOnNewSectionChb.isSelected()) {
            return;
        }
        String name = sectionName.toUpperCase(Locale.ROOT);
        switch (name) {
            case PITCH:
                sendMaxPitchFiles();
                break;
            case RHYTHM:
                sendMaxRhythmFiles();
                break;
            case MELODY:
                sendMaxMelodyFiles();
                break;
            case TIMBRE:
                sendMaxTimbreFiles();
                break;
            case IMPRO:
                sendMaxImproFiles();
                break;
            default:
                LOG.error("playAudio: unknown section: {}", name);
        }
        sendMaxPreset(3);

        long beatSec = 2;
        if(tempo != null) {
            int bpm = tempo.getBpm();
            beatSec = (60 * 1000) / bpm;
        }

        sendMaxEventWithDelay(MAX_GROOVE_CONT_ADDR, Collections.singletonList(MAXMSP_CMD_PLAY), 2*beatSec, TimeUnit.MILLISECONDS);
        sendMaxEventWithDelay(MAX_GRANULATOR_CONT_ADDR, Collections.singletonList(MAXMSP_CMD_PLAY), 4*beatSec, TimeUnit.MILLISECONDS);
    }

    private void onPlayAudioOnNewSection(Boolean newValue) {
        if(!newValue) {
            sendMaxEvent(MAX_GRANULATOR_CONT_STOP_ADDR, Collections.singletonList(MAXMSP_CMD_PLAY));
            sendMaxEvent(MAX_GROOVE_CONT_STOP_ADDR, Collections.singletonList(MAXMSP_CMD_PLAY));
        }
    }

    private void onSectionSelect(Section section) {
        if (section == null) {
            return;
        }
        setCurrentSectionLabel(section.getSection());
    }

    private void setCurrentSectionLabel(String value) {
        if (value == null) {
            value = Consts.NAME_NA;
        }
        String old = playingSectionProp.getValue();
        if (value.equals(old)) {
            return;
        }
        playingSectionProp.setValue(value);
        selectSection(value);
    }

    public void showSemaphore(int lightNo, Color fill) {
        for (int i = 1; i <= lightNo; i++) {
            if(i > semaphore.length) {
                continue;
            }
            Circle circ = semaphore[i -1];
            circ.setFill(fill);
        }
    }

    private void selectSection(String value) {
        Section section = getSection(value);
        if (section == null) {
            return;
        }
        Section selected = sectionsTableView.getSelectionModel().getSelectedItem();
        if (section.equals(selected)) {
            return;
        }
        sectionsTableView.getSelectionModel().select(section);

        String selectedOrder = sectionOrderLvw.getSelectionModel().getSelectedItem();
        if (value.equals(selectedOrder)) {
            return;
        }
        sectionOrderLvw.getSelectionModel().select(value);
    }

    private void setNextSectionLabel(String value) {
        if (value == null) {
            value = Consts.NAME_NA;
        }
        String old = nextSectionProp.getValue();
        if (value.equals(old)) {
            return;
        }
        nextSectionProp.setValue(value);
    }

    public void setScoreService(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    public void setPublisher(EventService publisher) {
        this.publisher = publisher;
        this.clock = publisher.getClock();
    }

    public void onScoreLoad(Score score) {
        if (!(score instanceof BasicScore)) {
            return;
        }
        BasicScore szcore = (BasicScore) score;
        ScoreBuilderStrategy builderStrategy = szcore.getScoreBuilderStrategy();
        if (builderStrategy == null || !builderStrategy.isActive()) {
            return;
        }
        List<String> sectionNames = builderStrategy.getSections();
        if (sectionNames == null || sectionNames.isEmpty()) {
            return;
        }
        List<Section> guiSections = new ArrayList<>();
        for (String section : sectionNames) {
            Section guiSection = new Section();
            guiSection.setSection(section);
            String owner = builderStrategy.getSectionOwner(section);
            if (owner != null) {
                guiSection.setOwner(owner);
            } else {
                guiSection.setOwner(Consts.EMPTY);
            }
            IntRange pageRange = builderStrategy.getSectionPageRange(section);
            if (pageRange != null) {
                guiSection.setStartPage(pageRange.getStart());
                guiSection.setEndPage(pageRange.getEnd());
            }
            guiSections.add(guiSection);
        }
        Platform.runLater(() -> {
            resetOverlays();
            sections.addAll(guiSections);
        });
    }

    public void onSectionInfo(List<SectionInfo> sectionInfos, List<String> sectionOrder, boolean isReady, String currentSection, String nextSection) {
        Section current = null;
        if (sectionInfos != null) {
            for (SectionInfo info : sectionInfos) {
                Section section = new Section();
                section.setSection(info.getSectionId());
                section.setOwner(info.getOwner());
                IntRange pageRange = info.getPageRange();
                if (pageRange != null) {
                    section.setStartPage(pageRange.getStart());
                    section.setEndPage(pageRange.getEnd());
                }
                VoteInfo voteInfo = info.getVoteInfo();
                if (voteInfo != null) {
                    section.setVoteNo(voteInfo.getCurrent());
                    section.setMinVote(voteInfo.getMin());
                    section.setMaxVote(voteInfo.getMax());
                    section.setAvgVote(voteInfo.getAvg());
                    section.setVoterNo(voteInfo.getVoterNo());
                }
                if (currentSection != null && currentSection.equals(section.getSection())) {
                    current = section;
                }
                addSection(section);
            }
        }

        if (isReady) {
            setSectionStatusStyle(Consts.READY, LABEL_GREEN, LABEL_RED);
        } else {
            setSectionStatusStyle(Consts.WAITING, LABEL_RED, LABEL_GREEN);
        }

        if(current != null) {
            processSectionVote(current);
        }

        if (sectionOrder != null && !sectionOrder.isEmpty()) {
            setSectionOrderInfo(sectionOrder, currentSection, nextSection);
        }
    }

    private void setSectionOrderInfo(final List<String> sections, final String cSection, final String nextSection) {
        String prev = this.currentSection;
        this.currentSection = cSection;
        Platform.runLater(() -> {
            String selectedItem = sectionOrderLvw.getSelectionModel().getSelectedItem();
            sectionOrder.clear();
            sectionOrder.addAll(sections);
            if (sections.contains(selectedItem)) {
                sectionOrderLvw.getSelectionModel().select(selectedItem);
            }
            setCurrentSectionLabel(cSection);
            setNextSectionLabel(nextSection);
        });
        if(currentSection != null && !currentSection.equals(prev)) {
            playAudio(cSection);
        }
    }

    private void processSectionVote(Section section) {
        Platform.runLater(() -> {
            audienceVote.setVoteNo(section.getVoteNo());
            audienceVote.setMinVote(section.getMinVote());
            audienceVote.setMaxVote(section.getMaxVote());
            audienceVote.setAvgVote(section.getAvgVote());
            audienceVote.setVoterNo(section.getVoterNo());
            updateVoteBar();
        });
    }

    private void setSectionStatusStyle(final String text, final String style, final String styleToRemove) {
        Platform.runLater(() -> {
            sectionsStatusLbl.setText(text);
            ObservableList<String> styleClass = sectionsStatusLbl.getStyleClass();
            styleClass.remove(styleToRemove);
            if (!styleClass.contains(style)) {
                styleClass.add(style);
            }
        });
    }

    public void addSection(Section section) {
        if (section == null) {
            return;
        }
        boolean isOwnerParticipant = false;
        String owner = section.getOwner();
        ObservableList<Participant> participants = mainApp.getParticipants();
        if(owner != null && !owner.isEmpty()) {
            for (Participant participant : participants) {
                if(owner.equals(participant.getClientId())) {
                    isOwnerParticipant = true;
                    break;
                }
            }
        }

        section.setIsOwnerValid(isOwnerParticipant);

        if (sections.contains(section)) {
            updateSection(section);
            return;
        }
        Platform.runLater(() -> {
            LOG.info("Adding Section: " + section);
            sections.add(section);
        });
    }

    public void updateSection(Section section) {
        Section toUpdate = getSection(section.getSection());
        if (toUpdate == null) {
            return;
        }
        Platform.runLater(() -> {
//            LOG.info("Updating Section: " + toUpdate);
            toUpdate.setSection(section.getSection());
            toUpdate.setOwner(section.getOwner());
            toUpdate.setStartPage(section.getStartPage());
            toUpdate.setEndPage(section.getEndPage());
            toUpdate.setVoteNo(section.getVoteNo());
            toUpdate.setMinVote(section.getMinVote());
            toUpdate.setMaxVote(section.getMaxVote());
            toUpdate.setAvgVote(section.getAvgVote());
            toUpdate.setIsOwnerValid(section.isIsOwnerValid());
        });
    }

    public void updateVoteBar() {
        int voteNo = audienceVote.getVoteNo();
        if (voteNo == 0) {
            voteUpVbox.setPrefHeight(0.0);
            voteDownVbox.setPrefHeight(0.0);
            return;
        }
        int voterNo = audienceVote.getVoterNo();
        if(voterNo < MIN_VOTER_NO) {
            voterNo = MIN_VOTER_NO;
        }
        double maxHeight = voteVbox.getHeight() / 2.0;
        ValueScaler vs = new ValueScaler(0.0, voterNo, 0.0, maxHeight);
        double h = vs.scaleValue(Math.abs(voteNo) * 1.0);
        if (voteNo > 0) {
            voteDownVbox.setPrefHeight(0.0);
            voteUpVbox.setPrefHeight(h);
        } else if (voteNo < 0) {
            voteUpVbox.setPrefHeight(0.0);
            voteDownVbox.setPrefHeight(h);
        }
    }

    public Section getSection(String sectionName) {
        if (sectionName == null) {
            return null;
        }
        for (Section section : sections) {
            if (sectionName.equals(section.getSection())) {
                return section;
            }
        }
        return null;
    }

    public void setSection(ActionEvent actionEvent) {
        presetsChob.getSelectionModel().select(Consts.PRESET_ALL_ON);
        String playSectionName = playingSectionProp.getValue();
        if (playSectionName == null) {
            return;
        }
        sendSetSection(playSectionName);
        Section nextSection = getSection(playSectionName);
        if (nextSection == null) {
            return;
        }
        int startPage = nextSection.getStartPage();
        mainApp.setPage(startPage);
        mainApp.sendPosition();
        updateOverlays();
    }

    public void playSection(ActionEvent actionEvent) {
        mainApp.playSection();
    }

    public void stopSection(ActionEvent actionEvent) {
        mainApp.stopSection();
    }

    public void assignSectionOwnersRnd(ActionEvent actionEvent) {
        EventFactory eventFactory = publisher.getEventFactory();
        StrategyEvent strategyEvent = eventFactory.createStrategyEvent(StrategyEventType.ASSIGN_OWNERS_RND, clock.getSystemTimeMillis());
        publisher.receive(strategyEvent);
    }

    public void resetSectionOwners(ActionEvent actionEvent) {
        EventFactory eventFactory = publisher.getEventFactory();
        StrategyEvent strategyEvent = eventFactory.createStrategyEvent(StrategyEventType.RESET, clock.getSystemTimeMillis());
        publisher.receive(strategyEvent);
        processPresetIntro();
    }

    private void sendSetSection(String sectionName) {
        EventFactory eventFactory = publisher.getEventFactory();
        StrategyEvent strategyEvent = eventFactory.createStrategyEvent(StrategyEventType.SET_SECTION, clock.getSystemTimeMillis());
        strategyEvent.setSectionName(sectionName);
        publisher.receive(strategyEvent);
    }

    public void shuffleSectionOrder(ActionEvent actionEvent) {
        EventFactory eventFactory = publisher.getEventFactory();
        StrategyEvent strategyEvent = eventFactory.createStrategyEvent(StrategyEventType.SHUFFLE_ORDER, clock.getSystemTimeMillis());
        publisher.receive(strategyEvent);
    }

    private void selectTxtToAllInstruments(Boolean newValue) {
        sendTxtToPresentChb.setSelected(newValue);
        sendTxtToDissentChb.setSelected(newValue);
        sendTxtToConcurChb.setSelected(newValue);
        sendTxtToAbstainChb.setSelected(newValue);
    }

    private void selectTxtToAudience(Boolean newValue) {
        sendTxtToAudienceChb.setSelected(newValue);
    }

    private void selectTxtToAll(Boolean newValue) {
        selectTxtToAudience(newValue);
        selectTxtToAllInstruments(newValue);
    }

    private void onUsePitchOverlay(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if (instrumentIds.isEmpty()) {
            return;
        }
        int alpha = 0;
        if (newValue) {
            long transparency = Math.round(pitchOverlayTransparencySldr.getValue());
            alpha = (int) Math.round(transparencyValueScaler.scaleValue(1.0 * transparency));
        } else {
            usePitchLineChb.setSelected(false);
        }

        mainApp.sendPitchValueChange(Math.round(pitchSldr.getValue()), instrumentIds);
        mainApp.sendUsePitchOverlay(newValue, alpha, instrumentIds);
    }

    private void onUsePitchStaveOverlay(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if (instrumentIds.isEmpty()) {
            return;
        }
        int alpha = calcOverlayAlpha(newValue);
        mainApp.sendUsePitchStaveOverlay(newValue, alpha, instrumentIds);
    }

    private void onUsePitchLine(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if (instrumentIds.isEmpty()) {
            return;
        }
        if (newValue && !usePitchOverlayChb.isSelected()) {
            usePitchOverlayChb.setSelected(true);
//            usePitchStaveOverlayChb.setSelected(true);
        }
        mainApp.sendPitchValueChange(Math.round(pitchSldr.getValue()), instrumentIds);
        mainApp.sendUsePitchLine(newValue, instrumentIds);
    }

    private void onPitchValueChange(long newVal) {
        if (!usePitchOverlayChb.isSelected()) {
            return;
        }
        List<Id> instrumentIds = getInstrumentsToSend();
        if (instrumentIds.isEmpty()) {
            return;
        }
        mainApp.sendPitchValueChange(newVal, instrumentIds);
    }

    private void onPitchOvrlTransparencyChange(long newVal) {
        if (!usePitchOverlayChb.isSelected()) {
            return;
        }
        onUsePitchOverlay(usePitchOverlayChb.isSelected());
    }

    private void onUseDynamicsOverlay(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if (instrumentIds.isEmpty()) {
            return;
        }
        if (!newValue) {
            useDynamicsLineChb.setSelected(false);
        }
        int alpha = calcOverlayAlpha(newValue);
        mainApp.sendDynamicsValueChange(Math.round(dynamicsSldr.getValue()), instrumentIds);
        mainApp.sendUseDynamicsOverlay(newValue, alpha, instrumentIds);
    }

    private int calcOverlayAlpha(Boolean isEnabled) {
        return isEnabled ? 255 : 0;
    }

    private void onUseDynamicsLine(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if (instrumentIds.isEmpty()) {
            return;
        }
        if (newValue && !useDynamicsOverlayChb.isSelected()) {
            useDynamicsOverlayChb.setSelected(true);
        }
        mainApp.sendDynamicsValueChange(Math.round(dynamicsSldr.getValue()), instrumentIds);
        mainApp.sendUseDynamicsLine(newValue, instrumentIds);
    }

    private void onTimbreValueChange(long newVal) {
        if (!useTimbreOverlayChb.isSelected()) {
            return;
        }
        List<Id> instrumentIds = getInstrumentsToSend();
        if (instrumentIds.isEmpty()) {
            return;
        }
        mainApp.sendTimbreValueChange(newVal, instrumentIds);
    }

    private void onUseTimbreOverlay(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if (instrumentIds.isEmpty()) {
            return;
        }
        if (!newValue) {
            useTimbreLineChb.setSelected(false);
        }
        int alpha = calcOverlayAlpha(newValue);
        mainApp.sendTimbreValueChange(Math.round(timbreSldr.getValue()), instrumentIds);
        mainApp.sendUseTimbreOverlay(newValue, alpha, instrumentIds);
    }

    private void onUseTimbreLine(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if (instrumentIds.isEmpty()) {
            return;
        }
        if (newValue && !useTimbreOverlayChb.isSelected()) {
            useTimbreOverlayChb.setSelected(true);
        }
        mainApp.sendTimbreValueChange(Math.round(timbreSldr.getValue()), instrumentIds);
        mainApp.sendUseTimbreLine(newValue, instrumentIds);
    }

    private void updateOverlays() {
        Platform.runLater(() -> {
            presetsChob.getSelectionModel().select(Consts.PRESET_ALL_OFF);
        });
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
                onUsePitchOnly(true);
                break;
            case Consts.PRESET_ALL_ON_CONTENT_OFF:
                onUsePitchOnly(false);
                break;
            default:
                LOG.error("Unknown preset selection {}", newSelection);
        }
    }

    private void onUseAllLines(Boolean newValue) {
        Platform.runLater(() -> {
            useDynamicsLineChb.setSelected(newValue);
            useTimbreLineChb.setSelected(newValue);
            usePitchLineChb.setSelected(newValue);
        });
    }

    private void onUseAllOverlays(Boolean newValue) {
        Platform.runLater(() -> {
            useDynamicsOverlayChb.setSelected(newValue);
            useTimbreOverlayChb.setSelected(newValue);
            usePitchOverlayChb.setSelected(newValue);
//            usePitchStaveOverlayChb.setSelected(newValue);
        });
    }

    private void onUsePitchOnly(Boolean newValue) {
        Platform.runLater(() -> {
            if (newValue) {
                useDynamicsLineChb.setSelected(false);
                useTimbreLineChb.setSelected(false);
                usePitchLineChb.setSelected(true);

                useDynamicsOverlayChb.setSelected(false);
                useTimbreOverlayChb.setSelected(false);
                usePitchOverlayChb.setSelected(true);
//                usePitchStaveOverlayChb.setSelected(true);
            } else {
                useDynamicsLineChb.setSelected(true);
                useTimbreLineChb.setSelected(true);
                usePitchLineChb.setSelected(false);

                useDynamicsOverlayChb.setSelected(true);
                useTimbreOverlayChb.setSelected(true);
                usePitchOverlayChb.setSelected(false);
//                usePitchStaveOverlayChb.setSelected(false);
            }
        });
    }

    private String[] getOverlayPresetValues() {
        return Consts.DIALOGS_OVERLAY_PRESETS;
    }

    private Double[] getSynthFreqValues() {
        return Consts.DIALOGS_SYNTH_FREQ_VAlUES;
    }

    private void onDynamicsValueChange(long newVal) {
        if (!useDynamicsOverlayChb.isSelected()) {
            return;
        }
        List<Id> instrumentIds = getInstrumentsToSend();
        if (instrumentIds.isEmpty()) {
            return;
        }
        mainApp.sendDynamicsValueChange(newVal, instrumentIds);
    }

    private List<Id> getInstrumentsToSendTxt() {
        List<Id> instruments = mainApp.getAllInstruments();
        addRemoveInstrument(instruments, presentInstId, sendTxtToPresentChb.isSelected());
        addRemoveInstrument(instruments, concurInstId, sendTxtToConcurChb.isSelected());
        addRemoveInstrument(instruments, dissentInstId, sendTxtToDissentChb.isSelected());
        addRemoveInstrument(instruments, abstainInstId, sendTxtToAbstainChb.isSelected());
        return instruments;
    }

    private void addRemoveInstrument(List<Id> instrumentIds, InstrumentId instId, boolean isSelected) {
        if (isSelected) {
            if (!instrumentIds.contains(instId)) {
                instrumentIds.add(instId);
            }
        } else {
            instrumentIds.remove(instId);
        }
    }

    private void setTxtGuiVals(boolean isAudienceSet, List<Id> instrumentIds, String l1, String l2, String l3) {
        for (Id instrumentId : instrumentIds) {
            WebscoreInstructions instructions = getInstrumentInstructions((InstrumentId) instrumentId);
            if (instructions == null) {
                continue;
            }
            instructions.setLine1(l1);
            instructions.setLine2(l2);
            instructions.setLine3(l3);
        }
        if (isAudienceSet) {
            audienceInstructions.setLine1(l1);
            audienceInstructions.setLine2(l2);
            audienceInstructions.setLine3(l3);
        }
    }

    private WebscoreInstructions getInstrumentInstructions(InstrumentId instrumentId) {
        switch (instrumentId.getName()) {
            case PRESENT:
                return presentInstructions;
            case CONCUR:
                return concurInstructions;
            case DISSENT:
                return dissentInstructions;
            case ABSTAIN:
                return abstainInstructions;
        }
        return null;
    }

    private List<Id> getInstrumentsToSend() {
        List<Id> instrumentIds;
        if (sendToAllChb.isSelected()) {
            instrumentIds = mainApp.getAllInstruments();
        } else {
            instrumentIds = mainApp.getSelectedInstruments();
        }
        return instrumentIds;
    }

    public void reset() {
        resetOverlays();
        resetSections();
    }

    public void resetSections() {
        sections.clear();
        sectionOrder.clear();
        nextSectionProp.setValue(Consts.NAME_NA);
        playingSectionProp.setValue(Consts.NAME_NA);
        setSectionStatusStyle(Consts.READY, LABEL_GREEN, LABEL_RED);
    }

    public void resetOverlays() {
        setTimbreDefaultValue();
        setDynamicsDefaultValue();
        setPitchDefaultValue();
        presetsChob.getSelectionModel().select(Consts.PRESET_ALL_OFF);
        selectAllTxtRecipientsChb.setSelected(false);
    }

    public void setTimbreDefaultValue() {
        timbreSldr.setValue(50.0);
    }

    public void setDynamicsDefaultValue() {
        dynamicsSldr.setValue(50.0);
    }

    public void setPitchDefaultValue() {
        pitchSldr.setValue(50.0);
    }

    public void setSynthFreqDefaultValue() {
        synthFreqChob.setValue(1.0);
    }

    public void setSynthDurDefaultValue() {
        synthDurChob.setValue(8.0);
    }

    public void onParticipantRemove(Participant participant) {
        for(Section section : sections) {
            String sectionOwner = section.getOwner();
            if(sectionOwner.startsWith(INVALID_OWNER_PREFIX)) {
                sectionOwner = sectionOwner.substring(INVALID_OWNER_PREFIX.length());
            }
            if(participant.getClientId().equals(sectionOwner)) {
                section.setIsOwnerValid(false);
                String owner = INVALID_OWNER_PREFIX + sectionOwner;
                section.setOwner(owner);
            }
        }
    }

    public void onParticipantUpdate(Participant participant) {
        if(participant.getClientId() == null) {
            return;
        }
        for(Section section : sections) {
            String sectionOwner = section.getOwner();
            if(sectionOwner == null) {
                continue;
            }
            if(sectionOwner.startsWith(INVALID_OWNER_PREFIX)) {
                sectionOwner = sectionOwner.substring(INVALID_OWNER_PREFIX.length());
            }
            if(participant.getClientId().equals(sectionOwner)) {
                section.setIsOwnerValid(true);
                section.setOwner(sectionOwner);
            }
        }
    }

    public void onTempoEvent(Tempo tempo) {
        this.tempo = tempo;
    }

    class MaxSender implements Runnable {
        private String target;
        private List<Object> args;
        public MaxSender(String target, List<Object> args) {
            this.target = target;
            this.args = args;
        }
        @Override
        public void run() {
            LOG.info("MaxSender: sending to {}", target);
            sendMaxEvent(target, args);
        }
    }

}
