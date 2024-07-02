package Player.Threads;

import Player.DistributedAlgorithms.ElectionAlgorithmThread;
import Player.DistributedAlgorithms.MutualExclusionAlgorithmThread;
import Player.OtherPlayer;
import Player.Player;
import proto.messages.MessageOuterClass.Message;

public class EvaluateMessagesThread extends Thread{
    private Queue queue;
    public EvaluateMessagesThread(){
        this.queue = new Queue();
    }
    public Queue getMessageQueue(){
        return queue;
    }
    public void run(){
        while(true) {
            Message message = queue.take();
            
            if(message.getProtocol() == Message.Protocol.ELECTION && ElectionAlgorithmThread.electionProcess(message)) 
                ElectionAlgorithmThread.initializeThread();
            
            if(message.getProtocol() == Message.Protocol.COORDINATOR)
                ElectionAlgorithmThread.coordinatorProcess(message);
            
            if(message.getProtocol() == Message.Protocol.OK) {
                OtherPlayer otherPlayer = OtherPlayer.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
                if(!otherPlayer.active)
                    otherPlayer.active = true;
                else {
                    MutualExclusionAlgorithmThread.decreaseCounter();
                }
            }
            if(message.getProtocol() == Message.Protocol.ELIMINATED) {
                if(ElectionAlgorithmThread.seekerId == Player.getId()) {
                    OtherPlayer otherPlayer = OtherPlayer.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
                    otherPlayer.active = false;
                    System.out.println("Caught " + message.getId());
                } else if(ElectionAlgorithmThread.seekerId == message.getId()) {
                    if(MutualExclusionAlgorithmThread.ableToBeEliminated()) {
                        EliminationThread.wasEliminated();
                    } else {
                        EliminationThread.cannotBeEliminated();
                    }
                } else {
                    OtherPlayer otherPlayer = OtherPlayer.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
                    otherPlayer.active = false;
                }
            }
            if(message.getProtocol() == Message.Protocol.NO) {
                OtherPlayer otherPlayer = OtherPlayer.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
                otherPlayer.active = false;
                System.out.println("Got away " + message.getId());
            }
            
        }
        
    }
}
