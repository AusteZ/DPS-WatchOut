package Player.service;

import Player.DistributedAlgorithms.ElectionAlgorithmThread;
import Player.client.SocketClient;
import Player.repository.dao.GameState;
import Player.repository.dao.OtherPlayer;
import Player.enums.GamePhase;
import Player.repository.OtherPlayerRepository;

public class RegistrationWithNewPlayerAcceptThread extends Thread {
    private final SocketClient socketClient;
    private final OtherPlayerRepository otherPlayerRepository;

    public RegistrationWithNewPlayerAcceptThread(SocketClient socketClient,
                                                 OtherPlayerRepository otherPlayerRepository) {
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

        if (GameState.getGamePhase() == GamePhase.PLAY && GameState.getSeekerId() == ElectionAlgorithmThread.seekerId) {
            //ElectionAlgorithmThread.coordinatorRespond(other);
        }
    }
}
