package Player.Connections;

import dtos.PlayerInfo;
import dtos.RegistrationResponse;
import Player.HRSimulation.HRSendToServerThread;
import Player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class RestConnection {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<PlayerInfo> registerPlayerToRest(String serverAddress) {
        HttpClient client = HttpClient.newHttpClient();

        String postPath = "/players/registration";

        PlayerInfo playerInfo = new PlayerInfo(
                Player.getId(),
                Player.getListeningPort()
        );

        try {
            String requestBody = objectMapper.writeValueAsString(playerInfo);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverAddress + postPath))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200) {
                RegistrationResponse registrationResponse =
                        objectMapper.readValue(response.body(), RegistrationResponse.class);

                System.out.println(
                        "Coordinates: "
                                + registrationResponse.getCoordinateX()
                                + " "
                                + registrationResponse.getCoordinateY()
                );

                Player.setCoordXAndCoordY(
                        registrationResponse.getCoordinateX(),
                        registrationResponse.getCoordinateY()
                );

                HRSendToServerThread.addClient(client, serverAddress);

                return registrationResponse.getPlayerList();

            } else if (response.statusCode() == 409) {
                System.out.println(response.body());
            } else {
                System.out.println("Registration failed with status: " + response.statusCode());
                System.out.println(response.body());
            }

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            System.out.println("Could not register player: " + e.getMessage());
        }

        return null;
    }
}