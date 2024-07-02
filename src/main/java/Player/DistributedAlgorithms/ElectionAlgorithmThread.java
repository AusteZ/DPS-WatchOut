package Player.DistributedAlgorithms;

import Player.PlayerMove;
import Player.OtherPlayer;
import Player.Player;
import Player.Threads.EliminationThread;
import proto.messages.MessageOuterClass;
import proto.messages.MessageOuterClass.Message;

import java.time.Instant;
import java.util.ArrayList;

public class ElectionAlgorithmThread extends Thread{
    public static int seekerId;
    private static Instant seekerCreation = null;
    private static double playerDistance;
    private static boolean ranElection = false;
    private static ElectionAlgorithmThread electionThread = new ElectionAlgorithmThread();
    private 
    
    public static void initializeThread(){
        if(ranElection)
            return;
        ranElection = true;
        electionThread.start();
        System.out.println("Election thread started");
        
        
    }
    

    public void run(){
        int count = 0;
        ArrayList<OtherPlayer> players = OtherPlayer.getPlayerList();
        MessageOuterClass.Message election = MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.ELECTION)
                .setId(Player.getId())
                .setDistance(playerDistance)
                .build();
        OtherPlayer otherPlayer;
        double otherDistance;
        for(int i = 0; i < players.size() && !electionThread.isInterrupted(); ++i) {
            otherPlayer = players.get(i);
            otherDistance = PlayerMove.distanceToHomeBase(otherPlayer.coordX, otherPlayer.coordY);
            if (otherDistance > playerDistance || (otherDistance == playerDistance && otherPlayer.id > Player.getId())) {
                otherPlayer.writeThread.writeMessage(election);
                count++;
                System.out.println("Election to " + otherPlayer.id);
            } else {
                otherPlayer.active = true;
            }
            i++;
        }
        if(count == 0 && !electionThread.isInterrupted()) {
            System.out.println("SEEKER");
            Instant timestamp = Instant.now();
            ElectionAlgorithmThread.seekerCreation = timestamp;
            ElectionAlgorithmThread.seekerId = Player.getId();

            for (OtherPlayer other : players) {
                coordinatorRespond(other);
            }
            try {
                electionThread.wait(3000);
                new EliminationThread().start();
            } catch (InterruptedException e) {
            }
        }
        
        
    }
    public static boolean electionProcess(Message message) {
        playerDistance = PlayerMove.distanceToHomeBase(Player.getCoordX(), Player.getCoordY());
        if (message.getDistance() > playerDistance || (message.getDistance() == playerDistance && message.getId() > Player.getId()))
            return false;
        
        OtherPlayer otherPlayer = OtherPlayer.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
        if (seekerCreation != null) {
            coordinatorRespond(otherPlayer);
            return false;
        }
        Message ok = Message.newBuilder()
                .setProtocol(Message.Protocol.OK)
                .setId(Player.getId())
                .build();
        otherPlayer.writeThread.writeMessage(ok);
        otherPlayer.active = true;
        System.out.println("OK to " + otherPlayer.id);
        return true;
    }
    
    
    public static void coordinatorProcess(Message message) {
        Instant timestamp = Instant.ofEpochSecond(message.getTimestamp().getSeconds(), message.getTimestamp().getNanos());
            
        if(seekerCreation == null || seekerCreation.isAfter(timestamp)) {
            seekerCreation = timestamp;
            seekerId = message.getId();
            electionThread.interrupt();
            System.out.println("SEEKER change to " + seekerId);
            electionThread.interrupt();
            
        } else {
            coordinatorRespond(OtherPlayer.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get());
            
        }
    }
    private static void coordinatorRespond(OtherPlayer other){
        MessageOuterClass.Message coordinator = MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.COORDINATOR)
                .setId(Player.getId())
                .setTimestamp(
                        com.google.protobuf.Timestamp.newBuilder()
                                .setSeconds(seekerCreation.getEpochSecond())
                                .setNanos(seekerCreation.getNano())
                )
                .build();
        other.writeThread.writeMessage(coordinator);
        new MutualExclusionAlgorithmThread().start();
    }
    
}
