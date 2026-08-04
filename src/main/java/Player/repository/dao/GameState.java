package Player.repository.dao;

import Player.enums.GamePhase;

public class GameState {
    private static volatile GamePhase gamePhase = GamePhase.PRE_REGISTRATION;
    private static int seekerId;

    public static GamePhase getGamePhase() {
        return gamePhase;
    }

    public static void setGamePhase(GamePhase gamePhase) {
        GameState.gamePhase = gamePhase;
    }

    public static int getSeekerId() {
        return seekerId;
    }

    public static void setSeekerId(int seekerId) {
        GameState.seekerId = seekerId;
    }
}
