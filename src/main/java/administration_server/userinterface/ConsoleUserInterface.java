package administration_server.userinterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleUserInterface implements UserInterface {
    private static final Logger LOGGER = Logger.getLogger(ConsoleUserInterface.class.getName());
    private static ConsoleUserInterface instance;

    public static UserInterface getInstance() {
        if (instance != null) {
            throw new IllegalStateException("Interface already running");
        }
        instance = new ConsoleUserInterface();
        return instance;
    }

    public void runInterface() {
        exit();
    }

    private void exit() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("UI: Press Enter to exit.");
            reader.readLine();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception while listening for exit.", e);
        }
    }
}
