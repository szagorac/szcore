package com.xenaksys.szcore.server.web;

import com.xenaksys.szcore.server.SzcoreServer;
import com.xenaksys.szcore.server.web.handler.ZsHttpHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.PathResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.xenaksys.szcore.Consts.INDEX_HTML;
import static io.undertow.Handlers.resource;

public class WebServer {
    static final Logger LOG = LoggerFactory.getLogger(WebServer.class);

    private final String staticDataPath;
    private final int port;
    private final int transferMinSize;
    private final SzcoreServer szcoreServer;

    public WebServer(String staticDataPath, int port, int transferMinSize, SzcoreServer szcoreServer) {
        this.staticDataPath = staticDataPath;
        this.port = port;
        this.transferMinSize = transferMinSize;
        this.szcoreServer = szcoreServer;
    }

    public void start(){
        initUndertow();
    }

    private void initUndertow() {
        HttpHandler staticDataHandler =  resource(new ClassPathResourceManager(WebServer.class.getClassLoader(), ""))
                .addWelcomeFiles(INDEX_HTML);
        if(staticDataPath != null) {
            Path path = Paths.get(staticDataPath);
            Path indexPath = Paths.get(path.toAbsolutePath().toString(), INDEX_HTML);

            if(Files.exists(path) && Files.exists(indexPath)) {
                staticDataHandler = resource(new PathResourceManager(path, transferMinSize))
                        .setWelcomeFiles(INDEX_HTML);
            }
        }

        Undertow server = Undertow.builder()
                .addHttpListener(port, "0.0.0.0")
                .setHandler(Handlers.path()
                        .addPrefixPath("/", staticDataHandler)
                        .addPrefixPath("/htp", new ZsHttpHandler(szcoreServer))
                ).build();
//
//        Undertow server = Undertow.builder()
//                .addHttpListener(port, "0.0.0.0")
//                .setHandler(Handlers.path()
//                        // REST API path
//                        .addPrefixPath("/api", Handlers.routing()
//                                .get("/customers", exchange -> {...})
//                                .delete("/customers/{customerId}", exchange -> {...})
//                                .setFallbackHandler(exchange -> {...}))
//
//                        // Redirect root path to /static to serve the index.html by default
//                        .addExactPath("/", Handlers.redirect("/static"))
//
//                        // Serve all static files from a folder
//                        .addPrefixPath("/static", new ResourceHandler(
//                                new PathResourceManager(Paths.get("/path/to/www/"), 100))
//                                .setWelcomeFiles("index.html"))
//
//                ).build();
//

        server.start();
    }

}
