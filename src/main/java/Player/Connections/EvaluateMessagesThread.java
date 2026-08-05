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
    private final Queue queue;

    private EvaluateMessagesThread(Queue queue) {
        this.queue = queue;
    }

    public static void startInstance(Queue queue) {
        new EvaluateMessagesThread(queue).start();
    }

    public void run() {
        while (true) {
            evaluateProtocol();
        }
    }

    private void evaluateProtocol(){
        Message message = queue.take();

        switch (message.getProtocol()){
            case COORDINATOR -> coordinatorProcess(message);
            case ELECTION -> electionProcess(message);
            case ELIMINATED -> eliminationProcess(message);
            case OK -> okProcess(message);
            case EXCLUSION -> exclusionProcess(message);
            case NO -> noProcess(message);
        }
    }

    private void coordinatorProcess(Message message){
        ElectionAlgorithmThread.coordinatorProcess(message);
    }

    private void electionProcess(Message message){
        if (GamePhase.PLAY == GameState.getGamePhase() && Self.getInstance().playerId() == ElectionAlgorithmThread.seekerId){
            ElectionAlgorithmThread.coordinatorProcess(message);
        }

        if(ElectionAlgorithmThread.electionProcess(message)){
            ElectionAlgorithmThread.initializeThread();
        }
    }

    private void eliminationProcess(Message message){
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

    private void okProcess(Message message){
        OtherPlayerRepository otherPlayerRepository = OtherPlayerRepository.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
        MutualExclusionAlgorithmThread.decreaseCounter();
    }

    private void exclusionProcess(Message message){
        MutualExclusionAlgorithmThread.exclusionRespond(message);
    }

    private void noProcess(Message message){
        OtherPlayerRepository otherPlayerRepository = OtherPlayerRepository.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
        otherPlayerRepository.active = false;
        System.out.println("Got away " + message.getId());
    }
}
