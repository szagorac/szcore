package com.xenaksys.szcore.gui.model;

import com.xenaksys.szcore.Consts;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class AudienceClient {
    private String id;
    private StringProperty hostAddress = new SimpleStringProperty(Consts.EMPTY);
    private StringProperty hostName = new SimpleStringProperty(Consts.EMPTY);
    private IntegerProperty port = new SimpleIntegerProperty(0);
    private StringProperty browser = new SimpleStringProperty(Consts.EMPTY);
    private BooleanProperty isMobile = new SimpleBooleanProperty(false);
    private StringProperty os = new SimpleStringProperty(Consts.EMPTY);

    public AudienceClient(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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

    public IntegerProperty getPortProperty() {
        return port;
    }

    public Integer getPort() {
        return port.get();
    }

    public void setPort(Integer port) {
        this.port.set(port);
    }

    public String getBrowser() {
        return browser.get();
    }

    public StringProperty browserProperty() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser.set(browser);
    }

    public boolean getIsMobile() {
        return isMobile.get();
    }

    public BooleanProperty isMobileProperty() {
        return isMobile;
    }

    public void setIsMobile(boolean isMobile) {
        this.isMobile.set(isMobile);
    }

    public String getOs() {
        return os.get();
    }

    public StringProperty osProperty() {
        return os;
    }

    public void setOs(String os) {
        this.os.set(os);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AudienceClient that = (AudienceClient) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AudienceClient{" +
                "id=" + id +
                ", hostAddress=" + hostAddress +
                ", hostName=" + hostName +
                ", port=" + port +
                '}';
    }
}
