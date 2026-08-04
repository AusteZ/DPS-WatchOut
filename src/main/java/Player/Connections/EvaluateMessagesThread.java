package Player.Connections;

import Player.DistributedAlgorithms.ElectionAlgorithmThread;
import Player.DistributedAlgorithms.MutualExclusionAlgorithmThread;
import Player.Gameplay.EliminationThread;
import Player.PlayerApplication;
import Player.enums.GamePhase;
import Player.repository.OtherPlayerRepository;
import Player.repository.dao.GameState;
import Player.repository.dao.Self;
import proto.messages.MessageOuterClass.Message;


public class EvaluateMessagesThread extends Thread {
    private Queue queue;

    public EvaluateMessagesThread() {
        this.queue = new Queue();
    }

    public Queue getMessageQueue() {
        return queue;
    }

    public void run() {
        while (true) {
            Message message = queue.take();

            if (message.getProtocol() == Message.Protocol.COORDINATOR || (GamePhase.PLAY == GameState.getGamePhase() && message.getProtocol() == Message.Protocol.ELECTION && Self.getInstance().playerId() == ElectionAlgorithmThread.seekerId))
                ElectionAlgorithmThread.coordinatorProcess(message);

            if (message.getProtocol() == Message.Protocol.ELECTION && ElectionAlgorithmThread.electionProcess(message))
                ElectionAlgorithmThread.initializeThread();


            if (message.getProtocol() == Message.Protocol.OK) {
                OtherPlayerRepository otherPlayerRepository = OtherPlayerRepository.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
                MutualExclusionAlgorithmThread.decreaseCounter();
            }
            if (message.getProtocol() == Message.Protocol.ELIMINATED) {
                if (ElectionAlgorithmThread.seekerId == PlayerApplication.getId()) {
                    OtherPlayerRepository otherPlayerRepository = OtherPlayerRepository.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
                    otherPlayerRepository.active = false;
                    System.out.println(message.getId() + " is out");
                } else if (ElectionAlgorithmThread.seekerId == message.getId()) {
                    if (MutualExclusionAlgorithmThread.ableToBeEliminated()) {
                        EliminationThread.wasEliminated();
                    } else {
                        EliminationThread.cannotBeEliminated();
                    }
                } else {
                    OtherPlayerRepository otherPlayerRepository = OtherPlayerRepository.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
                    otherPlayerRepository.active = false;
                    System.out.println(message.getId() + " is out");
                }
            }
            if (message.getProtocol() == Message.Protocol.EXCLUSION)
                MutualExclusionAlgorithmThread.exclusionRespond(message);
            if (message.getProtocol() == Message.Protocol.NO) {
                OtherPlayerRepository otherPlayerRepository = OtherPlayerRepository.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
                otherPlayerRepository.active = false;
                System.out.println("Got away " + message.getId());
            }

        }

    }
}
