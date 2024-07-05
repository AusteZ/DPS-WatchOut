package Player.DistributedAlgorithms;

import Player.Gameplay.OtherPlayer;
import Player.Player;
import Player.Gameplay.PlayerMove;
import Player.Connections.WriteThread;
import proto.messages.MessageOuterClass;
import proto.messages.MessageOuterClass.Message;

import java.util.ArrayList;

public class MutualExclusionAlgorithmThread extends Thread{
    private static boolean permission = false;
    private static int counter = 0;
    private static long timestamp = -1;
    private static Object lockCounter = new Object();
    private static Object permissionLock = new Object();
    private static Object timestampLock = new Object();
    private static ArrayList<WriteThread> queue = new ArrayList<WriteThread>();
    private static MutualExclusionAlgorithmThread thread;
    
    public static void initializeThread(){
        if(thread != null)
            return;
        thread = new MutualExclusionAlgorithmThread();
        thread.start();
    }
    public void run(){
        synchronized(timestampLock) {
            MutualExclusionAlgorithmThread.timestamp = System.nanoTime();
        }
        System.out.println("Exclusion");

        Message exclusion = MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.EXCLUSION)
                .setId(Player.getId())
                .setTimestamp(MutualExclusionAlgorithmThread.timestamp)
                .build();
        ArrayList<OtherPlayer> list = OtherPlayer.getPlayerList();
        for(OtherPlayer otherPlayer : list) {
            if(otherPlayer.id != ElectionAlgorithmThread.seekerId) {
                counter++;
                otherPlayer.writeThread.writeMessage(exclusion);
            }
        }
        synchronized(lockCounter) {
            while(counter > 0) {
                try {
                    lockCounter.wait();
                } catch (InterruptedException e) {
                }
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
            System.out.println("I got permission");
            
        }

        PlayerMove.moveToHomeBase();
        System.out.println("I got away");
        timestamp = -1;
        while(!queue.isEmpty()) {
            sendOk(take());
        }
        for(OtherPlayer player: OtherPlayer.getPlayerList()) {
            sendOut(player.writeThread);
        }
    }

    public static boolean ableToBeEliminated(){
        synchronized(permissionLock) {
            if(!permission) {
                Player.active = false;
                System.out.println("I was caught");
            }
            return !Player.active;
        }
    }
    public static void exclusionRespond(Message message){
        OtherPlayer otherPlayer = OtherPlayer.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
        //System.out.println(MutualExclusionAlgorithmThread.timestamp);
        //System.out.println(message.getTimestamp());
        synchronized(timestampLock) {
            if (timestamp == -1 || timestamp > message.getTimestamp()) {
                sendOk(otherPlayer.writeThread);
                return;
            }
            put(otherPlayer.writeThread);
        }
        
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
    private static void sendOut(WriteThread writeThread){
        Message out = MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.ELIMINATED)
                .setId(Player.getId())
                .build();
        writeThread.writeMessage(out);
    }
    
    private static synchronized void put(WriteThread writeThread){
        queue.add(writeThread);
    }
    private static synchronized WriteThread take(){
        return queue.remove(0);
    }
}
