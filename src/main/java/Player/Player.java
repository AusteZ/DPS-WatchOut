package Player;

import Extensions.IntegerExtension;
import Player.Connections.MqttConnection;
import Player.Connections.RestConnection;
import Player.HRSimulation.HRCollectValues;
import Player.Connections.ConnectToOtherPlayersThread;
import dtos.PlayerInfo;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Player {
    private static int id;
    private static int listeningPort;
    private static int coordX;
    private static int coordY;
    public static boolean active = true;
    public static int gamePhase = -1;
    
    public static void main(String[] args) throws IOException, InterruptedException {
        List<PlayerInfo> players = RestConnection.registerPlayerToRest(beginningInterface());
        if(players != null){
            (new ConnectToOtherPlayersThread(players,listeningPort)).start();
            MqttConnection.registerToMqtt();
            System.out.println("x: " + coordX + " y: " + coordY);
            (new HRCollectValues()).start();
        }
    }
    public static int getId(){
        return id;
    }
    public static int getCoordX(){
        return coordX;
    }
    public static int getCoordY(){
        return coordY;
    }
    public static void setCoordXAndCoordY(int coordX, int coordY){
        Player.coordX = coordX;
        Player.coordY = coordY;
    }
    public static int getListeningPort(){
        return listeningPort;
    }
    
    private static String beginningInterface(){
        Scanner scanner = new Scanner(System.in);
        Integer value = 0;
        while(true) {
            System.out.print("Enter player ID: ");
            value = IntegerExtension.tryParseInt(scanner.nextLine());
            if (value == null || value < 0) {
                System.out.println("Please enter a non-negative number");
                continue;
            }
            break;
        }
        Player.id = value;
        
        while(true) {
            System.out.print("Enter player listening port: ");
            value = IntegerExtension.tryParseInt(scanner.nextLine());
            if (value == null || value < 0) {
                System.out.println("Please enter a non-negative number");
                continue;
            }
            break;
        }
        Player.listeningPort = value;
        
        System.out.print("Enter player server address: ");
        String serverAddress = scanner.nextLine();
        if(serverAddress != "localhost") {
            System.out.println("Due to limitations of the project, address can only be localhost");
        }
        String address = "http://localhost:";
        while(true) {
            System.out.print("Enter server port: ");
            value = IntegerExtension.tryParseInt(scanner.nextLine());
            if (value == null || value < 0) {
                System.out.println("Please enter a non-negative number");
                continue;
            }
            break;
        }
        address += value + "";
        return address;
        
    }
    
    
    
    
    
}
