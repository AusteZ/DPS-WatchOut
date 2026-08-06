package administration_server;

import administration_server.config.Configuration;
import administration_server.repository.MeasurementRepository;
import administration_server.userinterface.ConsoleUserInterface;
import administration_server.userinterface.UserInterface;
import org.glassfish.grizzly.http.server.HttpServer;

import java.io.IOException;

public final class AdministrationServer {

    public static void main(String[] ignoredArgs) throws IOException {
        Configuration config = new Configuration();
        MeasurementRepository measurementRepository = new MeasurementRepository();

        HttpServer httpServer = config.startServer(measurementRepository);

        UserInterface userInterface = ConsoleUserInterface.getInstance();
        userInterface.runInterface();
        httpServer.shutdown();
    }
}