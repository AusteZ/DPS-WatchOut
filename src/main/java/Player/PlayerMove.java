package Player;

import com.sun.xml.bind.v2.util.EditDistance;

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
        long runFor = Math.round(5 * (distanceToHomeBase(Player.getCoordX(), Player.getCoordY())) * 1000 );
        try{
            Thread.sleep(runFor + 10*1000);
        } catch(InterruptedException e) {e.printStackTrace();}
    }
    public static double distanceToAnotherPlayer(int coordX, int coordY){
        return Math.sqrt(Math.pow(Player.getCoordX()-coordX,2) + Math.pow(Player.getCoordY()-coordY,2));
    }
    public static void moveToAnotherPlayer(double distance) {
        long runFor = Math.round(5 * (distanceToHomeBase(Player.getCoordX(), Player.getCoordY())) * 1000);
        try{
            Thread.sleep(runFor);
        } catch(InterruptedException e) {e.printStackTrace();}
    }
}
