package administration_client.service;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

class MqttThread extends Thread {
    private final MqttClient mqttClient;
    private final String payload;
    private final String topic;

    public MqttThread(MqttClient mqttClient, String topic, String payload) {
        this.mqttClient = mqttClient;
        this.topic = topic;
        this.payload = payload;
    }

    public void run() {
        MqttMessage message = new MqttMessage(payload.getBytes());
        //quality of service: at least once
        message.setQos(2);
        message.setRetained(topic.contains("flow"));
        System.out.println(" Publishing message: " + payload + " ...");
        try {
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
        }
        System.out.println(" Message published");
    }
}
