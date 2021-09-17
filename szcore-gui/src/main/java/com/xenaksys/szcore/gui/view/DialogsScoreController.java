package com.xenaksys.szcore.gui.view;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.ScoreBuilderStrategy;
import com.xenaksys.szcore.gui.SzcoreClient;
import com.xenaksys.szcore.gui.model.Section;
import com.xenaksys.szcore.model.EventService;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreService;
import com.xenaksys.szcore.model.SectionInfo;
import com.xenaksys.szcore.score.BasicScore;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DialogsScoreController {
    static final Logger LOG = LoggerFactory.getLogger(ScoreController.class);

    public static final String SCORE_NAME = "Dialogs";

    @FXML
    private TableView<Section> sectionsTableView;
    @FXML
    private TableColumn<Section, String> sectionColumn;
    @FXML
    private TableColumn<Section, String> ownerColumn;
    @FXML
    private TableColumn<Section, Integer> startPageColumn;
    @FXML
    private TableColumn<Section, Integer> endPageColumn;

    private SzcoreClient mainApp;
    private EventService publisher;
    private ScoreService scoreService;
    private ScoreController scoreController;
    private ObservableList<Section> sections = FXCollections.observableArrayList();

    public void setMainApp(SzcoreClient mainApp) {
        this.mainApp = mainApp;
    }


    public void populate() {
        sectionsTableView.setItems(sections);
    }

    @FXML
    private void initialize() {
        sectionsTableView.setSelectionModel(null);
        sectionsTableView.setEditable(false);

        sectionColumn.setCellValueFactory(cellData -> cellData.getValue().sectionProperty());
        ownerColumn.setCellValueFactory(cellData -> cellData.getValue().ownerProperty());
        startPageColumn.setCellValueFactory(cellData -> cellData.getValue().startPageProperty().asObject());
        endPageColumn.setCellValueFactory(cellData -> cellData.getValue().endPageProperty().asObject());
    }

    public void setScoreService(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    public void setPublisher(EventService publisher) {
        this.publisher = publisher;
    }

    public void setScoreController(ScoreController scoreController) {
        this.scoreController = scoreController;
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

    public void onSectionInfo(List<SectionInfo> sectionInfos, List<String> sectionOrder, boolean isReady) {
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
        //TODO section order and ready
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
}
