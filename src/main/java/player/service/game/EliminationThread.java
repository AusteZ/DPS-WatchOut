package player.service.game;

import player.repository.OtherPlayerRepository;
import player.repository.dao.OtherPlayer;
import player.repository.dao.Player;
import player.utils.DistanceUtils;
import proto.messages.MessageOuterClass.Message;

import java.util.List;

public class EliminationThread extends Thread {
    private final Player localPlayer;
    private final OtherPlayerRepository otherPlayerRepository;
    private final MovementService movementService;

    public EliminationThread(Player localPlayer,
                             OtherPlayerRepository otherPlayerRepository,
                             MovementService movementService) {
        this.localPlayer = localPlayer;
        this.otherPlayerRepository = otherPlayerRepository;
        this.movementService = movementService;
    }

    public void run() {

        OtherPlayerRepository other = new OtherPlayerRepository();
        System.out.println("I am the seeker");

        while (true) {
            double minDistance = Double.MAX_VALUE;
            List<OtherPlayer> players = otherPlayerRepository.getPlayerListV2();
            OtherPlayer huntedPlayer = null;
            for (OtherPlayer player : players) {
                if(!player.player().isActive()){
                    continue;
                }

                double distance = DistanceUtils.calculateDistance(localPlayer.getCoordinates(), player.player().getCoordinates());
                if (minDistance > distance) {
                    minDistance = distance;
                    huntedPlayer = player;
                }
            }

            if (huntedPlayer == null) {
                break;
            }
            System.out.println("I am after " + huntedPlayer.player().playerId());

            movementService.moveToAnotherPlayer(huntedPlayer.player().getCoordinates());

            eliminate(huntedPlayer);
            huntedPlayer.player().setActive(false);
        }
        System.out.println("Game over (new players can join, but seeker will not look for them anymore).");
    }

    private void eliminate(OtherPlayer otherPlayer) {
        Message eliminated = Message.newBuilder()
                .setProtocol(Message.Protocol.ELIMINATED)
                .setId(localPlayer.playerId())
                .build();

        otherPlayer.writeThread().writeMessage(eliminated);
    }
}
