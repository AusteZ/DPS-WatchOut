package Extensions;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ClientResponseExtension {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static HttpResponse<String> postRequest(HttpClient client, String url, Object object) {
        try {
            String requestBody = objectMapper.writeValueAsString(object);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException e) {
            System.out.println("Server not available or request failed: " + e.getMessage());
            return null;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Request was interrupted");
            return null;

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid URL: " + url);
            return null;
        }
    }

    public static HttpResponse<String> getRequest(HttpClient client, String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException e) {
            System.out.println("Server not available or request failed: " + e.getMessage());
            return null;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Request was interrupted");
            return null;

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid URL: " + url);
            return null;
        }
    }

    public static <T> T readBody(HttpResponse<String> response, Class<T> responseType) {
        if (response == null || response.body() == null || response.body().isBlank()) {
            return null;
        }

        try {
            return objectMapper.readValue(response.body(), responseType);
        } catch (IOException e) {
            throw new RuntimeException("Could not parse response body", e);
        }
    }
}