package com.xenaksys.szcore.gui.view;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.DynamicMovementStrategy;
import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.SequentalIntRange;
import com.xenaksys.szcore.algo.ValueScaler;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.gui.StrategyEvent;
import com.xenaksys.szcore.event.gui.StrategyEventType;
import com.xenaksys.szcore.event.web.audience.WebAudienceAudioEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceAudioEventType;
import com.xenaksys.szcore.event.web.audience.WebAudienceInstructionsEvent;
import com.xenaksys.szcore.gui.SzcoreClient;
import com.xenaksys.szcore.gui.model.Movement;
import com.xenaksys.szcore.gui.model.MovementSection;
import com.xenaksys.szcore.gui.model.WebscoreInstructions;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.EventService;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.MovementInfo;
import com.xenaksys.szcore.model.MovementSectionInfo;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreService;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.score.web.audience.AudioComponentType;
import com.xenaksys.szcore.util.MathUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.COMMA;
import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.MAXMSP_CMD_PLAY;
import static com.xenaksys.szcore.Consts.MAXMSP_CMD_SET_FILE;
import static com.xenaksys.szcore.Consts.MAXMSP_GRANULATOR;
import static com.xenaksys.szcore.Consts.MAXMSP_GRANULATOR_CONT;
import static com.xenaksys.szcore.Consts.MAXMSP_GRANULATOR_CONT_STOP;
import static com.xenaksys.szcore.Consts.MAXMSP_GROOVE;
import static com.xenaksys.szcore.Consts.MAXMSP_GROOVE_CONT;
import static com.xenaksys.szcore.Consts.MAXMSP_GROOVE_CONT_STOP;
import static com.xenaksys.szcore.Consts.NAME_NA;
import static com.xenaksys.szcore.Consts.OSC_ADDRESS_ZSCORE;
import static com.xenaksys.szcore.Consts.WEB_AUDIO_ACTION_DURATION_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_DUR_MULTIPLIER;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_FREQ_MULTIPLIER;
import static com.xenaksys.szcore.Consts.WEB_SYNTH;
import static com.xenaksys.szcore.gui.view.ScoreController.fixedLengthString;

public class SymphoneaScoreController {
    static final Logger LOG = LoggerFactory.getLogger(ScoreController.class);

    public static final String SCORE_NAME = "Symphonea";
    public static final String LABEL_GREEN = "label-green";
    public static final String LABEL_RED = "label-red";
    public static final String REGION_SELECTED_CLASS = "cell-selected";

    public static final String MP = "MP";
    public static final String MF = "MF";
    public static final String F = "F";
    public static final String P = "P";
    public static final String FF = "FF";
    public static final String PP = "PP";
    public static final String MAX_GRANULATOR_ADDR = OSC_ADDRESS_ZSCORE + MAXMSP_GRANULATOR;
    public static final String MAX_GRANULATOR_CONT_ADDR = OSC_ADDRESS_ZSCORE + MAXMSP_GRANULATOR_CONT;
    public static final String MAX_GRANULATOR_CONT_STOP_ADDR = OSC_ADDRESS_ZSCORE + MAXMSP_GRANULATOR_CONT_STOP;
    public static final String MAX_GROOVE_ADDR = OSC_ADDRESS_ZSCORE + MAXMSP_GROOVE;
    public static final String MAX_GROOVE_CONT_ADDR = OSC_ADDRESS_ZSCORE + MAXMSP_GROOVE_CONT;
    public static final String MAX_GROOVE_CONT_STOP_ADDR = OSC_ADDRESS_ZSCORE + MAXMSP_GROOVE_CONT_STOP;

    static final int MAX_VOTE_SECTIONS = 4;
    private final ValueScaler transparencyValueScaler = new ValueScaler(0.0, 100.0, 255.0, 0.0);

