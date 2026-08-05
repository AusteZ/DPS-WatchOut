package Player.Gameplay;

import Player.PlayerApplication;

public class PlayerMove {
    public static void moveToHomeBase() {
        //TODO: move to MovementService
    }

    public static double distanceToAnotherPlayer(int coordX, int coordY) {
        return Math.sqrt(Math.pow(PlayerApplication.getCoordX() - coordX, 2) + Math.pow(PlayerApplication.getCoordY() - coordY, 2));
    }

    public static void moveToAnotherPlayer(int coordX, int coordY, double distance) {
        //TODO: move to MovementService
    }
}
