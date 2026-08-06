package player.service.election;

import player.enums.GamePhase;
import player.repository.GameState;
import player.repository.OtherPlayerRepository;
import player.repository.dao.Coordinates;
import player.repository.dao.OtherPlayer;
import player.repository.dao.Player;
import player.service.game.ActiveGameService;
import player.service.game.EliminationService;
import player.utils.DistanceUtils;
import player.utils.ElectionPriority;
import proto.messages.MessageOuterClass;

public final class ElectionService {
    private final GameState gameState;
    private final Player localPlayer;
    private final OtherPlayerRepository otherPlayerRepository;
    private final ElectionAlgorithmThread electionAlgorithmThread;
    private final ActiveGameService activeGameService;

    public ElectionService(GameState gameState,
                           Player localPlayer,
                           OtherPlayerRepository otherPlayerRepository,
                           ActiveGameService activeGameService,
                           EliminationService eliminationService) {
        this.gameState = gameState;
        this.localPlayer = localPlayer;
        this.otherPlayerRepository = otherPlayerRepository;
        this.activeGameService = activeGameService;
        this.electionAlgorithmThread = new ElectionAlgorithmThread(gameState, localPlayer, otherPlayerRepository, eliminationService);
    }

    public void startElection() {
        if (GamePhase.REGISTERED != gameState.getGamePhase())
            return;
        gameState.setGamePhase(GamePhase.ELECTION);
        electionAlgorithmThread.start();
        System.out.println("Election thread started");
    }

    public boolean canStartElection(MessageOuterClass.Message message) {
        Coordinates coords = localPlayer.getCoordinates();
        double distance = DistanceUtils.calculateDistance(coords, GameState.HOMEBASE_COORDINATES);

        ElectionPriority selfElectionPriority = new ElectionPriority(distance, localPlayer.playerId());
        ElectionPriority otherElectionPriority = new ElectionPriority(message.getDistance(), message.getId());

        return selfElectionPriority.isHigherPriority(otherElectionPriority);
    }

    public void electionProcess(MessageOuterClass.Message message) {
        OtherPlayer otherPlayer = otherPlayerRepository.getPlayerById(message.getId());
        GameState.Seeker seeker = gameState.getSeeker();

        if (seeker != null) {
            MessageOuterClass.Message seekerMessage = createSeekerMessage(seeker.seekerId(), System.currentTimeMillis());
            otherPlayer.writeThread().writeMessage(seekerMessage);
        }

        MessageOuterClass.Message ok = MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.ELECTION_OK)
                .setId(localPlayer.playerId())
                .build();
        otherPlayer.writeThread().writeMessage(ok);
        otherPlayer.player().setActive(true);
        System.out.println("OK to " + otherPlayer.player().playerId());
    }

    public void seekerProcess(MessageOuterClass.Message message) {
        long newTimestamp = message.getTimestamp();
        GameState.Seeker seeker = gameState.getSeeker();

        if (seeker == null || seeker.seekerCreationTimestamp() > newTimestamp && newTimestamp > 0) {
            gameState.setSeeker(message.getId(), newTimestamp);
            electionAlgorithmThread.interrupt();
            System.out.println("SEEKER change to " + seeker.seekerId());
            activeGameService.startActiveGame();
        } else if (localPlayer.playerId() == seeker.seekerId()) {
            OtherPlayer otherPlayer = otherPlayerRepository.getPlayerById(message.getId());
            MessageOuterClass.Message seekerMessage = createSeekerMessage(seeker.seekerId(), seeker.seekerCreationTimestamp());
            otherPlayer.writeThread().writeMessage(seekerMessage);
        }
    }

    private MessageOuterClass.Message createSeekerMessage(int seekerId, long seekerCreationTimestamp) {
        return MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.SEEKER)
                .setId(seekerId)
                .setTimestamp(seekerCreationTimestamp)
                .build();
    }
}
