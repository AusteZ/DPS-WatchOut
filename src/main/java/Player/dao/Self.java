package Player.dao;

public final class Self {
    private final Integer playerId;
    private final Integer playerListeningPort;
    Coordinates coordinates;

    private static Self instance;

    private Self(Integer playerId, Integer playerListeningPort) {
        this.playerId = playerId;
        this.playerListeningPort = playerListeningPort;
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

    public void setCoordinates(Integer x, Integer y) {
        this.coordinates = new Coordinates(x, y);
    }

    public static Self getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Self has not been initialized");
        }
        return instance;
    }

    public static Self createInstance(Integer playerId, Integer playerListeningPort) {
        if (instance == null) {
            instance = new Self(playerId, playerListeningPort);
        }

        return instance;
    }

}
