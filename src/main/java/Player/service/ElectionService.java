package Player.service;

import Player.DistributedAlgorithms.MutualExclusionAlgorithmThread;
import Player.enums.GamePhase;
import Player.repository.OtherPlayerRepository;
import Player.repository.dao.Coordinates;
import Player.repository.dao.GameState;
import Player.repository.dao.OtherPlayer;
import Player.repository.dao.Player;
import Player.service.threads.ElectionAlgorithmThread;
import Player.utils.DistanceUtils;
import Player.utils.ElectionPriority;
import proto.messages.MessageOuterClass;

public final class ElectionService {
    private final GameState gameState;
    private final Player localPlayer;
    private final OtherPlayerRepository otherPlayerRepository;
    private final ElectionAlgorithmThread electionAlgorithmThread;

    public ElectionService(GameState gameState, Player localPlayer, OtherPlayerRepository otherPlayerRepository) {
        this.gameState = gameState;
        this.localPlayer = localPlayer;
        this.otherPlayerRepository = otherPlayerRepository;
        this.electionAlgorithmThread = new ElectionAlgorithmThread(gameState, localPlayer, otherPlayerRepository);
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

    public void electionProcess2(MessageOuterClass.Message message) {
        OtherPlayer otherPlayer = getOtherPlayerById(message.getId());
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
            ElectionAlgorithmThread.getElectionThread().interrupt();
            System.out.println("SEEKER change to " + seeker.seekerId());
            gameState.setGamePhase(GamePhase.PLAY);
            MutualExclusionAlgorithmThread.initializeThread();
        } else if (localPlayer.playerId() == seeker.seekerId()) {
            OtherPlayer otherPlayer = getOtherPlayerById(message.getId());
            MessageOuterClass.Message seekerMessage = createSeekerMessage(seeker.seekerId(), seeker.seekerCreationTimestamp());
            otherPlayer.writeThread().writeMessage(seekerMessage);
        }
    }

    private OtherPlayer getOtherPlayerById(int playerId) {
        return otherPlayerRepository.getPlayerListV2()
                .stream()
                .filter(other -> other.player().playerId() == playerId)
                .findFirst()
                .get();
    }

    private MessageOuterClass.Message createSeekerMessage(int seekerId, long seekerCreationTimestamp) {
        return MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.SEEKER)
                .setId(seekerId)
                .setTimestamp(seekerCreationTimestamp)
                .build();
    }
}
