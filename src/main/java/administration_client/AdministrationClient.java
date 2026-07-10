package administration_client;

import administration_client.client.AdminClient;
import administration_client.service.MqttService;
import administration_client.userinterface.ConsoleUserInterface;
import administration_client.userinterface.UserInterface;
import administration_client.userinterface.UserInterfaceBridge;
import administration_client.userinterface.UserInterfaceBridgeImpl;
import library.ApplicationResourcesHandler;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.http.HttpClient;

public class AdministrationClient {

    public static void main(String[] ignoredArgs) throws MqttException {
        AdminClient adminClient = getAdminClient();
        MqttService mqttService = getMqttService();

        UserInterfaceBridge userInterfaceBridge = new UserInterfaceBridgeImpl(adminClient, mqttService);
        UserInterface userInterface = ConsoleUserInterface.getInstance(userInterfaceBridge);

        userInterface.runInterface();
    }

    private static AdminClient getAdminClient() {
        String url = ApplicationResourcesHandler.getProperty("server.url");
        HttpClient httpClient = HttpClient.newBuilder().build();
        return new AdminClient(url, httpClient);
    }

    private static MqttService getMqttService() throws MqttException {
        MqttClient mqttClient = getMqttClient();
        return new MqttService(mqttClient);
    }

    private static MqttClient getMqttClient() throws MqttException {
        String broker = ApplicationResourcesHandler.getProperty("mqtt.broker");
        MqttClient mqttClient = new MqttClient(broker, MqttClient.generateClientId());
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setWill("game/flow", "".getBytes(), 1, true);
        mqttClient.connect(connOpts);
        return mqttClient;
    }

}
