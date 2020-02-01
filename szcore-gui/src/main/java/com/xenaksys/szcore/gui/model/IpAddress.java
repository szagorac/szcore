package com.xenaksys.szcore.gui.model;

import com.xenaksys.szcore.Consts;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class IpAddress {
    private IntegerProperty octet1 = new SimpleIntegerProperty(0);
    private IntegerProperty octet2 = new SimpleIntegerProperty(0);
    private IntegerProperty octet3 = new SimpleIntegerProperty(0);
    private IntegerProperty octet4 = new SimpleIntegerProperty(0);

    public int getOctet1() {
        return octet1.get();
    }

    public IntegerProperty getOctet1Property() {
        return octet1;
    }

    public void setOctet1(int oct) {
        this.octet1.set(validate(oct));
    }

    public int getOctet2() {
        return octet2.get();
    }

    public IntegerProperty getOctet2Property() {
        return octet2;
    }

    public void setOctet2(int oct) {
        this.octet2.set(validate(oct));
    }

    public int getOctet3() {
        return octet3.get();
    }

    public IntegerProperty getOctet3Property() {
        return octet3;
    }

    public void setOctet3(int oct) {
        this.octet3.set(validate(oct));
    }

    public int getOctet4() {
        return octet4.get();
    }

    public IntegerProperty getOctet4Property() {
        return octet4;
    }

    public void setOctet4(int oct) {
        this.octet4.set(validate(oct));
    }

    public void setIpAddress(String ipAddr) throws Exception {
        String[] octets = ipAddr.split(Consts.DOT_REGEX);
        if(octets.length > 0) {
            setOctet1(convert(octets[0]));
        }
        if(octets.length > 1) {
            setOctet2(convert(octets[1]));
        }
        if(octets.length > 2) {
            setOctet3(convert(octets[2]));
        }
        if(octets.length > 3) {
            setOctet4(convert(octets[3]));
        }
    }

    public String toString() {
        return getOctet1() + Consts.DOT + getOctet2() + Consts.DOT + getOctet3() + Consts.DOT + getOctet4();
    }

    public int convert(String oct) throws Exception {
        if(oct == null || oct.isEmpty()) {
            return 0;
        }
        return validate(Integer.parseInt(oct));
    }

    private int validate(int octet) {
        if( octet < 0 ) {
            return 0;
        } else {
            return Math.min(octet, 255);
        }
    }

}
