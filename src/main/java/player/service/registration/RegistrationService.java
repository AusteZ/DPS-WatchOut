package player.service.registration;

import dtos.RegistrationResponse;
import player.client.AdminServerClient;
import player.client.SocketClient;
import player.enums.GamePhase;
import player.repository.GameState;
import player.repository.OtherPlayerRepository;
import player.repository.dao.Coordinates;
import player.repository.dao.OtherPlayer;
import player.repository.dao.Player;
import proto.coordinates.RegistrationRequestOuterClass.RegistrationRequest;

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
        Coordinates coordinates = new Coordinates(response.assignedCoordinates().x(), response.assignedCoordinates().y());
        localPlayer.setCoordinates(coordinates);

        registerWithOtherPlayers(localPlayer, response.players());
        acceptNewRegistrations();

        gameState.setGamePhase(GamePhase.REGISTERED);
    }

    private void registerWithOtherPlayers(Player self, List<dtos.PlayerInfo> players) throws IOException {
        Objects.requireNonNull(players);

        RegistrationRequest.Coordinates coordinates = RegistrationRequest.Coordinates.newBuilder()
                .setX(self.getCoordinates().x())
                .setY(self.getCoordinates().y())
                .build();

        RegistrationRequest registrationRequest = RegistrationRequest.newBuilder()
                .setId(self.playerId())
                .setCoordinates(coordinates)
                .setListeningPort(self.playerListeningPort())
                .build();

        for (dtos.PlayerInfo playerInfo : players) {
            OtherPlayer otherPlayer = socketClient.registerWithOtherPlayer(registrationRequest, playerInfo);
            otherPlayerRepository.addPlayer(otherPlayer);
        }
    }

    private void acceptNewRegistrations() {
        new RegistrationWithNewPlayerAcceptThread(gameState, socketClient, otherPlayerRepository).start();
    }
}
