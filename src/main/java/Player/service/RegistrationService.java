package Player.service;

import Player.Connections.MqttConnection;
import Player.client.SocketClient;
import Player.HRSimulation.HRCollectValues;
import Player.client.AdminServerClient;
import Player.enums.GamePhase;
import Player.repository.dao.Coordinates;
import Player.repository.dao.GameState;
import Player.repository.dao.OtherPlayer;
import Player.repository.dao.Player;
import Player.repository.dao.Self;
import Player.repository.OtherPlayerRepository;
import dtos.PlayerInfo;
import dtos.RegistrationResponse;
import proto.coordinates.CoordinatesOuterClass;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public final class RegistrationService {
    private final AdminServerClient adminServerClient;
    private SocketClient socketClient;
    private OtherPlayerRepository otherPlayerRepository;

    public RegistrationService(AdminServerClient adminServerClient, OtherPlayerRepository otherPlayerRepository) {
        this.adminServerClient = adminServerClient;
        this.otherPlayerRepository = otherPlayerRepository;
    }

    public void register(Integer playerId, Integer playerListeningPort) throws IOException {
        Player self = Self.createInstance(playerId, playerListeningPort);

        RegistrationResponse response = adminServerClient.register(self);
        Coordinates coordinates = new Coordinates(response.getCoordinateX(), response.getCoordinateY());
        self.setCoordinates(coordinates);

        registerWithOtherPlayers(self, response.getPlayerList());
        acceptNewRegistrations();

        MqttConnection.registerToMqtt();

        (new HRCollectValues()).start();
        GameState.setGamePhase(GamePhase.REGISTERED);
    }

    private void registerWithOtherPlayers(Player self, List<PlayerInfo> players) throws IOException {
        Objects.requireNonNull(players);

        CoordinatesOuterClass.Coordinates playerCoordinates = CoordinatesOuterClass.Coordinates.newBuilder()
                .setId(self.playerId())
                .setCoordX(self.getCoordinates().x())
                .setCoordY(self.getCoordinates().y())
                .setListeningPort(self.playerListeningPort())
                .build();
        socketClient = new SocketClient(self.playerListeningPort());

        for (PlayerInfo player : players) {
            OtherPlayer otherPlayer = socketClient.registerWithOtherPlayer(playerCoordinates, player);
            otherPlayerRepository.addPlayer(otherPlayer);
        }
    }

    private void acceptNewRegistrations() {
        (new RegistrationWithNewPlayerAcceptThread(socketClient, otherPlayerRepository)).start();
    }
}
