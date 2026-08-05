package player.listener;

import player.enums.GamePhase;
import player.repository.dao.GameState;
import player.service.ElectionService;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttListener {
    private static final String GAME_FLOW_TOPIC = "game/flow";
    private static final String GAME_MESSAGES_TOPIC = "game/messages";
    private static final String START_GAME_MESSAGE = "Start game";

    private final String clientId;
    private final GameState gameState;
    private final ElectionService electionService;

    public MqttListener(String brokerUri, int qos, GameState gameState, ElectionService electionService) throws MqttException {
        this.clientId = MqttClient.generateClientId();
        MqttClient mqttClient = new MqttClient(brokerUri, clientId);
        mqttClient.setCallback(createCallback());
        mqttClient.subscribe(GAME_FLOW_TOPIC);
        mqttClient.subscribe(GAME_MESSAGES_TOPIC);
        subscribe(mqttClient, qos);
        mqttClient.connect(createConnectionOptions());
        this.electionService = electionService;
        this.gameState = gameState;
    }

    private MqttConnectOptions createConnectionOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        return options;
    }

    private void subscribe(MqttClient client, int qos) throws MqttException {
        client.subscribe(GAME_FLOW_TOPIC, qos);
        client.subscribe(GAME_MESSAGES_TOPIC, qos);
    }

    private MqttCallback createCallback() {
        return new MqttCallback() {

            public void messageArrived(String topic, MqttMessage message) {
                String receivedMessage = new String(message.getPayload());
                System.out.println(receivedMessage);
                if (topic.equals(GAME_FLOW_TOPIC) && receivedMessage.equals(START_GAME_MESSAGE) && GamePhase.PLAY != gameState.getGamePhase()) {
                    //GameState.setGamePhase(GamePhase.ELECTION);
                    System.out.println("Game start");
                    //if(ConnectToOtherPlayersThread.getGameStart()){
                    System.out.println("I start the game");
                    electionService.startElection();
                    //}
                }
            }

            public void connectionLost(Throwable cause) {
                System.out.println(clientId + " Connectionlost! cause:" + cause.getMessage() + "-  Thread PID: " + Thread.currentThread().getId());
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
                //Not used here
            }

        };
    }


}
