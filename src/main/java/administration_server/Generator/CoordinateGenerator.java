package administration_server.Generator;

import java.util.Random;

public class CoordinateGenerator {
    public static Random rand = new Random();
    public static int[] generateStartingPosition() {
        int coord1 = rand.nextInt(10);
        int coord2 = rand.nextInt(2) * 9;
        if(rand.nextInt(2) == 0) {
            return new int[]{coord1, coord2};
        }
        return new int[]{coord2, coord1};
    }
}
