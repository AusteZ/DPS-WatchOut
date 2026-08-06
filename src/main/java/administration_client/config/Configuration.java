package administration_client.config;

import administration_client.client.AdminServerClient;
import library.HttpClientWrapper;
import library.ResourceHandler;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.http.HttpClient;

public class Configuration {
    public final static String GAME_FLOW_TOPIC = "game/flow";
    public final static String MESSAGE_FLOW_TOPIC = "game/message/flow";

    private final ResourceHandler resourceHandler;

    public Configuration() {
        this.resourceHandler = new ResourceHandler("application");
    }

    public AdminServerClient getAdminServerClient() {
        String url = resourceHandler.getProperty("server.url");
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpClientWrapper httpClientWrapper = new HttpClientWrapper(httpClient);
        return new AdminServerClient(url, httpClientWrapper);
    }

    public MqttClient getMqttClient() throws MqttException {
        String broker = resourceHandler.getProperty("mqtt.broker");
        MqttClient mqttClient = new MqttClient(broker, MqttClient.generateClientId());
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setWill(GAME_FLOW_TOPIC, "".getBytes(), 1, true);
        mqttClient.connect(connOpts);
        return mqttClient;
    }
}
