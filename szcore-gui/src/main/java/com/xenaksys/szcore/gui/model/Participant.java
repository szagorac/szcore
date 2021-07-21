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
import java.util.Objects;

public class Participant {
    private ObjectProperty<InetAddress> inetAddress = new SimpleObjectProperty<>();
    private StringProperty hostAddress = new SimpleStringProperty(Consts.EMPTY);
    private IntegerProperty portIn = new SimpleIntegerProperty(0);
    private IntegerProperty portOut = new SimpleIntegerProperty(0);
    private IntegerProperty portErr = new SimpleIntegerProperty(0);
    private DoubleProperty ping = new SimpleDoubleProperty(0.0);
    private StringProperty instrument = new SimpleStringProperty(Consts.EMPTY);
    private BooleanProperty select = new SimpleBooleanProperty(false);
    private BooleanProperty expired = new SimpleBooleanProperty(false);
    private StringProperty lastPingTime = new SimpleStringProperty(Consts.EMPTY);
    private BooleanProperty isWebClient = new SimpleBooleanProperty(false);
    private BooleanProperty isReady = new SimpleBooleanProperty(false);

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

    public BooleanProperty getExpiredProperty() {
        return expired;
    }

    public Boolean getExpired() {
        return expired.get();
    }

    public void setExpired(Boolean select) {
        this.expired.set(select);
    }

    public String getLastPingTime() {
        return lastPingTime.get();
    }

    public StringProperty getLastPingMillisProperty() {
        return lastPingTime;
    }

    public void setLastPingTime(String lastPing) {
        this.lastPingTime.set(lastPing);
    }

    public boolean getIsWebClient() {
        return isWebClient.get();
    }

    public BooleanProperty isWebClientProperty() {
        return isWebClient;
    }

    public void setIsWebClient(boolean isWebClient) {
        this.isWebClient.set(isWebClient);
    }

    public boolean getIsReady() {
        return isReady.get();
    }

    public BooleanProperty readyProperty() {
        return isReady;
    }

    public void setIsReady(boolean isReady) {
        this.isReady.set(isReady);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return hostAddress.getValue().equals(that.hostAddress.getValue()) &&
                portIn.getValue().equals(that.portIn.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostAddress, portIn);
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
