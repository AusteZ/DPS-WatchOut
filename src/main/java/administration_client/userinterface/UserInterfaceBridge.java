package administration_client.userinterface;

import dtos.PlayerInfo;

import java.util.List;

public interface UserInterfaceBridge {
    List<PlayerInfo> getAllPlayers();

    void startGame();

    void sendMessageToPlayers(String message);

    double getMeasurementAverageBetweenTimestamps(Long startTimestamp, Long endTimestamp);

    double getLatestMeasurementAverage(int playerId, int count);

}
