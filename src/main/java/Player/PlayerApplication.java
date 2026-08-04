package Player;

import Player.client.AdminServerClient;
import Player.service.RegistrationService;
import Player.userinterface.CliController;
import library.ApplicationResourcesHandler;

import java.net.http.HttpClient;

public class PlayerApplication {
    private static int id;
    private static int listeningPort;
    private static int coordX;
    private static int coordY;
    public static boolean active = true;
    public static int gamePhase = -1;

    public static void main(String[] ignoredArgs) throws Exception {
        AdminServerClient adminServerClient = createAdminServerClient();
        RegistrationService registrationService = new RegistrationService(adminServerClient);
        CliController cliController = new CliController(registrationService);
        cliController.run();
    }

    private static AdminServerClient createAdminServerClient() {
        String url = ApplicationResourcesHandler.getProperty("server.url");
        HttpClient httpClient = HttpClient.newBuilder().build();
        return new AdminServerClient(url, httpClient);
    }

    public static int getId() {
        return id;
    }

    public static int getCoordX() {
        return coordX;
    }

    public static int getCoordY() {
        return coordY;
    }

    public static void setCoordXAndCoordY(int coordX, int coordY) {
        PlayerApplication.coordX = coordX;
        PlayerApplication.coordY = coordY;
    }

    public static int getListeningPort() {
        return listeningPort;
    }
}
