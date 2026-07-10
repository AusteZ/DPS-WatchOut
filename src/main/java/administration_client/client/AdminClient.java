package administration_client.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dtos.AverageDto;
import dtos.PlayerInfo;
import dtos.Players;
import dtos.TimestampsDto;
import jakarta.ws.rs.core.UriBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminClient {
    private final static Logger LOGGER = Logger.getLogger(AdminClient.class.getName());

    private final String baseUrl;
    private final HttpClient httpClient;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public AdminClient(String url, HttpClient httpClient) {
        this.baseUrl = url;
        this.httpClient = httpClient;
    }

    public List<PlayerInfo> getAllPlayers() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(UriBuilder.fromUri(baseUrl).build())
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            return extractPlayerInfo(response);
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Request to get all players failed.", e);
            return List.of();
        }
    }

    private List<PlayerInfo> extractPlayerInfo(HttpResponse<String> response) throws JsonProcessingException {
        if (response.statusCode() != 200) {
            LOGGER.log(Level.SEVERE, String.format("Request to get all players failed on status code. [statusCode=%d, body=%s]", response.statusCode(), response.body()));
            return List.of();
        }

        Players players = objectMapper.readValue(response.body(), Players.class);
        return players.getPlayersList();
    }

    public double getMeasurementAverageBetweenTimestamps(Long startTimestamp, Long endTimestamp) {
        try {
            TimestampsDto timestampsDto = new TimestampsDto(startTimestamp, endTimestamp);
            String jsonBody = objectMapper.writeValueAsString(timestampsDto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/analytics/getvaluesbetweentimestamps"))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200) {
                AverageDto averageDtoResponse = objectMapper.readValue(response.body(), AverageDto.class);
                return averageDtoResponse.average();
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
        return 0.0;
    }

    public double getLatestMeasurementAverage(int playerId, int count) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/analytics/getlastnmeasurements/" + playerId + "/" + count))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println(response);

            if (response.statusCode() == 200) {
                AverageDto averageDtoResponse = objectMapper.readValue(response.body(), AverageDto.class);
                return averageDtoResponse.average();
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
        return 0.0;
    }
}
