package com.xenaksys.szcore.web;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.PathResourceManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.xenaksys.szcore.Consts.INDEX_HTML;
import static io.undertow.Handlers.resource;

public class WebServer {

    private String staticDataPath;
    private int port;
    private int transferMinSize;

    public WebServer(String staticDataPath, int port, int transferMinSize) {
        this.staticDataPath = staticDataPath;
        this.port = port;
        this.transferMinSize = transferMinSize;
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
