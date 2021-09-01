package com.xenaksys.szcore.integration;

import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.event.osc.PrecountBeatOffEvent;
import com.xenaksys.szcore.event.osc.PrecountBeatOnEvent;
import com.xenaksys.szcore.model.BeatTimeStrategy;
import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.Timer;
import com.xenaksys.szcore.model.WaitStrategy;
import com.xenaksys.szcore.model.WebPublisher;
import com.xenaksys.szcore.net.osc.OSCPortOut;
import com.xenaksys.szcore.publish.OscPublishProcessor;
import com.xenaksys.szcore.publish.WebPublisherProcessor;
import com.xenaksys.szcore.score.ScoreProcessorHandler;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.time.BasicScheduler;
import com.xenaksys.szcore.time.BasicTimer;
import com.xenaksys.szcore.time.TransportFactory;
import com.xenaksys.szcore.time.beatstrategy.SimpleBeatTimeStrategy;
import com.xenaksys.szcore.time.clock.MutableNanoClock;
import com.xenaksys.szcore.time.waitstrategy.BlockingWaitStrategy;
import com.xenaksys.szcore.web.WebProcessor;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class TestOscPublish {
    static final Logger LOG = LoggerFactory.getLogger(TestOscPublish.class);

    ScoreProcessor scoreProcessor;
    TransportFactory transportFactory;
    OscPublisher oscPublisher;
    WebPublisher webPublisher;
    WebProcessor webProcessor;

    boolean isSkip = true;

    @Before
    public void init(){

        WaitStrategy waitStrategy = new BlockingWaitStrategy(1, TimeUnit.MILLISECONDS);
        MutableClock clock = new MutableNanoClock();
        Timer timer = new BasicTimer(waitStrategy, clock);
        oscPublisher = new OscPublishProcessor();
        webPublisher = new WebPublisherProcessor();
        Scheduler scheduler = new BasicScheduler(clock, timer);
        BeatTimeStrategy beatTimeStrategy = new SimpleBeatTimeStrategy();
        transportFactory = new TransportFactory(clock, scheduler, beatTimeStrategy);

        EventFactory eventFactory = new EventFactory();
        TaskFactory taskFactory = new TaskFactory();
        Properties props  = new Properties();

        scoreProcessor = new ScoreProcessorHandler(transportFactory, clock, oscPublisher, webPublisher, scheduler, eventFactory, taskFactory, props);
    }

    @Test
    public void testRunScoreSingleInstruments(){

        if(isSkip){
            return;
        }
//        String filePath = "testScoreSingleInstrument.csv";
//        String filePath = "testScore10Clarinet.csv";
        String filePath = "testScore11Clarinet.csv";


        try {
            InetAddress clarinetAddr = InetAddress.getLocalHost();
            int remotePort = 7000;
            OSCPortOut clarinetPort = new OSCPortOut(clarinetAddr, remotePort);

            oscPublisher.addOscPort("Clarinet", clarinetPort);

            scoreProcessor.loadAndPrepare(filePath);

            scoreProcessor.setPosition(0);

            Thread.sleep(2000);

            scoreProcessor.play();

            Thread.sleep(1000000);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testRunScoreCello(){

        if(isSkip){
            return;
        }
//        String filePath = "testScoreSingleInstrument.csv";
//        String filePath = "testScore10Cello.csv";
        String filePath = "testScore11Cello.csv";


        try {
            InetAddress clarinetAddr = InetAddress.getLocalHost();
            int remotePort = 7000;
            OSCPortOut clarinetPort = new OSCPortOut(clarinetAddr, remotePort);

            oscPublisher.addOscPort("Cello", clarinetPort);

            scoreProcessor.loadAndPrepare(filePath);

            scoreProcessor.setPosition(0);

            Thread.sleep(2000);

            scoreProcessor.play();

            Thread.sleep(1000000);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testRunScoreMultiInstruments(){

        if(isSkip){
            return;
        }
        String filePath = "testScoreMultiInstrument.csv";

        try {
            InetAddress clarinetAddr = InetAddress.getLocalHost();
            InetAddress celloAdr = InetAddress.getByName("192.168.0.20");
            int remotePort = 7000;
            OSCPortOut clarinetPort = new OSCPortOut(clarinetAddr, remotePort);
            OSCPortOut celloPort = new OSCPortOut(celloAdr, remotePort);

            oscPublisher.addOscPort("Clarinet", clarinetPort);
            oscPublisher.addOscPort("Cello", celloPort);

            scoreProcessor.loadAndPrepare(filePath);

            scoreProcessor.setPosition(0);

            Thread.sleep(1000);

            scoreProcessor.play();

            Thread.sleep(1000000);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testLoadFiles(){
        if(isSkip){
            return;
        }
        OSCPortOut port = null;
        try {
            InetAddress address = InetAddress.getLocalHost();
//            InetAddress address = InetAddress.getByName("192.168.0.20");
            int remotePort = 7000;
            port = new OSCPortOut(address, remotePort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }


        List<Object> arguments = new ArrayList<>();;

        oscPublisher.addOscPort("DEFAULT_OSC_PORT", port);
        WaitStrategy waitStrategy = new BlockingWaitStrategy(25, TimeUnit.MILLISECONDS);


        arguments.clear();
        String address = "/ITL";
        arguments.add("load");
        arguments.add("Cello.inscore");
        OscEvent event = new OscEvent(address, arguments, 0l);
        oscPublisher.process(event);

        doSLeep(1000);

        arguments.clear();
        address = "/ITL";
        arguments.add("load");
        arguments.add("Clarinet.inscore");
        event = new OscEvent(address, arguments, 0l);
        oscPublisher.process(event);

        doSLeep(1000);

//        /ITL/scene/beater set "ellipse" 0.5 0.5;
//         set rect 0.5 1.5
        arguments.clear();
        address = "/ITL/scene/myObject";
        arguments.add("set");
        arguments.add("rect");
        arguments.add(new Float(0.5));
        arguments.add(new Float(1.5));
        event = new OscEvent(address, arguments, 0l);
        oscPublisher.process(event);

        doSLeep(1000);

        arguments.clear();
        address = "/ITL";
        arguments.add("load");
        arguments.add("Cello.inscore");
        event = new OscEvent(address, arguments, 0l);
        oscPublisher.process(event);


//        /ITL/scene/stave set img Ukodus9_2_Clarinet_page2.png
//        arguments.clear();
//        address = "/ITL/scene/stave";
//        arguments.add("set");
//        arguments.add("img");
//        arguments.add("Ukodus9_2_Clarinet_page2.png");
//        event = new OscEvent(address, arguments);
//        oscPublishProcessor.process(event);
//
//        doSLeep(1000);

//        arguments.clear();
//        address = "/ITL/scene/stave";
//        arguments.add("mapf");
//        arguments.add("Ukodus9_2_Clarinet_page1_InScoreMap.txt");
//        event = new OscEvent(address, arguments);
//        oscPublishProcessor.process(event);
//
////        Sending msg address: /ITL/scene/stave2 args: [set, file, Ukodus9_2_Clarinet_page2.png]
////       Sending msg address: /ITL/scene/stave2 args: [mapf, Ukodus9_2_Clarinet_page2_InScoreMap.txt]
//
//
//        arguments.clear();
//        address = "/ITL/scene/stave2";
//        arguments.add("set");
//        arguments.add("file");
//        arguments.add("Ukodus9_2_Clarinet_page2.png");
//        event = new OscEvent(address, arguments);
//        oscPublishProcessor.process(event);

//        arguments.clear();
//        address = "/ITL/scene/stave";
//        arguments.add("mapf");
//        arguments.add("Ukodus9_2_Clarinet_page1_InScoreMap.txt");
//        event = new OscEvent(address, arguments);
//        oscPublishProcessor.process(event);

//
//        address = "/ITL/scene/slaveBeat";
//        String address2 = "/ITL/scene/slaveFollow";
//        ArrayList<Object> dateArgs = new ArrayList<>();
//        dateArgs.add("date");
//        OscEvent dateEvent = new OscEvent(address, dateArgs);
//        OscEvent dateEvent2 = new OscEvent(address2, dateArgs);
//        String beat = "5/8";
////        double beat = 5.0/8;
//        dateArgs.add(1, beat);
////        dateArgs.add(2, 8);
//
//        oscPublishProcessor.process(dateEvent);
//        oscPublishProcessor.process(dateEvent2);
//
//        doSLeep(1000);
//
//        dateArgs.remove(1);
//        beat = "10/8";
////        beat = 10.0/8;
//        dateArgs.add(1, beat);
//
//        oscPublishProcessor.process(dateEvent);
//        oscPublishProcessor.process(dateEvent2);
//
//        doSLeep(1000);
//
//        dateArgs.remove(1);
//        beat = "15/8";
////        beat = 15.0/8;
//        dateArgs.add(1, beat);
//
//        oscPublishProcessor.process(dateEvent);
//        oscPublishProcessor.process(dateEvent2);
//
//        doSLeep(1000);


    }

    @Test
    public void testLoadStringFiles(){
        if(isSkip){
            return;
        }
        OSCPortOut port = null;
        try {
            InetAddress address = InetAddress.getLocalHost();
            ;
            int remotePort = 7000;
            port = new OSCPortOut(address, remotePort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }


        List<Object> arguments = new ArrayList<>();;

        oscPublisher.addOscPort("DEFAULT_OSC_PORT", port);
        WaitStrategy waitStrategy = new BlockingWaitStrategy(25, TimeUnit.MILLISECONDS);

        arguments.clear();
        String address = "/ITL";
        arguments.add("load");
        arguments.add("Clarinet.inscore");
        OscEvent event = new OscEvent(address, arguments, 0l);
        oscPublisher.process(event);

        doSLeep(1000);


//        /ITL/scene/stave set img Ukodus9_2_Clarinet_page2.png
        arguments.clear();
        address = "/ITL/scene/stave";
        arguments.add("set");
        arguments.add("img");
        arguments.add("Ukodus9_2_Clarinet_page2.png");
        event = new OscEvent(address, arguments, 0l);
        oscPublisher.process(event);

        doSLeep(1000);

        arguments.clear();
        address = "/ITL/scene/stave";
        arguments.add("set");
        arguments.add("img");
        arguments.add("Ukodus9_2_Clarinet_page1.png");
        event = new OscEvent(address, arguments, 0l);
        oscPublisher.process(event);

        doSLeep(1000);

    }

    @Test
    public void testRunBeaterEvents(){

        if(isSkip){
            return;
        }

        try {
            InetAddress clarinetAddr = InetAddress.getLocalHost();
            int remotePort = 7000;
            OSCPortOut clarinetPort = new OSCPortOut(clarinetAddr, remotePort);

            String destination = "Clarinet";
            oscPublisher.addOscPort(destination, clarinetPort);

            EventFactory eventFactory = new EventFactory();
            PrecountBeatOnEvent eventOn = eventFactory.createPrecountBeatOnEvent(destination, 0l);
            eventOn.addCommandArg(3, 4);

            doSLeep(3000);

            oscPublisher.process(eventOn);

            doSLeep(1000);

            PrecountBeatOffEvent eventOff = eventFactory.createPrecountBeatOffEvent(destination, 0l);
            eventOff.addCommandArg(3);
            oscPublisher.process(eventOff);


            doSLeep(1000);
            eventOn.addCommandArg(2, 2);
            oscPublisher.process(eventOn);

            doSLeep(1000);
            eventOff.addCommandArg(2);
            oscPublisher.process(eventOff);

            doSLeep(1000);
            eventOn.addCommandArg(1, 1);
            oscPublisher.process(eventOn);

            doSLeep(1000);
            eventOff.addCommandArg(1);
            oscPublisher.process(eventOff);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void doSLeep(long millis){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
