package administration_server.config;

import administration_server.repository.MeasurementRepository;
import administration_server.repository.PlayerRepository;
import administration_server.service.MeasurementService;
import administration_server.service.PlayerService;
import library.ResourceHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import java.io.IOException;
import java.net.URI;

public class Configuration {
    private final ResourceHandler resourceHandler;

    public Configuration() {
        resourceHandler = new ResourceHandler("application");
    }

    public HttpServer startServer(MeasurementRepository measurementRepository, PlayerRepository playerRepository) throws IOException {
        String baseUrl = resourceHandler.getProperty("server.url");
        URI uri = URI.create(baseUrl);
        ResourceConfig config = createConfig(measurementRepository, playerRepository);
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, config);
        server.start();
        return server;
    }

    private ResourceConfig createConfig(MeasurementRepository measurementRepository, PlayerRepository playerRepository) {
        return new ResourceConfig()
                .packages("administration_server.service")
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(new MeasurementService(measurementRepository)).to(MeasurementService.class);
                        bind(MeasurementService.class).to(MeasurementService.class);

                        bind(new PlayerService(playerRepository)).to(PlayerService.class);
                        bind(PlayerService.class).to(PlayerService.class);
                    }
                })
                .property(ServerProperties.WADL_FEATURE_DISABLE, true);
    }
}