    @FXML
    private ListView<String> movementOrderLvw;
    @FXML
    private Label nextMovementLbl;
    @FXML
    private Label currentMovementLbl;
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
    private RadioButton presetScoreRdb;
    @FXML
    private RadioButton presetFreeRdb;
    @FXML
    private RadioButton presetWelcomeRdb;
    @FXML
    private RadioButton presetIntroRdb;
    @FXML
    private RadioButton presetAudienceNotesRdb;
    @FXML
    private RadioButton presetEndRdb;
    @FXML
    private RadioButton presetImproCurrentRdb;
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
    @FXML
    private RadioButton presetFreqDurMfRdb;
    @FXML
    private RadioButton presetFreqDurFRdb;
    @FXML
    private RadioButton presetFreqDurFfRdb;
    @FXML
    private RadioButton presetFreqDurMpRdb;
    @FXML
    private RadioButton presetFreqDurPRdb;
    @FXML
    private RadioButton presetFreqDurPpRdb;
    @FXML
    private ListView<String> sectionOrderLvw;
    @FXML
    private Label curSectionIdLbl;
    @FXML
    private Label curSectionStartPageLbl;
    @FXML
    private Label curSectionEndPageLbl;
    @FXML
    private Label nextSectionIdLbl;
    @FXML
    private Label nextSectionStartPageLbl;
    @FXML
    private Label nextSectionEndPageLbl;
    @FXML
    private Region voteSection1IdRgn;
    @FXML
    private Label voteSection1IdLbl;
    @FXML
    private Label voteSection1VoteLbl;
    @FXML
    private Label voteSection1StartPageLbl;
    @FXML
    private Label voteSection1EndPageLbl;
    @FXML
    private Region voteSection2IdRgn;
    @FXML
    private Label voteSection2IdLbl;
    @FXML
    private Label voteSection2VoteLbl;
    @FXML
    private Label voteSection2StartPageLbl;
    @FXML
    private Label voteSection2EndPageLbl;
    @FXML
    private Region voteSection3IdRgn;
    @FXML
    private Label voteSection3IdLbl;
    @FXML
    private Label voteSection3VoteLbl;
    @FXML
    private Label voteSection3StartPageLbl;
    @FXML
    private Label voteSection3EndPageLbl;
    @FXML
    private Region voteSection4IdRgn;
    @FXML
    private Label voteSection4IdLbl;
    @FXML
    private Label voteSection4VoteLbl;
    @FXML
    private Label voteSection4StartPageLbl;
    @FXML
    private Label voteSection4EndPageLbl;
    @FXML
    private ChoiceBox<String> mvtSectionsChob;
    @FXML
    private CheckBox overrideNextSectionChb;
    @FXML
    private CheckBox stopOnNextPageEndChb;

    private SzcoreClient mainApp;
    private EventService publisher;
    private ScoreService scoreService;
    private Clock clock;

    private WebscoreInstructions txtInstructions;
    private WebscoreInstructions presentInstructions;
    private WebscoreInstructions audienceInstructions;

    private final ObservableList<Movement> movements = FXCollections.observableArrayList();
    private final ObservableList<MovementSection> sections = FXCollections.observableArrayList();
    private final ObservableList<String> sectionIds = FXCollections.observableArrayList();
    private final ObservableList<String> movementOrder = FXCollections.observableArrayList();
    private final ObservableList<String> sectionOrder = FXCollections.observableArrayList();
    private final StringProperty nextMovementProp = new SimpleStringProperty(EMPTY);
    private final StringProperty currentMovementProp = new SimpleStringProperty(EMPTY);
    private final ObservableList<String> overlayPresets = FXCollections.observableArrayList();
    private final ObservableList<Double> synthFreq = FXCollections.observableArrayList();
    private final ObservableList<Double> synthDur = FXCollections.observableArrayList();
    private final ObservableList<String> defaultRegionStyle = FXCollections.observableArrayList();
    private final MovementSection currentSection = new MovementSection();
    private final MovementSection nextSection = new MovementSection();
    private final List<MovementSection> voteSections = new ArrayList<>();
    private final List<Region> voteRegions = new ArrayList<>();

    private final ToggleGroup presetGroup = new ToggleGroup();
    private final ToggleGroup freqPresetGroup = new ToggleGroup();
    private Tempo tempo;

    private Circle[] semaphore;

    private double lastSythFreqValue = 0.0;
    private double lastSythDurationValue = 8.0;

    private boolean isOverrideNextSection = false;
    private String nextSectionOverride = null;

    public void setMainApp(SzcoreClient mainApp) {
        this.mainApp = mainApp;
    }

