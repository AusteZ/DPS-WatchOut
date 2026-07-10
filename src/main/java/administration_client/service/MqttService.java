package administration_client.service;

import org.eclipse.paho.client.mqttv3.MqttClient;

public class MqttService {
    private final String GAME_FLOW_TOPIC = "game/flow";
    private final String MESSAGE_FLOW_TOPIC = "game/message/flow";

    private final MqttClient mqttClient;

    public MqttService(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public void startGame(){
        publishMessage(GAME_FLOW_TOPIC, "Start game");
    }

    public void messagePlayers(String message){
        publishMessage(MESSAGE_FLOW_TOPIC, message);
    }

    private void publishMessage(String topic, String message){
        MqttThread mqttThread = new MqttThread(mqttClient, topic, message);
        mqttThread.start();
    }
}
