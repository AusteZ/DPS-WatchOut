package Player.userinterface;

import Player.service.RegistrationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.IntPredicate;

public final class CliController {
    BufferedReader reader =
            new BufferedReader(new InputStreamReader(System.in));
    private final RegistrationService registrationService;

    public CliController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    public void run() throws Exception {
        register();
    }

    private void register() throws Exception {
        int playerId = getPlayerId();
        int listeningPort = getListeningPort();

        registrationService.register(playerId, listeningPort);
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

                print(errorMessage);
            } catch (NumberFormatException e) {
                print("Please enter a number");
            }
        }
    }

    private void print(String introductory) {

    }
}
