package Player.DistributedAlgorithms;

import Player.OtherPlayer;
import Player.Player;
import Player.PlayerMove;
import Player.Threads.WriteThread;
import proto.messages.MessageOuterClass;
import proto.messages.MessageOuterClass.Message;

import java.time.Instant;
import java.util.ArrayList;

public class MutualExclusionAlgorithmThread extends Thread{
    private static boolean permission = false;
    private static int counter = 0;
    private static Instant timestamp;
    private static Object lockCounter = new Object();
    private static Object permissionLock = new Object();
    private static ArrayList<WriteThread> queue = new ArrayList<WriteThread>();
    public void run(){
        MutualExclusionAlgorithmThread.timestamp = Instant.now();

        Message exclusion = MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.COORDINATOR)
                .setId(Player.getId())
                .setTimestamp(
                        com.google.protobuf.Timestamp.newBuilder()
                                .setSeconds(timestamp.getEpochSecond())
                                .setNanos(timestamp.getNano())
                )
                .build();
        
        for(OtherPlayer otherPlayer : OtherPlayer.getPlayerList()) {
            if(otherPlayer.id != ElectionAlgorithmThread.seekerId) {
                counter++;
                otherPlayer.writeThread.writeMessage(exclusion);
            }
        }
        
        synchronized(permissionLock) {
            if(!Player.active) {
                while(!queue.isEmpty()) {
                    sendOk(take());
                }
                return;
            }
            MutualExclusionAlgorithmThread.permission = true;
        }
        synchronized(lockCounter) {
            while(counter > 0) {
                try {
                    lockCounter.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        new PlayerMove();
        timestamp = null;
        while(!queue.isEmpty()) {
            sendOk(take());
        }
    }

    public static boolean ableToBeEliminated(){
        synchronized(permissionLock) {
            if(!permission) {
                Player.active = false;
            }
            return !Player.active;
        }
    }
    public static void exclusionRespond(Message message){
        Instant messageTimestamp = Instant.ofEpochSecond(message.getTimestamp().getSeconds(), message.getTimestamp().getNanos());
        OtherPlayer otherPlayer = OtherPlayer.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
        if(timestamp == null || timestamp.isAfter(messageTimestamp)) {
            sendOk(otherPlayer.writeThread);
            return;
        }
        put(otherPlayer.writeThread);
    }
    public static void decreaseCounter() {
        synchronized(lockCounter) {
            counter--;
            lockCounter.notify();
        }
        
    }
    private static void sendOk(WriteThread writeThread){
        Message ok = MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.OK)
                .setId(Player.getId())
                .build();
        writeThread.writeMessage(ok);
    }
    
    private static synchronized void put(WriteThread writeThread){
        queue.add(writeThread);
    }
    private static synchronized WriteThread take(){
        return queue.remove(0);
    }
}
