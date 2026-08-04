package Player.Gameplay;

import Player.DistributedAlgorithms.ElectionAlgorithmThread;
import Player.PlayerApplication;
import proto.messages.MessageOuterClass.Message;

public class EliminationThread extends Thread {

    public void run(){
        
        double minDistance = Double.MAX_VALUE;
        OtherPlayer other = new OtherPlayer();
        int playerCount = 0;
        System.out.println("I am the seeker");
        
        while(true) {
            minDistance = Double.MAX_VALUE;
            playerCount = 0;
            for(OtherPlayer otherPlayer : OtherPlayer.getPlayerList()) {
                if(otherPlayer.active && otherPlayer.id != other.id) {
                    double distance = PlayerMove.distanceToAnotherPlayer(otherPlayer.coordX, otherPlayer.coordY);
                    if (minDistance > distance) {
                        minDistance = distance;
                        other = otherPlayer;
                    }
                    playerCount++;
                }
                
            }
            if(playerCount == 0) {
                break;
            }
            System.out.println("I am after " + other.id);
            other.active = false;
            PlayerMove.moveToAnotherPlayer(other.coordX, other.coordY, minDistance);
            eliminate(other);
        }
        System.out.println("Game over (new players can join, but seeker will not look for them anymore).");
    }
    public static void wasEliminated(){
        Message eliminated = Message.newBuilder()
                .setProtocol(Message.Protocol.ELIMINATED)
                .setId(PlayerApplication.getId())
                .build();
        for(OtherPlayer otherPlayer : OtherPlayer.getPlayerList()) {
            otherPlayer.writeThread.writeMessage(eliminated);
        }
    }
    public static void cannotBeEliminated(){
        Message no = Message.newBuilder()
                .setProtocol(Message.Protocol.NO)
                .setId(PlayerApplication.getId())
                .build();
        OtherPlayer otherPlayer = OtherPlayer.getPlayerList().stream().filter(other -> other.id == ElectionAlgorithmThread.seekerId).findFirst().get();
        otherPlayer.writeThread.writeMessage(no);
    }
    public static void eliminate(OtherPlayer otherPlayer){
        Message eliminated = Message.newBuilder()
                .setProtocol(Message.Protocol.ELIMINATED)
                .setId(PlayerApplication.getId())
                .build();
        otherPlayer.writeThread.writeMessage(eliminated);
        
    }
}
