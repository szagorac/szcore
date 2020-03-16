package com.xenaksys.szcore.server;

import com.xenaksys.szcore.util.NetUtil;
import com.xenaksys.szcore.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements Runnable {
    static final Logger LOG = LoggerFactory.getLogger(Server.class);

    protected static final int TICK_INTERVAL = 1000;
    private static final String PROP_LOCATION = "location";
    private static final String PROP_INSTANCE = "instance";

    protected static volatile Server instance;
    protected static volatile Properties properties = loadProperties();
    protected static volatile String location = loadLocation();
    protected static volatile int instanceId = loadInstanceId();

    private final String id;
    protected String name;
    protected volatile boolean isStop;
    protected volatile boolean isStarted;
    protected InetAddress serverAddress;

    protected List<ServerStatusListener> statusListeners = new CopyOnWriteArrayList<>();

    protected Server(String id) {
        this.id = id;
        setName(id);
    }

    public static void createInstanceAndRun(Server server) {
        createInstance(server);
        configureAndRun();
    }

    public static Server createInstance(Server server) {
        if (instance != null) {
            stopServer();
        }
        instance = server;
        return instance;
    }

    public static void stopServer() {
        if (instance != null) {
            instance.stop();
            instance.isStarted = false;
        }
    }

    public static Server getInstance() {
        return instance;
    }

    public static String getLocation() {
        if (location != null && location.length() > 0) {
            return location;
        }
        return null;
    }

    public static int getInstanceId() {
        return instanceId;
    }

    public static void configureAndRun() {
        try {
            if (instance == null) {
                LOG.error("Invalid NULL instance");
                return;
            }

            instance.configureInternal();
            instance.startInternal();
        } catch (Exception e) {
            LOG.error("Failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void configureInternal() throws Exception {

        serverAddress = NetUtil.getHostAddress();

        registerShutdownHandler();

        properties = loadProperties();

        configure();
    }

    protected void configure() throws Exception {

    }

    private void startInternal() throws Exception {
        LOG.info("Starting in Location: " + getLocation() + " instance id: " + getInstanceId());
        onStart();
        isStarted = true;
        notifyListenersOnStart();
        Thread worker = new Thread(this);
        worker.start();
    }

    protected void onStart() throws Exception {

    }

    public static Properties getProperties() {
        return properties;
    }

    public void run() {
        while (!isStop) {
            try {
                Thread.sleep(TICK_INTERVAL);
                tick();
            } catch (InterruptedException e) {
                logInfo("Main thread interrupted");
            }
        }

        logInfo("Exiting process ");
    }

    protected void tick() {

    }

    private void notifyListenersOnStart() {
        for (ServerStatusListener listener : statusListeners) {
            listener.onServerStart();
        }
    }

    private void notifyListenersOnStop() {
        for (ServerStatusListener listener : statusListeners) {
            listener.onServerStop();
        }
    }

    public void registerServerStatusListener(ServerStatusListener listener) {
        statusListeners.add(listener);
    }

    private void registerShutdownHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Inside Shut Down Hook");
                stopServer();
            }
        });
        LOG.info("Shut Down Hook Attached.");
    }

    public static String getSystemProperty(String key, String defaultValue) {
        final String value = System.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public void stop() {
        if (isStop) {
            return;
        }
        logInfo("Stopping " + getName() + " ... ");
        notifyListenersOnStop();
        this.isStop = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void logInfo(String log) {
        LOG.info(getName() + ": " + log);
    }

    public void logError(String log) {
        LOG.error(getName() + ": " + log);
    }


    public String getId() {
        return id;
    }

    public boolean isRunning() {
        return isStarted;
    }

    protected static String loadLocation() {
        printProperties();
        String loc = System.getProperty(PROP_LOCATION);
        if (loc == null) {
            loc = "LOCAL";
        }
        return loc;
    }

    protected static int loadInstanceId() {
        String instanceStr = System.getProperty(PROP_INSTANCE);
        int instanceID = 1;

        try {
            if (instanceStr != null) {
                instanceID = Integer.valueOf(instanceStr);
            }
        } catch (NumberFormatException e) {
            LOG.error("Failed to parse instance ID, using default instance 1");
            instanceID = 1;
        }

        return instanceID;
    }

    protected static void printProperties() {
        Properties properties = System.getProperties();
        LOG.info("##### System Properties: ");
        for (String name : properties.stringPropertyNames()) {
            LOG.info("name: '" + name + "' value: '" + properties.getProperty(name) + "'");
        }
        LOG.info("##### End System Properties");
    }

    protected static Properties loadProperties() {
        Properties props = new Properties();
        try {
            String propFileName = System.getProperty("propertyFile");
            if (propFileName == null) {
                propFileName = "szcore.properties";
            }

            InputStream inputStream = Server.class.getClassLoader().getResourceAsStream(propFileName);
            if (inputStream == null) {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            props.load(inputStream);

            return props;
        } catch (IOException e) {
            LOG.error("Failed to load Properties file: " + e);
        }
        return props;
    }

    protected String parsePropertyValue(String propName, String defaultValue) {
        return PropertyUtil.parsePropertyValue(propName, defaultValue, properties);
    }

}
