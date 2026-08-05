package Player.Gameplay;

import Player.PlayerApplication;
import Player.repository.OtherPlayerRepository;
import Player.service.threads.ElectionAlgorithmThread;
import proto.messages.MessageOuterClass.Message;

public class EliminationThread extends Thread {

    public void run() {

        double minDistance = Double.MAX_VALUE;
        OtherPlayerRepository other = new OtherPlayerRepository();
        int playerCount = 0;
        System.out.println("I am the seeker");

        while (true) {
            minDistance = Double.MAX_VALUE;
            playerCount = 0;
            for (OtherPlayerRepository otherPlayerRepository : OtherPlayerRepository.getPlayerList()) {
                if (otherPlayerRepository.active && otherPlayerRepository.id != other.id) {
                    double distance = PlayerMove.distanceToAnotherPlayer(otherPlayerRepository.coordX, otherPlayerRepository.coordY);
                    if (minDistance > distance) {
                        minDistance = distance;
                        other = otherPlayerRepository;
                    }
                    playerCount++;
                }

            }
            if (playerCount == 0) {
                break;
            }
            System.out.println("I am after " + other.id);
            other.active = false;
            PlayerMove.moveToAnotherPlayer(other.coordX, other.coordY, minDistance);
            eliminate(other);
        }
        System.out.println("Game over (new players can join, but seeker will not look for them anymore).");
    }

    public static void wasEliminated() {
        Message eliminated = Message.newBuilder()
                .setProtocol(Message.Protocol.ELIMINATED)
                .setId(PlayerApplication.getId())
                .build();
        for (OtherPlayerRepository otherPlayerRepository : OtherPlayerRepository.getPlayerList()) {
            otherPlayerRepository.writeThread.writeMessage(eliminated);
        }
    }

    public static void cannotBeEliminated() {
        Message no = Message.newBuilder()
                .setProtocol(Message.Protocol.NO)
                .setId(PlayerApplication.getId())
                .build();
        OtherPlayerRepository otherPlayerRepository = OtherPlayerRepository.getPlayerList().stream().filter(other -> other.id == ElectionAlgorithmThread.seekerId).findFirst().get();
        otherPlayerRepository.writeThread.writeMessage(no);
    }

    public static void eliminate(OtherPlayerRepository otherPlayerRepository) {
        Message eliminated = Message.newBuilder()
                .setProtocol(Message.Protocol.ELIMINATED)
                .setId(PlayerApplication.getId())
                .build();
        otherPlayerRepository.writeThread.writeMessage(eliminated);

    }
}
