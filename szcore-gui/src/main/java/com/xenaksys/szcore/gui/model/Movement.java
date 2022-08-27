package com.xenaksys.szcore.gui.model;

import com.xenaksys.szcore.Consts;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Objects;

public class Movement {
    private StringProperty id = new SimpleStringProperty(Consts.EMPTY);
    private IntegerProperty firstPage = new SimpleIntegerProperty(0);
    private IntegerProperty lastPage = new SimpleIntegerProperty(0);
    private IntegerProperty startPage = new SimpleIntegerProperty(0);
    private BooleanProperty isActive = new SimpleBooleanProperty(false);
    private ListProperty<MovementSection> sections = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<String> sectionOrder = new SimpleListProperty<>(FXCollections.observableArrayList());

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public int getFirstPage() {
        return firstPage.get();
    }

    public IntegerProperty firstPageProperty() {
        return firstPage;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage.set(firstPage);
    }

    public int getLastPage() {
        return lastPage.get();
    }

    public IntegerProperty lastPageProperty() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage.set(lastPage);
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

    public ObservableList<MovementSection> getSections() {
        return sections.get();
    }

    public ListProperty<MovementSection> sectionsProperty() {
        return sections;
    }

    public void addSection(MovementSection section) {
        this.sections.add(section);
    }

    public void setSections(ObservableList<MovementSection> sections) {
        this.sections.set(sections);
    }

    public boolean isIsActive() {
        return isActive.get();
    }

    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive.set(isActive);
    }

    public ObservableList<String> getSectionOrder() {
        return sectionOrder.get();
    }

    public ListProperty<String> sectionOrderProperty() {
        return sectionOrder;
    }

    public void addSectionOrder(List<String> sectionOrder) {
        this.sectionOrder.clear();
        this.sectionOrder.addAll(sectionOrder);
    }

    public void setSectionOrder(ObservableList<String> sectionOrder) {
        this.sectionOrder.set(sectionOrder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movement section1 = (Movement) o;
        return id.getValue().equals(section1.id.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Section{" +
                "section=" + id +
                ", startPage=" + firstPage +
                ", endPage=" + lastPage +
                '}';
    }
}
