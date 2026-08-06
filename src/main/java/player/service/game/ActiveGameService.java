package player.service.game;

import player.enums.GamePhase;
import player.repository.GameState;
import player.repository.OtherPlayerRepository;
import player.repository.dao.Player;
import proto.messages.MessageOuterClass.Message;

public final class ActiveGameService {
    private final GameState gameState;

    private final MutualExclusionAlgorithmThread mutualExclusionAlgorithmThread;

    public ActiveGameService(GameState gameState,
                             Player localPlayer,
                             OtherPlayerRepository otherPlayerRepository,
                             MovementService movementService) {
        this.gameState = gameState;
        this.mutualExclusionAlgorithmThread = new MutualExclusionAlgorithmThread(gameState, localPlayer, otherPlayerRepository, movementService);
    }

    public void startActiveGame(){
        if(gameState.getGamePhase() == GamePhase.PLAY) {
            return;
        }

        gameState.setGamePhase(GamePhase.PLAY);
        mutualExclusionAlgorithmThread.start();
    }

    public boolean canBeEliminated(){
        return !mutualExclusionAlgorithmThread.hasPermissionForHomebase();
    }

    public void reduceLine(){
        mutualExclusionAlgorithmThread.decreaseCounter();
    }

    public void exclusionRespond(Message message){
        mutualExclusionAlgorithmThread.exclusionRespond(message);
    }
}
