package com.xenaksys.szcore.gui.model;

import com.xenaksys.szcore.Consts;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.net.InetAddress;

public class NetworkClient {
    private ObjectProperty<InetAddress> inetAddress = new SimpleObjectProperty<>();
    private StringProperty hostAddress = new SimpleStringProperty(Consts.EMPTY);
    private StringProperty hostName = new SimpleStringProperty(Consts.EMPTY);
    private BooleanProperty isParticipant = new SimpleBooleanProperty(false);

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

    public String getHostName() {
        return hostName.get();
    }

    public StringProperty getHostNameProperty() {
        return hostName;
    }

    public void setHostName(String instrument) {
        this.hostName.set(instrument);
    }

    public BooleanProperty getIsParticipantProperty() {
        return isParticipant;
    }

    public Boolean getIsParticipant() {
        return isParticipant.get();
    }

    public void setIsParticipant(Boolean select) {
        this.isParticipant.set(select);
    }

    @Override
    public String toString() {
        return "NetworkClient{" +
                "hostAddress=" + hostAddress +
                ", hostName='" + hostName + '\'' +
                ", isParticipant=" + isParticipant +
                '}';
    }
}
