package player.service;

import player.repository.OtherPlayerRepository;
import player.repository.dao.GameState;
import player.repository.dao.OtherPlayer;
import player.repository.dao.Player;
import player.service.threads.EliminationThread;
import proto.messages.MessageOuterClass;

import java.util.List;

public final class EliminationService {
    private final GameState gameState;
    private final Player localPlayer;
    private final OtherPlayerRepository otherPlayerRepository;
    private final EliminationThread eliminationThread;

    public EliminationService(GameState gameState,
                              Player localPlayer,
                              OtherPlayerRepository otherPlayerRepository,
                              MovementService movementService) {
        this.gameState = gameState;
        this.localPlayer = localPlayer;
        this.otherPlayerRepository = otherPlayerRepository;
        this.eliminationThread = new EliminationThread(localPlayer, otherPlayerRepository, movementService);
    }

    public void startSeeking(){
        eliminationThread.start();
    }

    public void wasEliminated() {
        MessageOuterClass.Message eliminated = MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.ELIMINATED)
                .setId(localPlayer.playerId())
                .build();
        localPlayer.setActive(false);
        List<OtherPlayer> players = otherPlayerRepository.getPlayerListV2();
        for (OtherPlayer player : players) {
            player.writeThread().writeMessage(eliminated);
        }
    }

    public void cannotBeEliminated() {
        MessageOuterClass.Message no = MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.NO)
                .setId(localPlayer.playerId())
                .build();
        OtherPlayer otherPlayer = otherPlayerRepository.getPlayerById(gameState.getSeeker().seekerId());
        otherPlayer.writeThread().writeMessage(no);
    }
}