    public void populate() {
        movementOrderLvw.setItems(movementOrder);
        sectionOrderLvw.setItems(sectionOrder);
        mvtSectionsChob.setItems(sectionIds);

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

        disableAllTxtRecipients();
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
        presetWelcomeRdb.setSelected(false);
        presetIntroRdb.setSelected(false);
        presetAudienceNotesRdb.setSelected(false);
        presetEndRdb.setSelected(false);

        presetFreqDurMfRdb.setSelected(false);
        presetFreqDurFRdb.setSelected(false);
        presetFreqDurFfRdb.setSelected(false);
        presetFreqDurMpRdb.setSelected(false);
        presetFreqDurPRdb.setSelected(false);
        presetFreqDurPpRdb.setSelected(false);

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

        defaultRegionStyle.addAll(voteSection1IdRgn.getStyleClass());

        overrideNextSectionChb.setSelected(false);
        overrideNextSectionChb.selectedProperty().addListener((observable, oldValue, newValue) -> setOverrideNextSection(newValue));

        stopOnNextPageEndChb.setSelected(false);
        stopOnNextPageEndChb.selectedProperty().addListener((observable, oldValue, newValue) -> setStopOnNextPage(newValue));
    }

    @FXML
    private void initialize() {
        movementOrderLvw.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        movementOrderLvw.setEditable(false);
        movementOrderLvw.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            String out = newSelection;
            if (newSelection == null) {
                out = oldSelection;
            }
            onMovementSelect(out);
        });

        sectionOrderLvw.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        sectionOrderLvw.setEditable(false);
        sectionOrderLvw.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            String out = newSelection;
            if (newSelection == null) {
                out = oldSelection;
            }
            onSectionOrderSelect(out);
        });

        mvtSectionsChob.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            String out = newSelection;
            if (newSelection == null) {
                out = oldSelection;
            }
            onMvtSectionChobChange(out);
        });

        nextMovementLbl.textProperty().bind(nextMovementProp);
        currentMovementLbl.textProperty().bind(currentMovementProp);

        txtInstructions = new WebscoreInstructions();
        instPitchLn1Txt.textProperty().bindBidirectional(txtInstructions.line1Property());
        instPitchLn2Txt.textProperty().bindBidirectional(txtInstructions.line2Property());
        instPitchLn3Txt.textProperty().bindBidirectional(txtInstructions.line3Property());

        presentInstructions = new WebscoreInstructions();
        presentL1Lbl.textProperty().bindBidirectional(presentInstructions.line1Property());
        presentL2Lbl.textProperty().bindBidirectional(presentInstructions.line2Property());
        presentL3Lbl.textProperty().bindBidirectional(presentInstructions.line3Property());

        audienceInstructions = new WebscoreInstructions();
        audienceL1Lbl.textProperty().bindBidirectional(audienceInstructions.line1Property());
        audienceL2Lbl.textProperty().bindBidirectional(audienceInstructions.line2Property());
        audienceL3Lbl.textProperty().bindBidirectional(audienceInstructions.line3Property());

        selectAllInstrumentsTxtChb.selectedProperty().addListener((observable, oldValue, newValue) -> selectTxtToAllInstruments(newValue));
        selectAudienceTxtChb.selectedProperty().addListener((observable, oldValue, newValue) -> selectTxtToAudience(newValue));
        selectAllTxtRecipientsChb.selectedProperty().addListener((observable, oldValue, newValue) -> selectTxtToAll(newValue));

        semaphore1Crc.setFill(Color.RED);
        semaphore1Crc.setStroke(Color.BLACK);
        semaphore2Crc.setFill(Color.TRANSPARENT);
        semaphore2Crc.setStroke(Color.BLACK);
        semaphore3Crc.setFill(Color.TRANSPARENT);
        semaphore3Crc.setStroke(Color.BLACK);
        semaphore4Crc.setFill(Color.TRANSPARENT);
        semaphore4Crc.setStroke(Color.BLACK);

        adncMasterVolSldr.setValue(100.0);
        adncSynthVolSldr.setValue(10.0);

        presetFreqDurMfRdb.setToggleGroup(freqPresetGroup);
        presetFreqDurMfRdb.setUserData(MF);
        presetFreqDurFRdb.setToggleGroup(freqPresetGroup);
        presetFreqDurFRdb.setUserData(F);
        presetFreqDurFfRdb.setToggleGroup(freqPresetGroup);
        presetFreqDurFfRdb.setUserData(FF);
        presetFreqDurMpRdb.setToggleGroup(freqPresetGroup);
        presetFreqDurMpRdb.setUserData(MP);
        presetFreqDurPRdb.setToggleGroup(freqPresetGroup);
        presetFreqDurPRdb.setUserData(P);
        presetFreqDurPpRdb.setToggleGroup(freqPresetGroup);
        presetFreqDurPpRdb.setUserData(PP);

        freqPresetGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            if (freqPresetGroup.getSelectedToggle() != null) {
                System.out.println(freqPresetGroup.getSelectedToggle().getUserData().toString());
                onFreqDurPresetChange(freqPresetGroup.getSelectedToggle().getUserData().toString());
            }
        });

        curSectionIdLbl.textProperty().bind(currentSection.sectionProperty());
        curSectionStartPageLbl.textProperty().bind(currentSection.startPageProperty().asString());
        curSectionEndPageLbl.textProperty().bind(currentSection.endPageProperty().asString());

        nextSectionIdLbl.textProperty().bind(nextSection.sectionProperty());
        nextSectionStartPageLbl.textProperty().bind(nextSection.startPageProperty().asString());
        nextSectionEndPageLbl.textProperty().bind(nextSection.endPageProperty().asString());

        for(int i = 0; i < MAX_VOTE_SECTIONS; i++) {
            voteSections.add(new MovementSection());
        }
        voteRegions.add(voteSection1IdRgn);
        voteRegions.add(voteSection2IdRgn);
        voteRegions.add(voteSection3IdRgn);
        voteRegions.add(voteSection4IdRgn);

        voteSection1IdLbl.textProperty().bind(voteSections.get(0).sectionProperty());
        voteSection1VoteLbl.textProperty().bind(createIntToStrBinding(voteSections.get(0).voteNoProperty()));
        voteSection1StartPageLbl.textProperty().bind(createIntToStrBinding(voteSections.get(0).startPageProperty()));
        voteSection1EndPageLbl.textProperty().bind(createIntToStrBinding(voteSections.get(0).endPageProperty()));

        voteSection2IdLbl.textProperty().bind(voteSections.get(1).sectionProperty());
        voteSection2VoteLbl.textProperty().bind(createIntToStrBinding(voteSections.get(1).voteNoProperty()));
        voteSection2StartPageLbl.textProperty().bind(createIntToStrBinding(voteSections.get(1).startPageProperty()));
        voteSection2EndPageLbl.textProperty().bind(createIntToStrBinding(voteSections.get(1).endPageProperty()));

        voteSection3IdLbl.textProperty().bind(voteSections.get(2).sectionProperty());
        voteSection3VoteLbl.textProperty().bind(createIntToStrBinding(voteSections.get(2).voteNoProperty()));
        voteSection3StartPageLbl.textProperty().bind(createIntToStrBinding(voteSections.get(2).startPageProperty()));
        voteSection3EndPageLbl.textProperty().bind(createIntToStrBinding(voteSections.get(2).endPageProperty()));

        voteSection4IdLbl.textProperty().bind(voteSections.get(3).sectionProperty());
        voteSection4VoteLbl.textProperty().bind(createIntToStrBinding(voteSections.get(3).voteNoProperty()));
        voteSection4StartPageLbl.textProperty().bind(createIntToStrBinding(voteSections.get(3).startPageProperty()));
        voteSection4EndPageLbl.textProperty().bind(createIntToStrBinding(voteSections.get(3).endPageProperty()));
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
//        scoreService.publishAudienceViewState(isNotesEnabled, isAudioEnabled, false, false, false);
    }

    @FXML
    private void sendPreset(ActionEvent event) {
        Toggle selected = presetGroup.getSelectedToggle();
        if(selected == null) {
            return;
        }
        String preset = selected.getUserData().toString();
        LOG.info("sendPreset: {}", preset);
//        switch (preset) {
//            case SCORE:
//                processPresetScore();
//                break;
//            default:
//                LOG.error("Unkonwn preset: {}", preset);
//        }
    }

    @FXML
    private void setPreviousSectionOrder(ActionEvent event) {
        int selected = sectionOrderLvw.getSelectionModel().getSelectedIndex();
        if(selected <= 0 ) {
            return;
        }
        selected--;
        sectionOrderLvw.getSelectionModel().select(selected);
    }

    @FXML
    private void setNextSectionOrder(ActionEvent event) {
        int selected = sectionOrderLvw.getSelectionModel().getSelectedIndex();
        if(selected >= sectionOrder.size() - 1 ) {
            return;
        }
        selected++;
        sectionOrderLvw.getSelectionModel().select(selected);
    }

    private void setCellRegionBkg(Region region, boolean isSelected) {
        Platform.runLater(() -> {
            ObservableList<String> regionStyle = region.getStyleClass();
            regionStyle.clear();
            if (isSelected) {
                regionStyle.add(REGION_SELECTED_CLASS);
            } else {
                if (defaultRegionStyle == null) {
                    region.setStyle(null);
                } else {
                    regionStyle.addAll(defaultRegionStyle);
                }
            }
        });
    }
    private StringBinding createIntToStrBinding(IntegerProperty intProp) {
        return  Bindings.createStringBinding(() -> {
            if(intProp.getValue() == 0) {
                return "";
            } else {
                return intProp.getValue().toString();
            }
        }, intProp);
    }

    private void disableAllTxtRecipients() {
        selectAllTxtRecipientsChb.setSelected(false);
        selectAllInstrumentsTxtChb.setSelected(false);
        selectAudienceTxtChb.setSelected(false);
    }

    private void processPresetScore() {
        disableAllTxtRecipients();
        txtInstructions.setLine1(EMPTY);
        txtInstructions.setLine2(EMPTY);
        txtInstructions.setLine3(EMPTY);
        txtInstructions.setVisible(false);
        selectAllInstrumentsTxtChb.setSelected(true);
        publishWebscoreInstructions();
    }

    private void processPresetImpro() {
        disableAllTxtRecipients();
        txtInstructions.setLine1("Click note to play");
        txtInstructions.setLine2(EMPTY);
        txtInstructions.setLine3(EMPTY);
        txtInstructions.setVisible(true);
        selectAudienceTxtChb.setSelected(true);
        publishWebscoreInstructions();

        disableAllTxtRecipients();
        txtInstructions.setLine1("Free Improvisation");
        txtInstructions.setLine2("Follow audience / MAX");
        txtInstructions.setLine3(EMPTY);
        txtInstructions.setVisible(true);
        selectAllInstrumentsTxtChb.setSelected(true);
        publishWebscoreInstructions();

        adncNotesChb.setSelected(true);
        adncAudioChb.setSelected(true);
        sendAudienceViewState(null);
        sendMaxPreset(3);
    }

    private void onFreqDurPresetChange(String value) {
        if(value == null) {
            return;
        }
        switch (value) {
            case PP:
                setFreqDur(0.6, 8.0);
                break;
            case P:
                setFreqDur(0.8, 8.0);
                break;
            case MP:
                setFreqDur(1.0, 8.0);
                break;
            case MF:
                setFreqDur(2.0, 2.0);
                break;
            case F:
                setFreqDur(4.0, 0.5);
                break;
            case FF:
                setFreqDur(8.0, 0.05);
                break;
        }
    }

    private void setFreqDur(double freq, double duration) {
        if(synthFreq.contains(freq)) {
            synthFreqChob.setValue(freq);
        }
        if(synthDur.contains(duration)) {
            synthDurChob.setValue(duration);
        }
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

    private void sendMaxFiles(String granulatorFile, String grooveFile) {
        sendMaxEvent(MAX_GRANULATOR_ADDR, Arrays.asList(MAXMSP_CMD_SET_FILE, granulatorFile));
        sendMaxEvent(MAX_GROOVE_ADDR, Arrays.asList(MAXMSP_CMD_SET_FILE, grooveFile));
    }

    private void sendMaxPreset(int preset) {
        scoreService.sendMaxPreset(preset);
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

    private void onMvtSectionChobChange(String section) {
        nextSectionOverride = section;
        if(isOverrideNextSection) {
            sendMovementInfo(false);
        }
    }

    private void setNextSection(String sectionId) {
        MovementSection section = getSection(sectionId);
        if(section == null || !section.getSection().equals(sectionId)) {
            LOG.error("setNextSection: invalid sectionId: {}", sectionId);
            return;
        }
        nextSection.copy(section);
        sendMovementInfo(false);
    }

    private MovementSection getSection(int selectedIndex) {
        if(selectedIndex < 0 || selectedIndex >= sections.size()) {
            LOG.error("getSection: invalid section Index: {}", selectedIndex);
            return null;
        }
        return sections.get(selectedIndex);
    }

    private MovementSection getSection(String sectionId) {
        if(sectionId == null) {
            return null;
        }
        for(MovementSection section : sections) {
            if(sectionId.equals(section.getSection())) {
                return section;
            }
        }
        return null;
    }

    private void onSectionOrderSelect(String sectionOrder) {
        String[] sections = sectionOrder.split(COMMA);
        String first = null;
        resetVoteSections();
        for(int i = 0; i < sections.length; i++) {
            String section = sections[i];
            if(first == null) {
                first = section;
            }
            MovementSection movSection = getSection(section);
            if(movSection != null &&  i < voteSections.size()) {
                MovementSection guiMovSection = voteSections.get(i);
                guiMovSection.copy(movSection);
            }
        }
        if(first != null && !isOverrideNextSection) {
//            setNextSection(first);
//            mvtSectionsChob.getSelectionModel().select(first);
        }
        processSectionVote();
        sendMovementInfo(false);
    }

    private void resetVoteSections() {
        for(MovementSection guiMovSection : voteSections) {
            guiMovSection.reset();
        }
    }

    private void processSectionVote() {
        int maxIndex = 0;
        int maxVote = 0;
        for( int i = 0; i < voteSections.size(); i++) {
            MovementSection movementSection = voteSections.get(i);
            if(movementSection.getVoteNo() > maxVote) {
                maxVote = movementSection.getVoteNo();
                maxIndex = i;
            }
        }
        showMaxVote(maxIndex);
    }

    private void showMaxVote(int maxIndex) {
        Platform.runLater(() -> {
            for(int i = 0; i < voteRegions.size(); i++) {
                Region voteRegion = voteRegions.get(i);
                setCellRegionBkg(voteRegion, i == maxIndex);
            }
        });
    }

    private void setOverrideNextSection(Boolean newValue) {
        isOverrideNextSection = newValue;
        if(!isOverrideNextSection) {
            sendMovementInfo(false);
        }
    }

    private void setStopOnNextPage(Boolean isStop) {
        if(isStop) {
            sendStopNext();
        }
    }

    private void onMovementSelect(String movementName) {
        Movement movement = getMovement(movementName);
        if (movement == null) {
            return;
        }
        onMovementSelect(movement);
    }

    private int getCurrentMovementIndex() {
        return movementOrder.indexOf(currentMovementProp.get());
    }

    private void onPlayAudioOnNewSection(Boolean newValue) {
        if(!newValue) {
            sendMaxEvent(MAX_GRANULATOR_CONT_STOP_ADDR, Collections.singletonList(MAXMSP_CMD_PLAY));
            sendMaxEvent(MAX_GROOVE_CONT_STOP_ADDR, Collections.singletonList(MAXMSP_CMD_PLAY));
        }
    }

    private void onMovementSelect(Movement movement) {
        if (movement == null) {
            return;
        }
        String prev = this.currentMovementProp.get();
        setCurrentMovementLabel(movement.getId());

        setNextMovement();

        if(prev != null && !prev.equals(movement.getId())) {
            onCurrentMovementChange();
        }
    }

    private void setNextMovement() {
        String nextMovement = NAME_NA;
        int currentIndex = getCurrentMovementIndex();
        if(currentIndex >= 0 && currentIndex < ( movementOrder.size() - 1 )) {
            nextMovement = movementOrder.get(currentIndex + 1);
        }
        setNextMovementsLabel(nextMovement);
    }

    private void onCurrentMovementChange() {
        Movement mov = getMovement(currentMovementProp.get());
        if(mov == null) {
            return;
        }
        setMovementSections(mov);
        setSectionOrder(mov);
    }

    private void setCurrentMovementLabel(String value) {
        if (value == null) {
            value = Consts.NAME_NA;
        }
        String old = currentMovementProp.getValue();
        if (value.equals(old)) {
            return;
        }
        currentMovementProp.setValue(value);
        selectMovement(value);
    }

    private void setCurrentMovementSection(String name) {
        if (name == null) {
            return;
        }
        if(currentSection.getSection().equals(name)) {
            return;
        }
        MovementSection section = getSection(name);
        if(section == null) {
            return;
        }
        currentSection.copy(section);
    }

    private void setNextMovementSection(String name) {
        if (name == null) {
            return;
        }
        if(nextSection.getSection().equals(name)) {
            return;
        }
        MovementSection section = getSection(name);
        if(section == null) {
            return;
        }
        nextSection.copy(section);
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

    private void selectMovement(String value) {
        String selectedOrder = movementOrderLvw.getSelectionModel().getSelectedItem();
        if (value.equals(selectedOrder)) {
            return;
        }
        movementOrderLvw.getSelectionModel().select(value);
    }

    private void setNextMovementsLabel(String value) {
        if (value == null) {
            value = Consts.NAME_NA;
        }
        String old = nextMovementProp.getValue();
        if (value.equals(old)) {
            return;
        }
        nextMovementProp.setValue(value);
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
        DynamicMovementStrategy movementStrategy = szcore.getDynamicScoreStrategy();
        if (movementStrategy == null || !movementStrategy.isActive()) {
            return;
        }

        List<MovementInfo>  movementInfos = movementStrategy.getMovementInfos();
        List<Movement> guiMovements = new ArrayList<>();
        for(MovementInfo movementInfo : movementInfos) {
            Movement movement = new Movement();
            movement.setId(movementInfo.getMovementId());
            SequentalIntRange range = movementInfo.getPageRange();
            if (range != null) {
                movement.setFirstPage(range.getStart());
                movement.setLastPage(range.getEnd());
            }
            List<MovementSectionInfo> sectionInfos = movementInfo.getSections();
            for (MovementSectionInfo sectionInfo : sectionInfos) {
                MovementSection movementSection = new MovementSection();
                movementSection.setSection(sectionInfo.getSectionId());
                IntRange pageRange = sectionInfo.getPageRange();
                if (pageRange != null) {
                    movementSection.setStartPage(pageRange.getStart());
                    movementSection.setEndPage(pageRange.getEnd());
                }
                List<String> sectionParts = sectionInfo.getParts();
                for (String part : sectionParts) {
                    movementSection.addPart(part);
                }
                movement.addSection(movementSection);
            }
            List<List<String>> sectionsOrder = movementInfo.getSectionsOrder();
            int firstSectionPage = 0;
            if (!sectionsOrder.isEmpty()) {
                List<String> firstOrder = sectionsOrder.get(0);
                if (firstOrder != null && !firstOrder.isEmpty()) {
                    String firstSection = firstOrder.get(0);
                    if(firstSection != null) {
                        MovementSection movementSection = movement.getSection(firstSection);
                        firstSectionPage = movementSection.getStartPage();
                    }
                }
            }
            int startPage = movementInfo.getStartPage();
            if(startPage != firstSectionPage) {
                LOG.error("onScoreLoad: Unequal startPage {}, section page {}", startPage, firstSectionPage);
                if(firstSectionPage > 0) {
                    startPage = firstSectionPage;
                }
            }
            movement.setStartPage(startPage);

            List<String> sectionsOrderCsv = convertSectionsOrder(sectionsOrder);
            movement.addSectionOrder(sectionsOrderCsv);

            guiMovements.add(movement);
        }

        List<String> movementOrderNames = movementStrategy.getMovementOrder();
        if (movementOrderNames == null || movementOrderNames.isEmpty()) {
            return;
        }
        String curMovementName = null;
        if(movementOrderNames.size() > 0) {
            curMovementName = movementOrderNames.get(0);
        }

        initMovementsInfo(guiMovements, movementOrderNames, curMovementName);
    }

    public void onMovementInfo(List<MovementInfo> movementInfos, String currentMovement, String nextMovement, String currentSection, String nextSection) {
        Platform.runLater(() -> {
            if(!currentMovementProp.get().equals(currentMovement)) {
                currentMovementProp.setValue(currentMovement);
            }
            if(!nextMovementProp.get().equals(nextMovement)) {
                nextMovementProp.setValue(nextMovement);
            }
            setCurrentMovementSection(currentSection);
            setNextMovementSection(nextSection);
        });
    }

    private List<String> convertSectionsOrder(List<List<String>> sectionsOrder) {
        List<String> out = new ArrayList<>();
        if(sectionsOrder == null) {
            return out;
        }

        for(List<String> sectionOrder : sectionsOrder) {
            String ord = String.join(",", sectionOrder);
            out.add(ord);
        }
        return out;
    }

    private void initMovementsInfo(final List<Movement> guiMovements, final List<String> guiMovementOrder, final String curMovement) {
        Platform.runLater(() -> {
            resetOverlays();
            this.movements.addAll(guiMovements);
            this.movementOrder.clear();
            this.movementOrder.addAll(guiMovementOrder);
            movementOrderLvw.getSelectionModel().select(curMovement);
        });
    }

    public Movement getMovement(String name) {
        if (name == null) {
            return null;
        }
        for (Movement movement : movements) {
            if (name.equals(movement.getId())) {
                return movement;
            }
        }
        return null;
    }

    public void setMovement(ActionEvent actionEvent) {
        stopOnNextPageEndChb.setSelected(false);
        overrideNextSectionChb.setSelected(false);
        presetsChob.getSelectionModel().select(Consts.PRESET_ALL_ON);
        String playMovName = currentMovementProp.getValue();
        if (playMovName == null) {
            return;
        }
        sendMovementInfo(true);
        Movement nextMovement = getMovement(playMovName);
        if (nextMovement == null) {
            return;
        }
        int startPage = nextMovement.getStartPage();
        mainApp.setPage(startPage);
        mainApp.sendPosition();
        updateOverlays();
    }

    private void setMovementSections(Movement movement) {
        ObservableList<MovementSection> movementSections = movement.getSections();
        Platform.runLater(() -> {
            sections.clear();
            if(movementSections == null) {
                return;
            }
            sections.clear();
            sectionIds.clear();
            sections.addAll(movementSections);
            for(MovementSection section : movementSections) {
                sectionIds.add(section.getSection());
            }
            String firstSection = movement.getFirstSection();
            if(firstSection != null) {
                mvtSectionsChob.getSelectionModel().select(firstSection);
            }
        });
    }

    private void setSectionOrder(Movement movement) {
        if(movement == null) {
            return;
        }

        Platform.runLater(() -> {
            sectionOrder.clear();
            sectionOrder.addAll(movement.getSectionOrder());
            if(sectionOrder.size() > 0) {
                String first = sectionOrder.get(0);
                setCurrentMovementSection(first);
                setNextMovementSection(first);
                sendMovementInfo(false);
            }
            if(sectionOrder.size() > 0) {
                sectionOrderLvw.getSelectionModel().select(0);
            }
        });
    }

    public void playMovement(ActionEvent actionEvent) {
        mainApp.playSection();
        updateOverlays();
    }

    public void stopSection(ActionEvent actionEvent) {
        mainApp.stopSection();
    }

    private void sendMovementInfo(Boolean isOverrideCurrentSection) {
        EventFactory eventFactory = publisher.getEventFactory();
        StrategyEvent strategyEvent = eventFactory.createStrategyEvent(StrategyEventType.SET_MOVEMENT_INFO, clock.getSystemTimeMillis());
        strategyEvent.setMovementName(currentMovementProp.get());
        strategyEvent.setSectionName(currentSection.getSection());
        if(isOverrideNextSection) {
            strategyEvent.setNextSectionName(nextSectionOverride);
        } else {
            strategyEvent.setNextSectionName(nextSection.getSection());
        }
        strategyEvent.setOrderIndex(sectionOrderLvw.getSelectionModel().getSelectedIndex());
        strategyEvent.setOverrideNextSection(isOverrideNextSection);
        strategyEvent.setOverrideCurrentSection(isOverrideCurrentSection);
        LOG.info("sendMovementInfo: {}", strategyEvent);
        publisher.receive(strategyEvent);
    }

    private void sendStopNext() {
        EventFactory eventFactory = publisher.getEventFactory();
        StrategyEvent strategyEvent = eventFactory.createStrategyEvent(StrategyEventType.STOP_NEXT, clock.getSystemTimeMillis());
        LOG.info("sendStopNext: {}", strategyEvent);
        publisher.receive(strategyEvent);
    }

    private void selectTxtToAllInstruments(Boolean newValue) {
        sendTxtToPresentChb.setSelected(newValue);
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

    private void clearAllScreens() {
        updateOverlays();
    }

    private void updateOverlays() {
        Platform.runLater(() -> {
            presetsChob.getSelectionModel().select(Consts.PRESET_ALL_OFF);
        });
        processPresetScore();
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
//        addRemoveInstrument(instruments, presentInstId, sendTxtToPresentChb.isSelected());
        return instruments;
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
//        switch (instrumentId.getName()) {
//            case PRESENT:
//                return presentInstructions;
//        }
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
        movements.clear();
        movementOrder.clear();
        nextMovementProp.setValue(Consts.NAME_NA);
        currentMovementProp.setValue(Consts.NAME_NA);
        currentSection.reset();
        nextSection.reset();
        stopOnNextPageEndChb.setSelected(false);
        overrideNextSectionChb.setSelected(false);
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

    public void onTempoEvent(Tempo tempo) {
        this.tempo = tempo;
    }

    public void onStop() {
        showSemaphore(1, Color.RED);
        stopOnNextPageEndChb.setSelected(false);
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
