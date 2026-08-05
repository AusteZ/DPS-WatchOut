package Player.service;

import Player.repository.OtherPlayerRepository;
import Player.repository.dao.GameState;
import Player.repository.dao.Player;
import Player.service.threads.MessageEvaluationThread;
import proto.messages.MessageOuterClass;

public final class MessagingService {
    private final MessageEvaluationThread messageEvaluationThread;

    public MessagingService(GameState gameState, Player localPlayer, OtherPlayerRepository otherPlayerRepository, ElectionService electionService) {
        this.messageEvaluationThread = new MessageEvaluationThread(gameState, localPlayer, otherPlayerRepository, electionService);
    }

    public void startEvaluatingMessages(){
        messageEvaluationThread.start();
    }

    public void putMessage(MessageOuterClass.Message message){
        messageEvaluationThread.putMessage(message);
    }
}
