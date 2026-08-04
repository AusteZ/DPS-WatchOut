package Player.Connections;

import Player.DistributedAlgorithms.ElectionAlgorithmThread;
import Player.PlayerApplication;
import org.eclipse.paho.client.mqttv3.*;

public class MqttConnection {
    public static void registerToMqtt(){
        MqttClient mqqtClient;
        String broker = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        int qos = 2;

        try {
            mqqtClient = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            //Connect the client
            mqqtClient.connect(connOpts);
            System.out.println("Mqtt connected");

            //Callback
            mqqtClient.setCallback(new MqttCallback() {

                public void messageArrived(String topic, MqttMessage message) {
                    String receivedMessage = new String(message.getPayload());
                    System.out.println(receivedMessage);
                    if(topic.equals("game/flow") && receivedMessage.equals("Start game") && PlayerApplication.gamePhase != 1) {
                        PlayerApplication.gamePhase = 0;
                        System.out.println("Game start");
                        if(ConnectToOtherPlayersThread.getGameStart()){
                            System.out.println("I start the game");
                            ElectionAlgorithmThread.initializeThread();
                        }
                    }
                }

                public void connectionLost(Throwable cause) {
                    System.out.println(clientId + " Connectionlost! cause:" + cause.getMessage()+ "-  Thread PID: " + Thread.currentThread().getId());
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    //Not used here
                }

            });
            mqqtClient.subscribe("game/flow",qos);
            mqqtClient.subscribe("game/messages", qos);



        } catch (MqttException me ) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }
}
