package player.userinterface;

import player.repository.dao.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.IntPredicate;

public final class CLI implements UserInterface {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public Player setupLocalPlayer() throws Exception {
        int playerId = getPlayerId();
        int listeningPort = getListeningPort();
        return new Player(playerId, listeningPort);
    }

    private int getPlayerId() throws Exception {
        IntPredicate isValid = i -> i >= 0;
        return readInput(isValid, "Please enter player id:", "Please enter a non-negative number");
    }

    private int getListeningPort() throws Exception {
        IntPredicate isValid = i -> i >= 0;
        return readInput(isValid, "Please enter player listening port:", "Please enter a non-negative number");
    }

    private int readInput(IntPredicate isValid, String introductory, String errorMessage) throws IOException {
        int value;
        while (true) {
            print(introductory);

            String input = reader.readLine().trim();

            try {
                value = Integer.parseInt(input);

                if (isValid.test(value)) {
                    return value;
                }

                printError(errorMessage);
            } catch (NumberFormatException e) {
                printError("Please enter a number");
            }
        }
    }

    private void print(String message) {
        System.out.println("UI: " + message);
    }

    private void printError(String message){

    }
}
