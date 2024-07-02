package Administration_Client;

import Extensions.IntegerExtension;

import java.util.Scanner;

public class AdministrationClient {
    private static final int PORT = 8080;
    private static final String HOST = "localhost";
    private boolean gameStarted = false;
    private MqttThread mqttThread;
    
    public static void main(String[] args) {
        RestConnection.setConnection("localhost", 8080, "/players/list");
        MqttThread.setConnection("tcp://localhost:1883");
        new AdministrationClient().cosoleInterface();
    }
    public void cosoleInterface(){
        System.out.println("Administration Client is running");
        Scanner scanner = new Scanner(System.in);

        while(true) {
            printChoices();
            readChoice(scanner);
        }
    }
    private void printChoices() {
        System.out.println("Choose:");
        System.out.println("1. Print the list of players");
        if(!gameStarted)
            System.out.println("2. Start the game");
        else {
            System.out.println("2. Send message to players");
            System.out.println("3. Compute last n heart rate values of a player.");
            System.out.println("4. Compute the average heart rate values of all players between time1 and time2");
        }
    }
    private void readChoice(Scanner scanner){
        Integer value = 0;
        while(true) {
            value = IntegerExtension.tryParseInt(scanner.nextLine());
            if (value == null) {
                System.out.println("Please enter a number");
                return;
            }
            if (value < 1 || (value > 3 && !gameStarted) || value > 5) {
                System.out.println("Please enter a number in the list");
                return;
            }
            break;
        }
        
        switch(value) {
            case 1:
                RestConnection.getPlayers();
                break;
            case 2:
                if (!gameStarted) {
                    (new MqttThread("hello", "game/flow")).start();
                    break;
                }
                (new MqttThread("SECONDHELLO", "game/messages")).start();
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }
}
