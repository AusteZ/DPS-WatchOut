package Player;

import Player.client.AdminServerClient;
import Player.listener.MqttListener;
import Player.repository.OtherPlayerRepository;
import Player.service.RegistrationService;
import Player.userinterface.CliController;
import administration_client.service.MqttService;
import library.ApplicationResourcesHandler;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

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
        OtherPlayerRepository otherPlayerRepository = new OtherPlayerRepository();
        RegistrationService registrationService = new RegistrationService(adminServerClient, otherPlayerRepository);
        startMqttListener();
        CliController cliController = new CliController(registrationService);
        cliController.run();
    }

    private static AdminServerClient createAdminServerClient() {
        String url = ApplicationResourcesHandler.getProperty("server.url");
        HttpClient httpClient = HttpClient.newBuilder().build();
        return new AdminServerClient(url, httpClient);
    }

    private static void startMqttListener() throws MqttException {
        String broker = ApplicationResourcesHandler.getProperty("mqtt.broker");
        int qos = 2;
        new MqttListener(broker, qos);
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
