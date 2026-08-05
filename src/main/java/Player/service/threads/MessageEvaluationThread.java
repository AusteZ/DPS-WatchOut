package Player.service.threads;

import Player.Connections.Queue;
import Player.DistributedAlgorithms.MutualExclusionAlgorithmThread;
import Player.Gameplay.EliminationThread;
import Player.enums.GamePhase;
import Player.repository.OtherPlayerRepository;
import Player.repository.dao.GameState;
import Player.repository.dao.Player;
import Player.service.ElectionService;
import proto.messages.MessageOuterClass.Message;


public class MessageEvaluationThread extends Thread {
    private final Queue queue = new Queue();

    private final GameState gameState;
    private final Player localPlayer;
    private final OtherPlayerRepository otherPlayerRepository;
    private final ElectionService electionService;
    private static MessageEvaluationThread messageEvaluationThread;

    public MessageEvaluationThread(GameState gameState, Player localPlayer, OtherPlayerRepository otherPlayerRepository, ElectionService electionService) {
        this.gameState = gameState;
        this.localPlayer = localPlayer;
        this.otherPlayerRepository = otherPlayerRepository;
        this.electionService = electionService;
    }

    public void putMessage(Message message) {
        queue.put(message);
    }

    @Override
    public void run() {
        while (true) {
            evaluateProtocol();
        }
    }

    private void evaluateProtocol() {
        Message message = queue.take();

        switch (message.getProtocol()) {
            case SEEKER -> coordinatorProcess(message);
            case ELECTION -> electionProcess(message);
            case ELIMINATED -> eliminationProcess(message);
            case OK -> okProcess(message);
            case EXCLUSION -> exclusionProcess(message);
            case NO -> noProcess(message);
        }
    }

    private void coordinatorProcess(Message message) {
        electionService.seekerProcess(message);
    }

    private void electionProcess(Message message) {
        if (GamePhase.PLAY == gameState.getGamePhase() && localPlayer.playerId() == ElectionAlgorithmThread.seekerId) {
            electionService.seekerProcess(message);
        }

        if (electionService.canStartElection(message)) {
            electionService.electionProcess2(message);
            electionService.startElection();
        }
    }

    private void eliminationProcess(Message message) {
        if (ElectionAlgorithmThread.seekerId == localPlayer.playerId()) {
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

    private void okProcess(Message message) {
        OtherPlayerRepository otherPlayerRepository = OtherPlayerRepository.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
        MutualExclusionAlgorithmThread.decreaseCounter();
    }

    private void exclusionProcess(Message message) {
        MutualExclusionAlgorithmThread.exclusionRespond(message);
    }

    private void noProcess(Message message) {
        OtherPlayerRepository otherPlayerRepository = OtherPlayerRepository.getPlayerList().stream().filter(other -> other.id == message.getId()).findFirst().get();
        otherPlayerRepository.active = false;
        System.out.println("Got away " + message.getId());
    }
}
