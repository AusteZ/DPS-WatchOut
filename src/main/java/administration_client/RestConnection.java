package administration_client;

import Beans.Average;
import Beans.PlayerInfo;
import Beans.Players;
import Beans.Timestamps;
import Player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.UriBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RestConnection {
    private static String path;
    private static HttpClient client;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void setConnection(String host, int port) {
        path = "http://" + host + ":" + port;
        client = HttpClient.newHttpClient();
    }

    public static void getPlayers() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(UriBuilder.fromUri(path).build())
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println(response);

            if (response.statusCode() == 200) {
                Players players = objectMapper.readValue(response.body(), Players.class);

                for (PlayerInfo p : players.getPlayersList()) {
                    System.out.println("id: " + p.getId());
                }
            } else {
                System.out.println("Request failed with status: " + response.statusCode());
                System.out.println(response.body());
            }

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            System.out.println("Failed to get players: " + e.getMessage());
        }
    }

    public static void getMeasurementsBetweenTimestamps(Long startTimestamp, Long endTimestamp) {
        PlayerInfo playerInfo = new PlayerInfo(Player.getId(), Player.getListeningPort());
        Timestamps timestamps = new Timestamps(startTimestamp, endTimestamp);

        try {
            String jsonBody = objectMapper.writeValueAsString(timestamps);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(path + "/analytics/getvaluesbetweentimestamps"))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200) {
                Average averageResponse = objectMapper.readValue(response.body(), Average.class);
                Double average = averageResponse.getAverage();

                System.out.println(
                        "The heart rate average between "
                                + startTimestamp
                                + " and "
                                + endTimestamp
                                + " is "
                                + average
                );
            } else if (response.statusCode() == 409) {
                System.out.println(response.body());
            } else {
                System.out.println("Request failed with status: " + response.statusCode());
                System.out.println(response.body());
            }

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            System.out.println("Failed to get measurements between timestamps: " + e.getMessage());
        }
    }

    public static void getLastNMeasurements(Integer id, Integer n) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(path + "/analytics/getlastnmeasurements/" + id + "/" + n))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println(response);

            if (response.statusCode() == 200) {
                Average averageResponse = objectMapper.readValue(response.body(), Average.class);
                Double average = averageResponse.getAverage();

                System.out.println(
                        "The " + n + " last heart rate average of " + id + " is " + average
                );
            } else {
                System.out.println("Request failed with status: " + response.statusCode());
                System.out.println(response.body());
            }

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            System.out.println("Failed to get last measurements: " + e.getMessage());
        }
    }
}
