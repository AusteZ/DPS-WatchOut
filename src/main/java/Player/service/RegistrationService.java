package Player.service;

import Player.Connections.ConnectToOtherPlayersThread;
import Player.Connections.MqttConnection;
import Player.HRSimulation.HRCollectValues;
import Player.client.AdminServerClient;
import Player.dao.Self;
import dtos.PlayerInfo;
import dtos.RegistrationResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public final class RegistrationService {
    private final AdminServerClient adminServerClient;

    public RegistrationService(AdminServerClient adminServerClient) {
        this.adminServerClient = adminServerClient;
    }

    public void register(Integer playerId, Integer playerListeningPort) {
        Self self = Self.createInstance(playerId, playerListeningPort);

        RegistrationResponse response = adminServerClient.register(self);
        self.setCoordinates(response.getCoordinateX(), response.getCoordinateY());

        registerWithOtherPlayers(self, response.getPlayerList());

        MqttConnection.registerToMqtt();

        (new HRCollectValues()).start();
    }

    private void registerWithOtherPlayers(Self self, List<PlayerInfo> players) {
        Objects.requireNonNull(players);
        (new ConnectToOtherPlayersThread(players, self.playerListeningPort())).start();
    }
}
