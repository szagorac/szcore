package com.xenaksys.szcore.gui.model;

import com.xenaksys.szcore.Consts;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.net.InetAddress;

public class Participant {
    private ObjectProperty<InetAddress> inetAddress = new SimpleObjectProperty<>();
    private StringProperty hostAddress = new SimpleStringProperty(Consts.EMPTY);
    private IntegerProperty portIn = new SimpleIntegerProperty(0);
    private IntegerProperty portOut = new SimpleIntegerProperty(0);
    private IntegerProperty portErr = new SimpleIntegerProperty(0);
    private DoubleProperty ping = new SimpleDoubleProperty(0.0);
    private StringProperty instrument = new SimpleStringProperty(Consts.EMPTY);
    private BooleanProperty select = new SimpleBooleanProperty(false);

    public InetAddress getInetAddress() {
        return inetAddress.get();
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress.set(inetAddress);
    }

    public String getHostAddress() {
        return hostAddress.get();
    }

    public StringProperty getHostAddressProperty() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress.set(hostAddress);
    }

    public int getPortIn() {
        return portIn.get();
    }

    public IntegerProperty getPortInProperty() {
        return portIn;
    }

    public void setPortIn(int portIn) {
        this.portIn.set(portIn);
    }

    public int getPortOut() {
        return portOut.get();
    }

    public IntegerProperty getPortOutProperty() {
        return portOut;
    }

    public void setPortOut(int portOut) {
        this.portOut.set(portOut);
    }

    public int getPortErr() {
        return portErr.get();
    }

    public IntegerProperty getPortErrProperty() {
        return portErr;
    }

    public void setPortErr(int portErr) {
        this.portErr.set(portErr);
    }

    public double getPing() {
        return ping.get();
    }

    public DoubleProperty getPingProperty() {
        return ping;
    }

    public void setPing(double ping) {
        this.ping.set(ping);
    }

    public String getInstrument() {
        return instrument.get();
    }

    public StringProperty getInstrumentProperty() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument.set(instrument);
    }

    public BooleanProperty getSelectProperty() {
        return select;
    }

    public Boolean getSelect() {
        return select.get();
    }

    public void setSelect(Boolean select) {
        this.select.set(select);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participant)) return false;

        Participant that = (Participant) o;

        return getHostAddress().equals(that.getHostAddress());
    }

    @Override
    public int hashCode() {
        return hostAddress.hashCode();
    }

    @Override
    public String toString() {
        return "Participant{" +
                "inetAddress=" + inetAddress +
                ", hostAddress='" + hostAddress + '\'' +
                ", portIn=" + portIn +
                ", portOut=" + portOut +
                ", portErr=" + portErr +
                ", instrument='" + instrument + '\'' +
                '}';
    }
}
