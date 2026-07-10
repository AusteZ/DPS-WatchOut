package administration_client.userinterface;

import dtos.PlayerInfo;

import java.util.List;
import java.util.Scanner;
import java.util.function.IntPredicate;

public class ConsoleUserInterface extends UserInterface {
    private boolean gameStarted = false;
    private final Scanner scanner;
    List<String> choices;

    private ConsoleUserInterface(UserInterfaceBridge userInterfaceBridge) {
        super(userInterfaceBridge);
        scanner = new Scanner(System.in);
    }

    public static UserInterface getInstance(UserInterfaceBridge userInterfaceBridge) {
        return new ConsoleUserInterface(userInterfaceBridge);
    }

    public void runInterface() {
        print("Administration Client is running.");
        setInitialChoices();

        while (true) {
            printChoices();
            int choice = readInputChoices();
            readChoice(choice);
        }
    }

    private void setInitialChoices() {
        choices = List.of("Print the list of players",
                "Start the game");
    }

    private void setGameChoices() {
        choices = List.of("Print the list of players",
                "Send message to players",
                "Compute last n heart rate values of a player.",
                "Compute the average heart rate values of all players between given start and end timestamps");
    }

    private void printChoices() {
        print("Choose:");

        int count = 1;
        for (String choice : choices) {
            print(String.format("%d. %s", count, choice));
            count++;
        }
    }

    private void readChoice(int choice) {
        switch (choice) {
            case 1 -> choice1();
            case 2 -> choice2();
            case 3 -> choice3();
            case 4 -> choice4();
        }
    }

    private void choice1() {
        List<PlayerInfo> players = userInterfaceBridge.getAllPlayers();
        int count = 1;
        for (PlayerInfo player : players) {
            print(String.format("%d playerId: %s", count, player.id()));
            count++;
        }
    }

    private void choice2() {
        if (gameStarted) {
            String customMessage = scanner.nextLine();
            userInterfaceBridge.sendMessageToPlayers(customMessage);
            return;
        }

        userInterfaceBridge.startGame();
        gameStarted = true;
        setGameChoices();
    }

    private void choice3() {
        IntPredicate isValid = i -> i > 0;
        String errorMessage = "Value has to be natural number";
        int playerId = readInput(isValid, "Please enter the player id:", errorMessage);
        int latestCount = readInput(isValid, "Please enter how many latest measurements to include:", errorMessage);

        double average = userInterfaceBridge.getLatestMeasurementAverage(playerId, latestCount);
        print(String.format("The latest %d measurement average of player id %d is %f.", latestCount, playerId, average));
    }

    private void choice4() {
        long startTimestamp, endTimestamp;

        while (true) {
            startTimestamp = readLongInput("Please enter the starting timestamp:");
            endTimestamp = readLongInput("Please enter the ending timestamp:");

            if (startTimestamp > endTimestamp) {
                break;
            }

            print("Starting timestamp has to be before ending timestamp.");
        }
        double average = userInterfaceBridge.getMeasurementAverageBetweenTimestamps(startTimestamp, endTimestamp);
        print(String.format("The average heart rate between %d and %d is %f", startTimestamp, endTimestamp, average));
    }

    private int readInputChoices() {
        IntPredicate isValid = i -> i > 0 && i < choices.size();
        return readInput(isValid, "Please enter your choice:", "Please enter a number in the list");
    }

    private int readInput(IntPredicate isValid, String introductory, String errorMessage) {
        int value;
        while (true) {
            print(introductory);

            String input = scanner.nextLine();

            try {
                value = Integer.parseInt(input);

                if (isValid.test(value)) {
                    return value;
                }

                print(errorMessage);
            } catch (NumberFormatException e) {
                print("Please enter a number");
            }
        }
    }

    private long readLongInput(String introductory) {
        long value;
        while (true) {
            print(introductory);

            String input = scanner.nextLine();

            try {
                value = Long.parseLong(input);

                if (value >= 0) {
                    return value;
                }

                print("Value has to be non-negative number");
            } catch (NumberFormatException e) {
                print("Please enter a number");
            }
        }
    }

    private void print(String message) {
        System.out.printf("UI: %s%n", message);
    }
}
