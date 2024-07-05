package Administration_Client;

import Extensions.IntegerExtension;
import Extensions.LongExtension;
import io.opencensus.common.ServerStatsFieldEnums;

import java.util.Scanner;

public class AdministrationClient {
    private static final int PORT = 8080;
    private static final String HOST = "localhost";
    private boolean gameStarted = false;
    private MqttThread mqttThread;
    
    public static void main(String[] args) {
        RestConnection.setConnection("localhost", 8080);
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
                continue;
            }
            if (value < 1 || (value > 3 && !gameStarted) || value > 5) {
                System.out.println("Please enter a number in the list");
                continue;
            }
            break;
        }
        
        switch(value) {
            case 1:
                RestConnection.getPlayers();
                break;
            case 2:
                if (!gameStarted) {
                    (new MqttThread("Start game", "game/flow")).start();
                    gameStarted = true;
                    break;
                }
                String customMessage = scanner.nextLine();
                (new MqttThread(customMessage, "game/messages")).start();
                break;
            case 3:
                Integer n, id;
                while(true) {
                    System.out.println("Please enter the id:");
                    id = IntegerExtension.tryParseInt(scanner.nextLine());
                    
                    
                    if(id == null || id <= 0) {
                        System.out.println("Value has to be positive number (non-zero)");
                        continue;
                    }
                    break;
                }
                while(true) {
                    System.out.println("Please enter the n value:");
                    n = IntegerExtension.tryParseInt(scanner.nextLine());

                    if(n == null || n <= 0) {
                        System.out.println("Value has to be positive number (non-zero)");
                        continue;
                    }
                    break;
                }
                RestConnection.getLastNMeasurements(id,n);
                
                break;
            case 4:
                Long time1, time2;
                while(true) {
                    while(true) {
                        System.out.println("Please enter the time1:");
                        time1 = LongExtension.tryParseLong(scanner.nextLine());
                        
                        if(time1 == null || time1 < 0) {
                            System.out.println("Value has to be positive number");
                            continue;
                        }
                        break;
                    }
                    while(true) {
                        System.out.println("Please enter the time2 value:");
                        time2 = LongExtension.tryParseLong(scanner.nextLine());

                        if(time2 == null || time2 <= 0) {
                            System.out.println("Value has to be positive number");
                            continue;
                        }
                        break;
                    }
                    if(time1 > time2) {
                        System.out.println("time1 has to be earlier than time2");
                        continue;
                    }
                    break;
                }
                RestConnection.getMeasurementsBetweenTimestamps(time1,time2);
                break;
        }
    }
}
