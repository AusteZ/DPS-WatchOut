package administration_server.utils;

import dtos.CoordinatesDto;

import java.util.Random;

public class CoordinateGeneratorUtil {
    public static Random rand = new Random();

    private CoordinateGeneratorUtil() {
        throw new IllegalStateException("Utility class");
    }

    // The Player starts on the edge of one of the axes
    public static CoordinatesDto generateStartingCoordinates() {
        int coord1 = rand.nextInt(10); // starts anywhere on the first axis
        int coord2 = rand.nextInt(2) * 9; // starts on edges of the second axis

        boolean invertAxis = rand.nextBoolean();
        return invertAxis ?  new CoordinatesDto(coord2, coord1) : new CoordinatesDto(coord1, coord2);
    }
}
