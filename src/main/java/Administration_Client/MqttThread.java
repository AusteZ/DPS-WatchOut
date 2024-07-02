package Administration_Client;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttThread extends Thread{
    private static MqttClient client;
    private String payload = null;
    private String topic = null;
    private static String broker = "tcp://localhost:1883";
    public MqttThread(String payload, String topic){
        this.payload = payload;
        this.topic = topic;
    }
    public static void setConnection(String broker){
        try {
            MqttThread.client = new MqttClient(broker, MqttClient.generateClientId());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            MqttThread.client.connect(connOpts);
        } catch(MqttException e){
            e.printStackTrace();
        }
    }
    public void run(){
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(2);
        System.out.println(" Publishing message: " + payload + " ...");
        try{client.publish(topic, message);}
        catch(MqttException e){}
        System.out.println(" Message published");
    }
}
