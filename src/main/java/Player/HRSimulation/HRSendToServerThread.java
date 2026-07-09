package Player.HRSimulation;

import dtos.MeasurementList;
import dtos.MeasurementValue;
import Player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class HRSendToServerThread extends Thread {
    private static HttpClient client;
    private static String serverAddress;

    private static final String POST_PATH = "/analytics/postmeasurements";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void addClient(HttpClient client, String serverAddress) {
        HRSendToServerThread.client = client;
        HRSendToServerThread.serverAddress = serverAddress;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10 * 1000);

                List<MeasurementValue> list = HRCollectValues.getLastMeasurements();

                if (list.isEmpty()) {
                    continue;
                }

                MeasurementList measurementList = new MeasurementList(
                        Player.getId(),
                        System.currentTimeMillis(),
                        list
                );

                String requestBody = objectMapper.writeValueAsString(measurementList);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(serverAddress + POST_PATH))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

                if (response.statusCode() != 200) {
                    System.out.println(response.statusCode() + " " + response.body());
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("HR sending thread was interrupted.");
                break;

            } catch (IOException e) {
                System.out.println("Failed to send heart rate measurements: " + e.getMessage());

            } catch (Exception e) {
                System.out.println("Unexpected error while sending heart rate measurements: " + e.getMessage());
            }
        }
    }
}