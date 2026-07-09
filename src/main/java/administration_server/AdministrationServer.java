package administration_server;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class AdministrationServer {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        URI baseUri = URI.create("http://" + HOST + ":" + PORT + "/");

        ResourceConfig config = new ResourceConfig()
                .packages("administration_server.Services");

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);

        System.out.println("Server running at " + baseUri);
        System.out.println("Press Enter to stop...");
        System.in.read();

        server.shutdownNow();
    }
}