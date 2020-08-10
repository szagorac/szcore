package com.xenaksys.szcore.gui.model;

import com.xenaksys.szcore.Consts;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WebscoreInstructions {
    private StringProperty line1 = new SimpleStringProperty(Consts.EMPTY);
    private StringProperty line2 = new SimpleStringProperty(Consts.EMPTY);
    private StringProperty line3 = new SimpleStringProperty(Consts.EMPTY);
    private BooleanProperty visible = new SimpleBooleanProperty(false);

    public String getLine1() {
        return line1.get();
    }

    public StringProperty line1Property() {
        return line1;
    }

    public void setLine1(String l1) {
        this.line1.set(l1);
    }

    public String getLine2() {
        return line2.get();
    }

    public StringProperty line2Property() {
        return line2;
    }

    public void setLine2(String l2) {
        this.line2.set(l2);
    }

    public String getLine3() {
        return line3.get();
    }

    public StringProperty line3Property() {
        return line3;
    }

    public void setLine3(String l3) {
        this.line3.set(l3);
    }

    public BooleanProperty getVisibleProperty() {
        return visible;
    }

    public Boolean getVisible() {
        return visible.get();
    }

    public void setVisible(Boolean isVisible) {
        this.visible.set(isVisible);
    }
}
