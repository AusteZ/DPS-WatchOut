package administration_server;

import administration_server.helper.BinderHelper;
import administration_server.handler.ReceivingServerHandler;
import administration_server.userinterface.ConsoleUserInterface;
import administration_server.userinterface.UserInterface;
import library.ApplicationResourcesHandler;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;

public final class AdministrationServer {

    public static void main(String[] ignoredArgs) throws IOException {
        ReceivingServerHandler serverService = getServer();
        serverService.start();

        UserInterface userInterface = ConsoleUserInterface.getInstance();
        userInterface.runInterface(serverService);
    }

    private static ReceivingServerHandler getServer() {
        String host = ApplicationResourcesHandler.getProperty("server.host");
        int port = Integer.parseInt(ApplicationResourcesHandler.getProperty("server.port"));
        ResourceConfig config = BinderHelper.createConfig();
        return ReceivingServerHandler.getInstance(host, port, config);
    }
}