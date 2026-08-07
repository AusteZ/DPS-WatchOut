package administration_server.userinterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CLI {
    private static final Logger LOGGER = Logger.getLogger(CLI.class.getName());

    public void exit() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("UI: Press Enter to exit.");
            reader.readLine();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception while listening for exit.", e);
        }
    }
}
