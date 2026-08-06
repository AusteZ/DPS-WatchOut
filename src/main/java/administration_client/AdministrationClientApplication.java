package administration_client;

import administration_client.client.AdminServerClient;
import administration_client.config.Configuration;
import administration_client.service.MqttService;
import administration_client.userinterface.ConsoleUserInterface;
import administration_client.userinterface.UserInterface;
import administration_client.userinterface.UserInterfaceBridge;
import administration_client.userinterface.UserInterfaceBridgeImpl;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class AdministrationClientApplication {

    public static void main(String[] ignoredArgs) throws MqttException {
        Configuration config = new Configuration();

        AdminServerClient adminServerClient = config.getAdminServerClient();
        MqttClient mqttClient = config.getMqttClient();
        MqttService mqttService = new MqttService(mqttClient);

        UserInterfaceBridge userInterfaceBridge = new UserInterfaceBridgeImpl(adminServerClient, mqttService);
        UserInterface userInterface = ConsoleUserInterface.getInstance(userInterfaceBridge);

        userInterface.runInterface();
    }
}
