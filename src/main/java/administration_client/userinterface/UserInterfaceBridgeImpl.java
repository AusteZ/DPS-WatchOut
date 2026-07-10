package administration_client.userinterface;

import administration_client.service.MqttService;
import administration_client.client.AdminClient;
import dtos.PlayerInfo;

import java.util.List;

public class UserInterfaceBridgeImpl implements UserInterfaceBridge {
    private final AdminClient adminClient;
    private final MqttService mqttService;

    public UserInterfaceBridgeImpl(AdminClient adminClient, MqttService mqttService) {
        this.adminClient = adminClient;
        this.mqttService = mqttService;
    }

    public List<PlayerInfo> getAllPlayers() {
        return adminClient.getAllPlayers();
    }

    public double getLatestMeasurementAverage(int playerId, int count) {
        return adminClient.getLatestMeasurementAverage(playerId, count);
    }

    public double getMeasurementAverageBetweenTimestamps(Long startTimestamp, Long endTimestamp) {
        return adminClient.getMeasurementAverageBetweenTimestamps(startTimestamp, endTimestamp);
    }

    public void startGame() {
        mqttService.startGame();
    }

    public void sendMessageToPlayers(String message) {
        mqttService.messagePlayers(message);
    }
}
