package administration_server;

import administration_server.config.Configuration;
import administration_server.repository.MeasurementRepository;
import administration_server.repository.PlayerRepository;
import administration_server.userinterface.CLI;
import org.glassfish.grizzly.http.server.HttpServer;

import java.io.IOException;

public final class AdministrationServerApplication {

    public static void main(String[] ignoredArgs) throws IOException {
        Configuration config = new Configuration();
        MeasurementRepository measurementRepository = new MeasurementRepository();
        PlayerRepository playerRepository = new PlayerRepository();

        HttpServer httpServer = config.httpServer(measurementRepository, playerRepository);

        CLI userInterface = new CLI();
        userInterface.exit();
        httpServer.shutdown();
    }
}