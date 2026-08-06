package player.service.election;

import player.enums.GamePhase;
import player.repository.GameState;
import player.repository.OtherPlayerRepository;
import player.repository.dao.Coordinates;
import player.repository.dao.OtherPlayer;
import player.repository.dao.Player;
import player.service.game.EliminationService;
import player.utils.DistanceUtils;
import proto.messages.MessageOuterClass;
import proto.messages.MessageOuterClass.Message;

import java.util.List;

final class ElectionAlgorithmThread extends Thread {
    private static final Object lock = new Object();

    private final GameState gameState;
    private final Player localPlayer;
    private final OtherPlayerRepository otherPlayerRepository;
    private final EliminationService eliminationService;

    public ElectionAlgorithmThread(GameState gameState,
                                   Player localPlayer,
                                   OtherPlayerRepository otherPlayerRepository,
                                   EliminationService eliminationService) {
        this.gameState = gameState;
        this.localPlayer = localPlayer;
        this.otherPlayerRepository = otherPlayerRepository;
        this.eliminationService = eliminationService;
    }

    @Override
    public void run() {
        int count = 0;
        Coordinates coords = localPlayer.getCoordinates();
        double playerDistance = DistanceUtils.calculateDistanceToHomeBase(coords);
        List<OtherPlayer> players = otherPlayerRepository.getPlayerListV2();

        MessageOuterClass.Message election = MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.ELECTION)
                .setId(localPlayer.playerId())
                .setDistance(playerDistance)
                .build();

        for (OtherPlayer otherPlayer : players) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }

            Player otherPlayerInfo = otherPlayer.player();
            Coordinates otherPlayerCoordinates = otherPlayerInfo.getCoordinates();

            double otherDistance = DistanceUtils.calculateDistanceToHomeBase(otherPlayerCoordinates);

            if (otherDistance > playerDistance || (otherDistance == playerDistance && otherPlayerInfo.playerId() > localPlayer.playerId())) {
                otherPlayer.writeThread().writeMessage(election);
                count++;
                System.out.println("Election to " + otherPlayerInfo.playerId());
            } else {
                otherPlayerInfo.setActive(true);
            }
        }

        System.out.println("Distance: " + playerDistance + " and id: " + localPlayer.playerId());

        if (count > 0) {
            return;
        }

        long seekerCreationTimestamp = System.currentTimeMillis();
        gameState.setSeeker(localPlayer.playerId(), System.currentTimeMillis());
        Message seekerMessage = createSeekerMessage(localPlayer.playerId(), seekerCreationTimestamp);

        for (OtherPlayer other : players) {
            other.writeThread().writeMessage(seekerMessage);
        }

        synchronized (lock) {
            try {
                lock.wait(5000);
                if (!Thread.currentThread().isInterrupted()) {
                    gameState.setGamePhase(GamePhase.PLAY);
                    eliminationService.startSeeking();
                    System.out.println("SEEKER");
                }
            } catch (InterruptedException e) {
            }
        }
    }

    private Message createSeekerMessage(int seekerId, long seekerCreationTimestamp) {
        return Message.newBuilder()
                .setProtocol(Message.Protocol.SEEKER)
                .setId(seekerId)
                .setTimestamp(seekerCreationTimestamp)
                .build();
    }
}
