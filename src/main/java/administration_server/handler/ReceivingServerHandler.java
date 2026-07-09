package administration_server.handler;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ReceivingServerHandler implements Closeable {
    private static final Logger LOGGER = Logger.getLogger(ReceivingServerHandler.class.getName());
    private final HttpServer server;

    private ReceivingServerHandler(String host, int port, ResourceConfig config) {
        String baseUrl = host + ":" + port;
        URI uri = URI.create(baseUrl);

        this.server = GrizzlyHttpServerFactory.createHttpServer(uri, config);
    }

    public static ReceivingServerHandler getInstance(String host, int port, ResourceConfig config) {
        return new ReceivingServerHandler(host, port, config);
    }

    public void start() throws IOException {
        if (!server.isStarted()) {
            server.start();
            LOGGER.log(Level.INFO, "Server running.");
        }
    }

    public void close() {
        server.shutdown();
        LOGGER.log(Level.INFO, "Server shutdown.");
    }
}
