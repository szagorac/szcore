package com.xenaksys.szcore.publish;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.OscEvent;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.WaitStrategy;
import com.xenaksys.szcore.net.osc.OSCPortOut;
import com.xenaksys.szcore.time.waitstrategy.BlockingWaitStrategy;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OscPublishTest {
    static final Logger LOG = LoggerFactory.getLogger(OscPublishTest.class);


    OscPublisher oscPublisher;
    WaitStrategy waitStrategy;
    WaitStrategy beatWaitStrategy;

    @Before
    public void init(){

        try {
            InetAddress address = InetAddress.getLocalHost();
            ;
            int remotePort = 7000;
            OSCPortOut oscPort = new OSCPortOut(address, remotePort);
            Map<String, OSCPortOut> oscPublishPorts = new HashMap<>();
            oscPublishPorts.put(Consts.DEFAULT_OSC_PORT_NAME, oscPort);
            oscPublisher = new OscPublishProcessor(oscPublishPorts);

            waitStrategy = new BlockingWaitStrategy(25, TimeUnit.MILLISECONDS);
            beatWaitStrategy = new BlockingWaitStrategy(250, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testLoadScore(){

        String address = "/ITL";
        List<Object> arguments = new ArrayList<>();
        arguments.add("load");
        arguments.add("Clarinetp1-5.inscore");
        OscEvent event = new OscEvent(address, arguments, 0l);

        oscPublisher.process(event);
    }

    @Test
    public void testTicks(){

        String address = "/ITL/scene/slaveFollow";
        String address2 = "/ITL/scene/slaveBeat";
        List<Object> arguments = new ArrayList<>();
        arguments.add("clock");
        OscEvent event = new OscEvent(address, arguments, 0l);
        OscEvent event2 = new OscEvent(address2, arguments, 0l);


        for(int i = 0; i < 100; i += 25) {

            long start = System.currentTimeMillis();
            oscPublisher.process(event);
            oscPublisher.process(event2);
            long end = System.currentTimeMillis();
            long diff = end - start;
            if(diff > 1) {
                LOG.info("Spent in OSC millis: " + diff);
            }

            waitStrategy.doWait();

        }
    }

    @Test
    public void testBeats(){
        String address = "/ITL/scene/slaveBeat";
        String address2 = "/ITL/scene/slaveFollow";
        ArrayList<Object> arguments = new ArrayList<>();
        arguments.add("date");
        OscEvent event = new OscEvent(address, arguments, 0l);
        OscEvent event2 = new OscEvent(address2, arguments, 0l);


        for(int i = 0; i < 4; i++) {
            if(arguments.size() == 2) {
                arguments.remove(1);
            }
            String beat = i + "\\8";
            arguments.add(1, beat);

            long start = System.currentTimeMillis();

            oscPublisher.process(event);
            oscPublisher.process(event2);

            long end = System.currentTimeMillis();
            long diff = end - start;
            if(diff > 1) {
                LOG.info("Spent in OSC millis: " + diff);
            }

            beatWaitStrategy.doWait();

        }
    }

    @Test
    public void testStave2Beats(){
        String address = "/ITL/scene/slaveBeat2";
        String address2 = "/ITL/scene/slaveFollow2";
        ArrayList<Object> arguments = new ArrayList<>();
        arguments.add("date");
        OscEvent event = new OscEvent(address, arguments, 0l);
        OscEvent event2 = new OscEvent(address2, arguments, 0l);


        for(int i = 41; i < 45; i++) {
            if(arguments.size() == 2) {
                arguments.remove(1);
            }
            String beat = i + "\\8";
            arguments.add(1, beat);

            long start = System.currentTimeMillis();

            oscPublisher.process(event);
            oscPublisher.process(event2);

            long end = System.currentTimeMillis();
            long diff = end - start;
            if(diff > 1) {
                LOG.info("Spent in OSC millis: " + diff);
            }

            beatWaitStrategy.doWait();

        }
    }

    @Test
    public void testTwoStaveBeats(){
        String address = "/ITL/scene/slaveBeat";
        String address2 = "/ITL/scene/slaveFollow";
        String address3 = "/ITL/scene/slaveBeat2";
        String address4 = "/ITL/scene/slaveFollow2";
        ArrayList<Object> arguments = new ArrayList<>();
        arguments.add("date");
        OscEvent event = new OscEvent(address, arguments, 0l);
        OscEvent event2 = new OscEvent(address2, arguments, 0l);
        OscEvent event3 = new OscEvent(address3, arguments, 0l);
        OscEvent event4 = new OscEvent(address4, arguments, 0l);

        for(int i = 0; i < 4; i++) {
            if(arguments.size() == 2) {
                arguments.remove(1);
            }
            String beat = i + "\\8";
            arguments.add(1, beat);

            long start = System.currentTimeMillis();

            oscPublisher.process(event);
            oscPublisher.process(event2);
            oscPublisher.process(event3);
            oscPublisher.process(event4);

            long end = System.currentTimeMillis();
            long diff = end - start;
            if(diff > 1) {
                LOG.info("Spent in OSC millis: " + diff);
            }

            beatWaitStrategy.doWait();

        }
    }

    @Test
    public void testTwoCLocks(){
        ArrayList<Object> argumentsDate = new ArrayList<>();
        ArrayList<Object> argumentsClock = new ArrayList<>();

        String address = "/ITL/scene/slaveBeat";
        String address2 = "/ITL/scene/slaveFollow";
        String address3 = "/ITL/scene/slaveFollow2";
        String address4 = "/ITL/scene/slaveBeat2";

        argumentsDate.clear();
        argumentsDate.add("date");
        argumentsDate.add("1/8");
        OscEvent eventDate = new OscEvent(address, argumentsDate, 0l);
        OscEvent eventDate2 = new OscEvent(address2, argumentsDate, 0l);
        OscEvent eventDate3 = new OscEvent(address3, argumentsDate, 0l);
        OscEvent eventDate4 = new OscEvent(address4, argumentsDate, 0l);
        oscPublisher.process(eventDate);
        oscPublisher.process(eventDate2);
        oscPublisher.process(eventDate3);
        oscPublisher.process(eventDate4);

        List<OscEvent> events = new ArrayList<>();
        argumentsClock = new ArrayList<>();
        argumentsClock.add("clock");

        OscEvent event = new OscEvent(address, argumentsClock, 0l);
        events.add(event);
        OscEvent event2 = new OscEvent(address2, argumentsClock, 0l);
        events.add(event2);
        OscEvent event3 = new OscEvent(address3, argumentsClock, 0l);
        events.add(event3);
        OscEvent event4 = new OscEvent(address4, argumentsClock, 0l);
        events.add(event4);


        for(int i = 0; i < 10; i++) {
            if(i == 481){
                argumentsDate.clear();
                argumentsDate.add("date");
                argumentsDate.add("41/8");
                oscPublisher.process(eventDate);
                oscPublisher.process(eventDate2);
                oscPublisher.process(eventDate3);
                oscPublisher.process(eventDate4);

            }

            for (OscEvent e : events) {
                oscPublisher.process(e);
            }

            waitStrategy.doWait();
        }
    }

    @Test
    public void testRewind(){
        //      /ITL/scene/slaveBeat date 0/8
        //      /ITL/scene/slaveFollow date 0/8

        String address = "/ITL/scene/slaveBeat";
        String address2 = "/ITL/scene/slaveFollow";
        ArrayList<Object> arguments = new ArrayList<>();
        arguments.add("date");
        arguments.add("0/8");
        OscEvent event = new OscEvent(address, arguments, 0l);
        OscEvent event2 = new OscEvent(address2, arguments, 0l);

        oscPublisher.process(event);
        oscPublisher.process(event2);
     }

}
