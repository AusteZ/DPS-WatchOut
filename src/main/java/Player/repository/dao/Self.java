package Player.repository.dao;

public final class Self {
    private static Player instance;

    private Self() {
    }

    public static Player getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Self has not been initialized");
        }
        return instance;
    }

    public static Player createInstance(Integer playerId, Integer playerListeningPort) {
        if (instance == null) {
            instance = new Player(playerId, playerListeningPort);
        }

        return instance;
    }

}
