package Player.service.threads;

import Player.Gameplay.EliminationThread;
import Player.enums.GamePhase;
import Player.repository.OtherPlayerRepository;
import Player.repository.dao.Coordinates;
import Player.repository.dao.GameState;
import Player.repository.dao.OtherPlayer;
import Player.repository.dao.Player;
import Player.utils.DistanceUtils;
import proto.messages.MessageOuterClass;
import proto.messages.MessageOuterClass.Message;

import java.util.List;

public class ElectionAlgorithmThread extends Thread {
    public static int seekerId;
    private static final Object lock = new Object();

    private static ElectionAlgorithmThread electionAlgorithmThread;

    private final GameState gameState;
    private final Player localPlayer;
    private final OtherPlayerRepository otherPlayerRepository;

    public ElectionAlgorithmThread(GameState gameState, Player localPlayer, OtherPlayerRepository otherPlayerRepository) {
        this.gameState = gameState;
        this.localPlayer = localPlayer;
        this.otherPlayerRepository = otherPlayerRepository;
    }

    public static ElectionAlgorithmThread getElectionThread() {
        if (electionAlgorithmThread == null || !electionAlgorithmThread.isAlive()) {
            throw new RuntimeException("Election thread has not been started");
        }

        return electionAlgorithmThread;
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
                if (!electionAlgorithmThread.isInterrupted()) {
                    gameState.setGamePhase(GamePhase.PLAY);
                    new EliminationThread().start();
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
