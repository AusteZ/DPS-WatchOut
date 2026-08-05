package player.utils;

import player.repository.dao.Coordinates;

import static player.repository.dao.GameState.HOMEBASE_COORDINATES;

public class DistanceUtils {
    private DistanceUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static double calculateDistanceToHomeBase(Coordinates coords) {
        return calculateDistance(coords, HOMEBASE_COORDINATES);
    }

    public static double calculateDistance(Coordinates coordinatesFrom, Coordinates coordinatesTo) {
        int distanceX = Math.absExact(coordinatesTo.x() - coordinatesFrom.x());
        int distanceY = Math.absExact(coordinatesTo.y() - coordinatesFrom.y());

        return Math.hypot(distanceX, distanceY);
    }
}
