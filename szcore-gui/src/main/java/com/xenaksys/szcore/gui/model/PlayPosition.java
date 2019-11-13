package com.xenaksys.szcore.gui.model;

import com.xenaksys.szcore.Consts;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PlayPosition {

    private StringProperty pageNo = new SimpleStringProperty(Consts.DOUBLE_UNDERSCORE);
    private StringProperty barNo = new SimpleStringProperty(Consts.DOUBLE_UNDERSCORE);
    private StringProperty beatNo = new SimpleStringProperty(Consts.DOUBLE_UNDERSCORE);
    private StringProperty baseBeatNo = new SimpleStringProperty(Consts.DOUBLE_UNDERSCORE);
    private StringProperty tickNo = new SimpleStringProperty(Consts.DOUBLE_UNDERSCORE);

    public String getPageNo() {
        return pageNo.get();
    }

    public StringProperty pageNoProperty() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo.set(pageNo);
    }

    public void setPageNo(int pageNo) {
        this.pageNo.set(String.valueOf(pageNo));
    }

    public String getBarNo() {
        return barNo.get();
    }

    public StringProperty barNoProperty() {
        return barNo;
    }

    public void setBarNo(String barNo) {
        this.barNo.set(barNo);
    }

    public void setBarNo(int barNo) {
        this.barNo.set(String.valueOf(barNo));
    }

    public String getBeatNo() {
        return beatNo.get();
    }

    public StringProperty beatNoProperty() {
        return beatNo;
    }

    public void setBeatNo(String beatNo) {
        this.beatNo.set(beatNo);
    }

    public void setBeatNo(int beatNo) {
        this.beatNo.set(String.valueOf(beatNo));
    }

    public String getBaseBeatNo() {
        return baseBeatNo.get();
    }

    public StringProperty baseBeatNoProperty() {
        return baseBeatNo;
    }

    public void setBaseBeatNo(String baseBeatNo) {
        this.baseBeatNo.set(baseBeatNo);
    }

    public void setBaseBeatNo(int baseBeatNo) {
        this.baseBeatNo.set(String.valueOf(baseBeatNo));
    }

    public String getTickNo() {
        return tickNo.get();
    }

    public StringProperty tickNoProperty() {
        return tickNo;
    }

    public void setTickNo(String tickNo) {
        this.tickNo.set(tickNo);
    }

    public void setTickNo(int tickNo) {
        this.tickNo.set(String.valueOf(tickNo));
    }
}
