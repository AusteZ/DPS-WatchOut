package player.service;

import player.enums.GamePhase;
import player.repository.OtherPlayerRepository;
import player.repository.dao.GameState;
import player.repository.dao.Player;
import player.service.threads.MutualExclusionAlgorithmThread;
import proto.messages.MessageOuterClass.Message;

public final class ActiveGameService {
    private final GameState gameState;
    private final Player localPlayer;
    private final OtherPlayerRepository otherPlayerRepository;
    private final MovementService movementService;

    private final MutualExclusionAlgorithmThread mutualExclusionAlgorithmThread;

    public ActiveGameService(GameState gameState,
                             Player localPlayer,
                             OtherPlayerRepository otherPlayerRepository,
                             MovementService movementService) {
        this.gameState = gameState;
        this.localPlayer = localPlayer;
        this.otherPlayerRepository = otherPlayerRepository;
        this.movementService = movementService;
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
