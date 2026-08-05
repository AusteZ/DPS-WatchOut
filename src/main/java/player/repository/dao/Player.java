package player.repository.dao;

public class Player {
    private final int playerListeningPort;
    private final int playerId;
    private Coordinates coordinates;
    private boolean isActive;

    public Player(int playerListeningPort, int playerId) {
        this.playerListeningPort = playerListeningPort;
        this.playerId = playerId;
    }

    public Player(int playerListeningPort, int playerId, Coordinates coordinates) {
        this.playerListeningPort = playerListeningPort;
        this.playerId = playerId;
        this.coordinates = coordinates;
    }

    public Integer playerId() {
        return playerId;
    }

    public Integer playerListeningPort() {
        return playerListeningPort;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }
}
