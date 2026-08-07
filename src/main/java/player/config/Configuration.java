package player.config;

import library.HttpClientWrapper;
import library.ResourceHandler;
import org.eclipse.paho.client.mqttv3.MqttException;
import player.client.AdminServerClient;
import player.listener.MqttListener;
import player.repository.GameState;
import player.service.election.ElectionService;

import java.net.http.HttpClient;

public class Configuration {
    private final ResourceHandler resourceHandler;

    public Configuration() {
        this.resourceHandler = new ResourceHandler("application");
    }

    public AdminServerClient createAdminServerClient() {
        String url = resourceHandler.getProperty("server.url");
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpClientWrapper httpClientWrapper = new HttpClientWrapper(httpClient);
        return new AdminServerClient(url, httpClientWrapper);
    }

    public void startMqttListener(GameState gameState, ElectionService electionService) throws MqttException {
        String broker = resourceHandler.getProperty("mqtt.broker");
        int qos = 2;
        new MqttListener(broker, qos, gameState, electionService);
    }
}
