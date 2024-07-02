package Player.Threads;

import Player.DistributedAlgorithms.ElectionAlgorithmThread;
import Player.Player;
import Player.OtherPlayer;
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
                if(otherPlayer.status == null)
                    otherPlayer.status = OtherPlayer.Status.Active;
                else if (ElectionAlgorithmThread.seekerId == Player.getId())
                    continue;
                continue;
            }
            
        }
    }
}
