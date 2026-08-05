package Player.repository.dao;

import Player.enums.GamePhase;

public class GameState {
    private volatile GamePhase gamePhase = GamePhase.PRE_REGISTRATION;
    private volatile Seeker seeker;
    public static final Coordinates HOMEBASE_COORDINATES = new Coordinates(5, 5);

    public GamePhase getGamePhase() {
        return this.gamePhase;
    }

    public void setGamePhase(GamePhase gamePhase) {
        this.gamePhase = gamePhase;
    }

    public Seeker getSeeker() {
        return this.seeker;
    }

    public void setSeeker(int playerId, long creationTimestamp) {
        this.seeker = new Seeker(playerId, creationTimestamp);
    }

    public record Seeker(int seekerId, long seekerCreationTimestamp) {
    }
}
