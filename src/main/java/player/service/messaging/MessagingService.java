package player.service.messaging;

import player.repository.OtherPlayerRepository;
import player.repository.dao.GameState;
import player.repository.dao.Player;
import player.service.election.ElectionService;
import player.service.game.ActiveGameService;
import player.service.game.EliminationService;
import proto.messages.MessageOuterClass;

public final class MessagingService {
    private final MessageEvaluationThread messageEvaluationThread;

    public MessagingService(GameState gameState,
                            Player localPlayer,
                            OtherPlayerRepository otherPlayerRepository,
                            ElectionService electionService,
                            ActiveGameService activeGameService,
                            EliminationService eliminationService) {
        this.messageEvaluationThread = new MessageEvaluationThread(gameState, localPlayer, otherPlayerRepository, electionService, activeGameService, eliminationService);
        this.messageEvaluationThread.start();
    }

    public void putMessage(MessageOuterClass.Message message){
        messageEvaluationThread.putMessage(message);
    }
}
