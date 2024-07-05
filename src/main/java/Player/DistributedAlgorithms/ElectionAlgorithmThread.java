package Player.DistributedAlgorithms;

import Player.Gameplay.PlayerMove;
import Player.Gameplay.OtherPlayer;
import Player.Player;
import Player.Gameplay.EliminationThread;
import proto.messages.MessageOuterClass;
import proto.messages.MessageOuterClass.Message;

import java.util.ArrayList;

public class ElectionAlgorithmThread extends Thread {
    public static int seekerId;
    private static long seekerCreation = -1;
    private static double playerDistance;
    private static boolean ranElection = false;
    private static Object lock = new Object();
    private static ElectionAlgorithmThread electionThread = new ElectionAlgorithmThread();

    public static void initializeThread() {
        if (ranElection)
            return;
        ranElection = true;
        electionThread.start();
        System.out.println("Election thread started");
    }


    public void run() {
        int count = 0;
        playerDistance = PlayerMove.distanceToHomeBase(Player.getCoordX(), Player.getCoordY());
        ArrayList<OtherPlayer> players = OtherPlayer.getPlayerList();
        MessageOuterClass.Message election = MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.ELECTION)
                .setId(Player.getId())
                .setDistance(playerDistance)
                .build();
        OtherPlayer otherPlayer;
        double otherDistance;
        for (int i = 0; i < players.size() && !electionThread.isInterrupted(); ++i) {
            otherPlayer = players.get(i);
            otherDistance = PlayerMove.distanceToHomeBase(otherPlayer.coordX, otherPlayer.coordY);
            if (otherDistance > playerDistance || (otherDistance == playerDistance && otherPlayer.id > Player.getId())) {
                otherPlayer.writeThread.writeMessage(election);
                count++;
                System.out.println("Election to " + otherPlayer.id);
            } else {
                otherPlayer.active = true;
            }
        }
        System.out.println("Distance: " + playerDistance + " and id: " + Player.getId());
        
        if (count == 0) {
            ElectionAlgorithmThread.seekerCreation = System.currentTimeMillis();
            ElectionAlgorithmThread.seekerId = Player.getId();

            for (OtherPlayer other : players) {
                coordinatorRespond(other);
            }
            synchronized(lock) {
                try {
                    lock.wait(5000);
                    if (!electionThread.isInterrupted()) {
                        Player.gamePhase = 1;
                        new EliminationThread().start();
                        System.out.println("SEEKER");
                    }
                } catch (InterruptedException e) {}
            }
            
        }
    }


    public static boolean electionProcess(Message message) {
        playerDistance = PlayerMove.distanceToHomeBase(Player.getCoordX(), Player.getCoordY());
        if (message.getDistance() > playerDistance || (message.getDistance() == playerDistance && message.getId() > Player.getId()))
            return false;

        OtherPlayer otherPlayer = OtherPlayer.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
        if (seekerCreation != -1) {
            coordinatorRespond(otherPlayer);
            return false;
        }
        Message ok = Message.newBuilder()
                .setProtocol(Message.Protocol.ELECTION_OK)
                .setId(Player.getId())
                .build();
        otherPlayer.writeThread.writeMessage(ok);
        otherPlayer.active = true;
        System.out.println("OK to " + otherPlayer.id);
        return true;
    }


    public static void coordinatorProcess(Message message) {
        long newTimestamp = message.getTimestamp();

        if (seekerCreation == -1 || seekerCreation > newTimestamp && newTimestamp > 0) {
            seekerCreation = newTimestamp;
            seekerId = message.getId();
            electionThread.interrupt();
            System.out.println("SEEKER change to " + seekerId);
            Player.gamePhase = 1;
            MutualExclusionAlgorithmThread.initializeThread();
        
        } else if (Player.getId() == ElectionAlgorithmThread.seekerId){
            coordinatorRespond(OtherPlayer.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get());
        }
    }

    public static void coordinatorRespond(OtherPlayer other) {
        MessageOuterClass.Message coordinator = MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.COORDINATOR)
                .setId(Player.getId())
                .setTimestamp(seekerCreation)
                .build();
        other.writeThread.writeMessage(coordinator);
    }
}
