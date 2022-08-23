package com.xenaksys.szcore.gui.model;

import com.xenaksys.szcore.Consts;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class Movement {
    private StringProperty movement = new SimpleStringProperty(Consts.EMPTY);
    private IntegerProperty startPage = new SimpleIntegerProperty(0);
    private IntegerProperty endPage = new SimpleIntegerProperty(0);

    public String getMovement() {
        return movement.get();
    }

    public StringProperty movementProperty() {
        return movement;
    }

    public void setMovement(String movement) {
        this.movement.set(movement);
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movement section1 = (Movement) o;
        return movement.getValue().equals(section1.movement.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(movement);
    }

    @Override
    public String toString() {
        return "Section{" +
                "section=" + movement +
                ", startPage=" + startPage +
                ", endPage=" + endPage +
                '}';
    }
}
