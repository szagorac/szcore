package com.xenaksys.szcore.gui.model;

import com.xenaksys.szcore.Consts;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class Section {
    private StringProperty section = new SimpleStringProperty(Consts.EMPTY);
    private StringProperty owner = new SimpleStringProperty(Consts.EMPTY);
    private IntegerProperty startPage = new SimpleIntegerProperty(0);
    private IntegerProperty endPage = new SimpleIntegerProperty(0);
    private IntegerProperty voteNo = new SimpleIntegerProperty(0);
    private IntegerProperty minVote = new SimpleIntegerProperty(0);
    private IntegerProperty maxVote = new SimpleIntegerProperty(0);
    private IntegerProperty avgVote = new SimpleIntegerProperty(0);
    private IntegerProperty voterNo = new SimpleIntegerProperty(0);
    private BooleanProperty isOwnerValid = new SimpleBooleanProperty(true);

    public String getSection() {
        return section.get();
    }

    public StringProperty sectionProperty() {
        return section;
    }

    public void setSection(String section) {
        this.section.set(section);
    }

    public String getOwner() {
        return owner.get();
    }

    public StringProperty ownerProperty() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner.set(owner);
    }

    public int getStartPage() {
        return startPage.get();
    }

    public IntegerProperty startPageProperty() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage.set(startPage);
    }

    public int getEndPage() {
        return endPage.get();
    }

    public IntegerProperty endPageProperty() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage.set(endPage);
    }

    public int getVoteNo() {
        return voteNo.get();
    }

    public IntegerProperty voteNoProperty() {
        return voteNo;
    }

    public void setVoteNo(int voteNo) {
        this.voteNo.set(voteNo);
    }

    public int getMinVote() {
        return minVote.get();
    }

    public IntegerProperty minVoteProperty() {
        return minVote;
    }

    public void setMinVote(int minVote) {
        this.minVote.set(minVote);
    }

    public int getMaxVote() {
        return maxVote.get();
    }

    public IntegerProperty maxVoteProperty() {
        return maxVote;
    }

    public void setMaxVote(int maxVote) {
        this.maxVote.set(maxVote);
    }

    public int getAvgVote() {
        return avgVote.get();
    }

    public IntegerProperty avgVoteProperty() {
        return avgVote;
    }

    public void setAvgVote(int avgVote) {
        this.avgVote.set(avgVote);
    }

    public int getVoterNo() {
        return voterNo.get();
    }

    public IntegerProperty voterNoProperty() {
        return voterNo;
    }

    public void setVoterNo(int voterNo) {
        this.voterNo.set(voterNo);
    }

    public boolean isIsOwnerValid() {
        return isOwnerValid.get();
    }

    public BooleanProperty isOwnerValidProperty() {
        return isOwnerValid;
    }

    public void setIsOwnerValid(boolean isOwnerValid) {
        this.isOwnerValid.set(isOwnerValid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section1 = (Section) o;
        return section.getValue().equals(section1.section.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(section);
    }

    @Override
    public String toString() {
        return "Section{" +
                "section=" + section +
                ", owner=" + owner +
                ", startPage=" + startPage +
                ", endPage=" + endPage +
                ", voteNo=" + voteNo +
                ", minVote=" + minVote +
                ", maxVote=" + maxVote +
                ", avgVote=" + avgVote +
                '}';
    }
}
