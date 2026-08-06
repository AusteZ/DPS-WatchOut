package administration_client;

import administration_client.client.AdminServerClient;
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

public class AdministrationClientApplication {

    public static void main(String[] ignoredArgs) throws MqttException {
        AdminServerClient adminServerClient = getAdminClient();
        MqttService mqttService = getMqttService();

        UserInterfaceBridge userInterfaceBridge = new UserInterfaceBridgeImpl(adminServerClient, mqttService);
        UserInterface userInterface = ConsoleUserInterface.getInstance(userInterfaceBridge);

        userInterface.runInterface();
    }

    private static AdminServerClient getAdminClient() {
        String url = ApplicationResourcesHandler.getProperty("server.url");
        HttpClient httpClient = HttpClient.newBuilder().build();
        return new AdminServerClient(url, httpClient);
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
