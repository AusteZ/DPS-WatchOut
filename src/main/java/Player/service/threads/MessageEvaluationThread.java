package Player.service.threads;

import Player.Connections.Queue;
import Player.enums.GamePhase;
import Player.repository.OtherPlayerRepository;
import Player.repository.dao.GameState;
import Player.repository.dao.OtherPlayer;
import Player.repository.dao.Player;
import Player.service.ActiveGameService;
import Player.service.ElectionService;
import Player.service.EliminationService;
import proto.messages.MessageOuterClass.Message;


public class MessageEvaluationThread extends Thread {
    private final Queue queue = new Queue();

    private final GameState gameState;
    private final Player localPlayer;
    private final OtherPlayerRepository otherPlayerRepository;
    private final ElectionService electionService;
    private final ActiveGameService activeGameService;
    private final EliminationService eliminationService;

    public MessageEvaluationThread(GameState gameState,
                                   Player localPlayer,
                                   OtherPlayerRepository otherPlayerRepository,
                                   ElectionService electionService,
                                   ActiveGameService activeGameService,
                                   EliminationService eliminationService) {
        this.gameState = gameState;
        this.localPlayer = localPlayer;
        this.otherPlayerRepository = otherPlayerRepository;
        this.electionService = electionService;
        this.activeGameService = activeGameService;
        this.eliminationService = eliminationService;
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
            case OK -> okProcess();
            case EXCLUSION -> exclusionProcess(message);
            case NO -> noProcess(message);
        }
    }

    private void coordinatorProcess(Message message) {
        electionService.seekerProcess(message);
    }

    private void electionProcess(Message message) {
        if (GamePhase.PLAY == gameState.getGamePhase() && localPlayer.playerId() == gameState.getSeeker().seekerId()) {
            electionService.seekerProcess(message);
        }

        if (electionService.canStartElection(message)) {
            electionService.electionProcess(message);
            electionService.startElection();
        }
    }

    private void eliminationProcess(Message message) {
        int seekerId = gameState.getSeeker().seekerId();
        if (seekerId == localPlayer.playerId()) {
            OtherPlayer otherPlayer = otherPlayerRepository.getPlayerById(message.getId());
            otherPlayer.player().setActive(false);

            System.out.println(message.getId() + " is out");
        } else if (seekerId == message.getId()) {
            if (activeGameService.canBeEliminated()) {
                eliminationService.wasEliminated();
            } else {
                eliminationService.cannotBeEliminated();
            }
        } else {
            OtherPlayer otherPlayer = otherPlayerRepository.getPlayerById(message.getId());
            otherPlayer.player().setActive(false);
            System.out.println(message.getId() + " is out");
        }
    }

    private void okProcess() {
        activeGameService.reduceLine();
    }

    private void exclusionProcess(Message message) {
        activeGameService.exclusionRespond(message);
    }

    private void noProcess(Message message) {
        OtherPlayer otherPlayer = otherPlayerRepository.getPlayerById(message.getId());
        otherPlayer.player().setActive(false);
        System.out.println("Got away " + message.getId());
    }
}
