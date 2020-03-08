package com.xenaksys.szcore.server.web;

import com.google.gson.Gson;
import com.xenaksys.szcore.server.SzcoreServer;
import com.xenaksys.szcore.server.web.handler.ZsHttpHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.sse.ServerSentEventConnection;
import io.undertow.server.handlers.sse.ServerSentEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.xenaksys.szcore.Consts.INDEX_HTML;
import static io.undertow.Handlers.resource;
import static io.undertow.Handlers.serverSentEvents;

public class WebServer {
    static final Logger LOG = LoggerFactory.getLogger(WebServer.class);

    private static final Gson GSON = new Gson();

    private final String staticDataPath;
    private final int port;
    private final int transferMinSize;
    private final SzcoreServer szcoreServer;

    private volatile boolean isRunning = false;
    private Undertow undertow = null;
    private ServerSentEventHandler sseHandler = null;

    public WebServer(String staticDataPath, int port, int transferMinSize, SzcoreServer szcoreServer) {
        this.staticDataPath = staticDataPath;
        this.port = port;
        this.transferMinSize = transferMinSize;
        this.szcoreServer = szcoreServer;
    }

    public boolean isRunning(){
        return isRunning;
    }

    public void start(){
        LOG.info("Starting Web Server ...");
        if(undertow == null) {
            initUndertow();
        }

        if(isRunning) {
            return;
        }

        undertow.start();
        isRunning = true;
        LOG.info("Web Server is Running");
    }

    public void stop(){
        LOG.info("Stopping Web Server ...");
        if(isRunning && undertow != null) {
            undertow.stop();
            isRunning = false;
            LOG.info("Web Server is Stopped");
        }
    }

    private void initUndertow() {
        HttpHandler staticDataHandler =  resource(new ClassPathResourceManager(WebServer.class.getClassLoader(), ""))
                .addWelcomeFiles(INDEX_HTML);
        sseHandler = serverSentEvents();

        if(staticDataPath != null) {
            Path path = Paths.get(staticDataPath);
            Path indexPath = Paths.get(path.toAbsolutePath().toString(), INDEX_HTML);

            if(Files.exists(path) && Files.exists(indexPath)) {
                staticDataHandler = resource(new PathResourceManager(path, transferMinSize))
                        .setWelcomeFiles(INDEX_HTML);
            }
        }

        undertow = Undertow.builder()
                .addHttpListener(port, "0.0.0.0")
                .setHandler(Handlers.path()
                        .addPrefixPath("/", staticDataHandler)
                        .addPrefixPath("/sse", sseHandler)
                        .addPrefixPath("/htp", new ZsHttpHandler(szcoreServer))
                ).build();

    }

    public void pushToAll(String data) {
        if(sseHandler == null || data == null) {
            return;
        }

        for(ServerSentEventConnection sseConnection : sseHandler.getConnections()) {
            sseConnection.send(data);
        }
    }
}