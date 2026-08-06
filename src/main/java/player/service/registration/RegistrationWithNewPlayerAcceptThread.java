package player.service.registration;

import player.client.SocketClient;
import player.repository.GameState;
import player.repository.OtherPlayerRepository;
import player.repository.dao.OtherPlayer;

final class RegistrationWithNewPlayerAcceptThread extends Thread {
    private final GameState gameState;
    private final SocketClient socketClient;
    private final OtherPlayerRepository otherPlayerRepository;

    public RegistrationWithNewPlayerAcceptThread(GameState gameState, SocketClient socketClient,
                                                 OtherPlayerRepository otherPlayerRepository) {
        this.gameState = gameState;
        this.socketClient = socketClient;
        this.otherPlayerRepository = otherPlayerRepository;
    }

    @Override
    public void run() {
        while (true) {
            try {
                acceptRegistration();
            } catch (Exception e) {
            }
        }
    }

    private void acceptRegistration() throws Exception {
        //TODO: send out own request
        OtherPlayer otherPlayer = socketClient.acceptRegistrationWithOtherPlayers(null);
        otherPlayerRepository.addPlayer(otherPlayer);

        //TODO: fix
        //if (gameState.getGamePhase() == GamePhase.PLAY && gameState.getSeeker().seekerId() == .seekerId) {
        //ElectionAlgorithmThread.coordinatorRespond(other);
        //}
    }
}
