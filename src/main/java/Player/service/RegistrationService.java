package Player.service;

import Player.HRSimulation.HRCollectValues;
import Player.client.AdminServerClient;
import Player.client.SocketClient;
import Player.enums.GamePhase;
import Player.repository.OtherPlayerRepository;
import Player.repository.dao.Coordinates;
import Player.repository.dao.GameState;
import Player.repository.dao.OtherPlayer;
import Player.repository.dao.Player;
import Player.service.threads.RegistrationWithNewPlayerAcceptThread;
import dtos.RegistrationResponse;
import proto.coordinates.CoordinatesOuterClass;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public final class RegistrationService {
    private final GameState gameState;
    private final Player localPlayer;
    private final AdminServerClient adminServerClient;
    private final SocketClient socketClient;
    private final OtherPlayerRepository otherPlayerRepository;

    public RegistrationService(GameState gameState, Player localPlayer, AdminServerClient adminServerClient, OtherPlayerRepository otherPlayerRepository, SocketClient socketClient) {
        this.gameState = gameState;
        this.localPlayer = localPlayer;
        this.adminServerClient = adminServerClient;
        this.otherPlayerRepository = otherPlayerRepository;
        this.socketClient = socketClient;
    }

    public void register() throws IOException {
        RegistrationResponse response = adminServerClient.register(localPlayer);
        Coordinates coordinates = new Coordinates(response.getCoordinateX(), response.getCoordinateY());
        localPlayer.setCoordinates(coordinates);

        registerWithOtherPlayers(localPlayer, response.getPlayerList());
        acceptNewRegistrations();

        (new HRCollectValues()).start();
        gameState.setGamePhase(GamePhase.REGISTERED);
    }

    private void registerWithOtherPlayers(Player self, List<dtos.PlayerInfo> players) throws IOException {
        Objects.requireNonNull(players);

        CoordinatesOuterClass.Coordinates playerCoordinates = CoordinatesOuterClass.Coordinates.newBuilder()
                .setId(self.playerId())
                .setCoordX(self.getCoordinates().x())
                .setCoordY(self.getCoordinates().y())
                .setListeningPort(self.playerListeningPort())
                .build();

        for (dtos.PlayerInfo playerInfo : players) {
            OtherPlayer otherPlayer = socketClient.registerWithOtherPlayer(playerCoordinates, playerInfo);
            otherPlayerRepository.addPlayer(otherPlayer);
        }
    }

    private void acceptNewRegistrations() {
        new RegistrationWithNewPlayerAcceptThread(gameState, socketClient, otherPlayerRepository).start();
    }
}
