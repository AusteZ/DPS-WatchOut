package Player.service;

import Player.repository.dao.Coordinates;
import Player.repository.dao.Player;
import Player.utils.DistanceUtils;

public final class MovementService {
    private Player localPlayer;

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

    public void moveToAnotherPlayer(int coordX, int coordY, double distance) {
        Coordinates coords = localPlayer.getCoordinates();
        Coordinates moveTo = new Coordinates(coordX, coordY);
        long runFor = Math.round(5 * (DistanceUtils.calculateDistance(coords, moveTo)) * 1000);
        try {
            Thread.sleep(runFor);
            localPlayer.setCoordinates(moveTo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
