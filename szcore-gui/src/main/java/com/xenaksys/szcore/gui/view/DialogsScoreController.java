package com.xenaksys.szcore.gui.view;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.ScoreBuilderStrategy;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.gui.StrategyEvent;
import com.xenaksys.szcore.event.gui.StrategyEventType;
import com.xenaksys.szcore.gui.SzcoreClient;
import com.xenaksys.szcore.gui.model.Section;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.EventService;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreService;
import com.xenaksys.szcore.model.SectionInfo;
import com.xenaksys.szcore.score.BasicScore;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.xenaksys.szcore.gui.view.ScoreController.fixedLengthString;

public class DialogsScoreController {
    static final Logger LOG = LoggerFactory.getLogger(ScoreController.class);

    public static final String SCORE_NAME = "Dialogs";
    public static final String LABEL_GREEN = "label-green";
    public static final String LABEL_RED = "label-red";

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

    private SzcoreClient mainApp;
    private EventService publisher;
    private ScoreService scoreService;
    private Clock clock;
    private ObservableList<Section> sections = FXCollections.observableArrayList();
    private ObservableList<String> sectionOrder = FXCollections.observableArrayList();
    private StringProperty nextSectionProp = new SimpleStringProperty("N/A");
    private StringProperty playingSectionProp = new SimpleStringProperty("N/A");
    private ObservableList<String> overlayPresets = FXCollections.observableArrayList();

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
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    setTimbreDefaultValue();
                }
            }
        });

        dynamicsValLbl.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    setDynamicsDefaultValue();
                }
            }
        });

        pitchValLbl.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    setPitchDefaultValue();
                }
            }
        });

        presetsChob.setItems(overlayPresets);
        overlayPresets.addAll(getOverlayPresetValues());
        presetsChob.getSelectionModel().select(Consts.PRESET_ALL_OFF);
        presetsChob.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            String out  = newSelection;
            if (newSelection == null) {
                out = oldSelection;
            }
            onPresetsChobChange(out);
        });
    }

    @FXML
    private void initialize() {
        sectionsTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        sectionsTableView.setEditable(false);

        sectionOrderLvw.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        sectionOrderLvw.setEditable(false);
        sectionOrderLvw.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            String out  = newSelection;
            if (newSelection == null) {
                out = oldSelection;
            }
            onSectionSelect(out);
        });

        sectionColumn.setCellValueFactory(cellData -> cellData.getValue().sectionProperty());
        ownerColumn.setCellValueFactory(cellData -> cellData.getValue().ownerProperty());
        startPageColumn.setCellValueFactory(cellData -> cellData.getValue().startPageProperty().asObject());
        endPageColumn.setCellValueFactory(cellData -> cellData.getValue().endPageProperty().asObject());

        nextSectionLbl.textProperty().bind(nextSectionProp);
        playingSectionLbl.textProperty().bind(playingSectionProp);
    }

    private void onSectionSelect(String sectionName) {
        Section section = getSection(sectionName);
        if(section == null) {
            return;
        }
        onSectionSelect(section);
    }

    private void onSectionSelect(Section section) {
        if(section == null) {
            return;
        }
        setCurrentSectionLabel(section.getSection());
    }

    private void setCurrentSectionLabel(String value){
        if(value == null) {
            value = Consts.NAME_NA;
        }
        String old = playingSectionProp.getValue();
        if(value.equals(old)) {
            return;
        }
        playingSectionProp.setValue(value);
        selectSection(value);
    }

    private void selectSection(String value){
        Section section = getSection(value);
        if(section == null) {
            return;
        }
        Section selected = sectionsTableView.getSelectionModel().getSelectedItem();
        if(section.equals(selected)) {
            return;
        }
        sectionsTableView.getSelectionModel().select(section);

        String selectedOrder = sectionOrderLvw.getSelectionModel().getSelectedItem();
        if(value.equals(selectedOrder)) {
            return;
        }
        sectionOrderLvw.getSelectionModel().select(value);
    }

    private void setNextSectionLabel(String value){
        if(value == null) {
            value = Consts.NAME_NA;;
        }
        String old = nextSectionProp.getValue();
        if(value.equals(old)) {
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
        if(!(score instanceof BasicScore)) {
            return;
        }
        BasicScore szcore = (BasicScore)score;
        ScoreBuilderStrategy builderStrategy = szcore.getScoreBuilderStrategy();
        if(builderStrategy == null || !builderStrategy.isActive()) {
            return;
        }
        List<String> sectionNames = builderStrategy.getSections();
        if(sectionNames == null || sectionNames.isEmpty()) {
            return;
        }
        List<Section> guiSections = new ArrayList<>();
        for(String section : sectionNames) {
            Section guiSection = new Section();
            guiSection.setSection(section);
            String owner = builderStrategy.getSectionOwner(section);
            if(owner != null) {
                guiSection.setOwner(owner);
            } else {
                guiSection.setOwner(Consts.EMPTY);
            }
            IntRange pageRange = builderStrategy.getSectionPageRange(section);
            if(pageRange != null) {
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
        if(sectionInfos != null) {
            for(SectionInfo info : sectionInfos) {
                Section section = new Section();
                section.setSection(info.getSectionId());
                section.setOwner(info.getOwner());
                IntRange pageRange = info.getPageRange();
                if(pageRange != null) {
                    section.setStartPage(pageRange.getStart());
                    section.setEndPage(pageRange.getEnd());
                }
                addSection(section);
            }
        }

        if(isReady) {
            setSectionStatusStyle(Consts.READY, LABEL_GREEN, LABEL_RED);
        } else {
            setSectionStatusStyle(Consts.WAITING, LABEL_RED, LABEL_GREEN);
        }

        if(sectionOrder != null && !sectionOrder.isEmpty()) {
            setSectionOrderInfo(sectionOrder, currentSection, nextSection);
        }
    }

    private void setSectionOrderInfo(final List<String> sections, final String currentSection, final String nextSection) {
        Platform.runLater(() -> {
            String selectedItem = sectionOrderLvw.getSelectionModel().getSelectedItem();
            sectionOrder.clear();
            sectionOrder.addAll(sections);
            if(sections.contains(selectedItem)) {
                sectionOrderLvw.getSelectionModel().select(selectedItem);
            }
            setCurrentSectionLabel(currentSection);
            setNextSectionLabel(nextSection);
        });
    }

    private void setSectionStatusStyle(final String text, final String style, final String styleToRemove) {
        Platform.runLater(() -> {
            sectionsStatusLbl.setText(text);
            ObservableList<String> styleClass = sectionsStatusLbl.getStyleClass();
            styleClass.remove(styleToRemove);
            if(!styleClass.contains(style)) {
                styleClass.add(style);
            }
        });
    }

    public void addSection(Section section) {
        if (section == null) {
            return;
        }
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
            LOG.info("Updating Section: " + toUpdate);
            toUpdate.setSection(section.getSection());
            toUpdate.setOwner(section.getOwner());
            toUpdate.setStartPage(section.getStartPage());
            toUpdate.setEndPage(section.getEndPage());
        });
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
        if(playSectionName == null) {
            return;
        }
        sendSetSection(playSectionName);
        Section nextSection = getSection(playSectionName);
        if(nextSection == null) {
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
        StrategyEvent strategyEvent = eventFactory.createStrategyEvent(StrategyEventType.RESET_OWNERS, clock.getSystemTimeMillis());
        publisher.receive(strategyEvent);
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

    private void onUsePitchOverlay(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(!newValue) {
            usePitchLineChb.setSelected(false);
        }
        mainApp.sendPitchValueChange(Math.round(pitchSldr.getValue()), instrumentIds);
        mainApp.sendUsePitchOverlay(newValue, instrumentIds);
    }

    private void onUsePitchStaveOverlay(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        mainApp.sendUsePitchStaveOverlay(newValue, instrumentIds);
    }

    private void onUsePitchLine(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(newValue && !usePitchOverlayChb.isSelected()) {
            usePitchOverlayChb.setSelected(true);
//            usePitchStaveOverlayChb.setSelected(true);
        }
        mainApp.sendPitchValueChange(Math.round(pitchSldr.getValue()), instrumentIds);
        mainApp.sendUsePitchLine(newValue, instrumentIds);
    }

    private void onPitchValueChange(long newVal) {
        if(!usePitchOverlayChb.isSelected()) {
            return;
        }
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        mainApp.sendPitchValueChange(newVal, instrumentIds);
    }

    private void onUseDynamicsOverlay(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(!newValue) {
            useDynamicsLineChb.setSelected(false);
        }
        mainApp.sendDynamicsValueChange(Math.round(dynamicsSldr.getValue()), instrumentIds);
        mainApp.sendUseDynamicsOverlay(newValue, instrumentIds);
    }

    private void onUseDynamicsLine(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(newValue && !useDynamicsOverlayChb.isSelected()) {
            useDynamicsOverlayChb.setSelected(true);
        }
        mainApp.sendDynamicsValueChange(Math.round(dynamicsSldr.getValue()), instrumentIds);
        mainApp.sendUseDynamicsLine(newValue, instrumentIds);
    }

    private void onTimbreValueChange(long newVal) {
        if(!useTimbreOverlayChb.isSelected()) {
            return;
        }
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        mainApp.sendTimbreValueChange(newVal, instrumentIds);
    }

    private void onUseTimbreOverlay(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(!newValue) {
            useTimbreLineChb.setSelected(false);
        }
        mainApp.sendTimbreValueChange(Math.round(timbreSldr.getValue()), instrumentIds);
        mainApp.sendUseTimbreOverlay(newValue, instrumentIds);
    }

    private void onUseTimbreLine(Boolean newValue) {
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        if(newValue && !useTimbreOverlayChb.isSelected()) {
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

    private void onDynamicsValueChange(long newVal) {
        if(!useDynamicsOverlayChb.isSelected()) {
            return;
        }
        List<Id> instrumentIds = getInstrumentsToSend();
        if(instrumentIds.isEmpty()) {
            return;
        }
        mainApp.sendDynamicsValueChange(newVal, instrumentIds);
    }

    private List<Id> getInstrumentsToSend() {
        List<Id> instrumentIds;
        if(sendToAllChb.isSelected()) {
            instrumentIds = mainApp.getAllInstruments();
        } else {
            instrumentIds = mainApp.getSelectedInstruments();
        }
        return instrumentIds;
    }

    public void resetOverlays() {
        setTimbreDefaultValue();
        setDynamicsDefaultValue();
        setPitchDefaultValue();
        presetsChob.getSelectionModel().select(Consts.PRESET_ALL_OFF);
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
}
