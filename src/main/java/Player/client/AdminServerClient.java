package Player.client;

import Player.dao.Self;
import com.fasterxml.jackson.databind.ObjectMapper;
import dtos.PlayerInfo;
import dtos.RegistrationResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

public class AdminServerClient {
    private final static Logger LOGGER = Logger.getLogger(AdminServerClient.class.getName());
    private final static String REGISTRATION_PATH = "/players/registration";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String registrationUrl;
    private final HttpClient client;

    public AdminServerClient(String baseUrl, HttpClient client) {
        this.registrationUrl = baseUrl + REGISTRATION_PATH;
        this.client = client;
    }

    public RegistrationResponse register(Self self) {
        PlayerInfo playerInfo = new PlayerInfo(self.playerId(), self.playerListeningPort());

        try {
            String requestBody = objectMapper.writeValueAsString(playerInfo);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(registrationUrl))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                translateError(response);
            }
            return objectMapper.readValue(response.body(), RegistrationResponse.class);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            System.out.println("Could not register player: " + e.getMessage());
        } catch (Exception ex) {

        }

        return null;
    }

    private void translateError(HttpResponse<String> response) throws IllegalAccessException {
        if (response.statusCode() == 409) {
            throw new IllegalAccessException();
        }


    }
}
