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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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

    private SzcoreClient mainApp;
    private EventService publisher;
    private ScoreService scoreService;
    private Clock clock;
    private ObservableList<Section> sections = FXCollections.observableArrayList();
    private ObservableList<String> sectionOrder = FXCollections.observableArrayList();
    private StringProperty nextSectionProp = new SimpleStringProperty("N/A");
    private StringProperty playingSectionProp = new SimpleStringProperty("N/A");

    public void setMainApp(SzcoreClient mainApp) {
        this.mainApp = mainApp;
    }


    public void populate() {
        sectionsTableView.setItems(sections);
        sectionOrderLvw.setItems(sectionOrder);
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
        if(builderStrategy == null) {
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
}
