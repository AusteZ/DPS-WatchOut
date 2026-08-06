package administration_client.userinterface;

import administration_client.client.AdminServerClient;
import administration_client.service.MqttService;
import dtos.PlayerInfo;

import java.util.List;

public class UserInterfaceBridgeImpl implements UserInterfaceBridge {
    private final AdminServerClient adminServerClient;
    private final MqttService mqttService;

    public UserInterfaceBridgeImpl(AdminServerClient adminServerClient, MqttService mqttService) {
        this.adminServerClient = adminServerClient;
        this.mqttService = mqttService;
    }

    public List<PlayerInfo> getAllPlayers() {
        return adminServerClient.getAllPlayers();
    }

    public double getLatestMeasurementAverage(int playerId, int count) {
        return adminServerClient.getLatestMeasurementAverage(playerId, count);
    }

    public double getMeasurementAverageBetweenTimestamps(Long startTimestamp, Long endTimestamp) {
        return adminServerClient.getMeasurementAverageBetweenTimestamps(startTimestamp, endTimestamp);
    }

    public void startGame() {
        mqttService.startGame();
    }

    public void sendMessageToPlayers(String message) {
        mqttService.messagePlayers(message);
    }
}
