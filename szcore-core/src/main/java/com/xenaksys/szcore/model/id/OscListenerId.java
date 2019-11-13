package com.xenaksys.szcore.model.id;


import com.xenaksys.szcore.model.Id;

public class OscListenerId implements Id {
    private final int port;
    private final String address;
    private final String name;

    public OscListenerId(int port, String address, String name) {
        this.port = port;
        this.address = address;
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OscListenerId)) return false;

        OscListenerId that = (OscListenerId) o;

        if (port != that.port) return false;
        if (!address.equals(that.address)) return false;
        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = port;
        result = 31 * result + address.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "OscListenerId{" +
                "port=" + port +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
