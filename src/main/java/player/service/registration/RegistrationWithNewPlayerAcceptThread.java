package player.service.registration;

import player.client.SocketClient;
import player.enums.GamePhase;
import player.repository.OtherPlayerRepository;
import player.repository.dao.GameState;
import player.repository.dao.OtherPlayer;
import player.service.election.ElectionAlgorithmThread;

public class RegistrationWithNewPlayerAcceptThread extends Thread {
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
        while(true){
            try{
                acceptRegistration();
            } catch(Exception e){
            }
        }
    }

    private void acceptRegistration() throws Exception{
        OtherPlayer otherPlayer = socketClient.acceptRegistrationWithOtherPlayers(null);
        otherPlayerRepository.addPlayer(otherPlayer);

        if (gameState.getGamePhase() == GamePhase.PLAY && gameState.getSeeker().seekerId() == ElectionAlgorithmThread.seekerId) {
            //ElectionAlgorithmThread.coordinatorRespond(other);
        }
    }
}
