package player.service.game;

import player.repository.dao.Coordinates;
import player.repository.dao.Player;
import player.utils.DistanceUtils;

public final class MovementService {
    private final Player localPlayer;

    public MovementService(Player localPlayer) {
        this.localPlayer = localPlayer;
    }

    public void moveToHomeBase() {
        Coordinates coords = localPlayer.getCoordinates();
        long runFor = Math.round(5 * (DistanceUtils.calculateDistanceToHomeBase(coords)) * 1000);
        try {
            Thread.sleep(runFor + 10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void moveToAnotherPlayer(Coordinates moveTo) {
        Coordinates coords = localPlayer.getCoordinates();
        long runFor = Math.round(5 * (DistanceUtils.calculateDistance(coords, moveTo)) * 1000);
        try {
            Thread.sleep(runFor);
            localPlayer.setCoordinates(moveTo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
