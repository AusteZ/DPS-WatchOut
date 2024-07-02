package Player;

public class PlayerMove{

    public static double distanceToHomeBase(int coordX, int coordY){
        coordX = coordX < 5 ? coordX : (9 - coordX);
        coordY = coordY < 5 ? coordY : (9 - coordY);
        System.out.println("coordX: " + coordX + " coordY: " + coordY);

        int thirdPoint = 4 - (coordX > 0 ? coordX : coordY);

        System.out.println(thirdPoint);
        return Math.sqrt(thirdPoint * thirdPoint + 16);
    }
    public static void moveToHomeBase() {
        long runFor = Math.round(5 * (distanceToHomeBase(Player.getCoordX(), Player.getCoordY()))) * 1000;
        try{
            Thread.sleep(runFor + 10*1000);
        } catch(InterruptedException e) {e.printStackTrace();}
    }
}
