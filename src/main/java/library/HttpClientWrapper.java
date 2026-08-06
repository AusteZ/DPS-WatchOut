package library;

import com.fasterxml.jackson.databind.ObjectMapper;
import library.exceptions.HttpClientException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class HttpClientWrapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient;

    public HttpClientWrapper(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public <T> T postRequestWithResponse(URI uri, Object object, Class<T> responseType) {
        try {
            String requestBody = objectMapper.writeValueAsString(object);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            validateResponse(httpResponse);

            return readBody(httpResponse, responseType);
        } catch (IOException | InterruptedException | HttpClientException e) {
            throw new HttpClientException("Error while sending POST request to " + uri, e);
        }
    }

    public void postRequest(URI uri, Object object) {
        try {
            String requestBody = objectMapper.writeValueAsString(object);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<Void> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            validateResponse(httpResponse);
        } catch (IOException | InterruptedException | HttpClientException e) {
            throw new HttpClientException("Error while sending POST request to " + uri, e);
        }
    }

    public <T> T getRequest(URI uri, Class<T> responseType) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            validateResponse(response);

            return readBody(response, responseType);
        } catch (IOException | InterruptedException | HttpClientException e) {
            throw new HttpClientException("Error while sending GET request to " + uri, e);
        }
    }

    public void validateResponse(HttpResponse<?> response) {
        if (response == null) {
            throw new HttpClientException("Request was unsuccessful, response body is null");
        }

        if (response.statusCode() != 200) {
            throw new HttpClientException("Request was unsuccessful, status code: " + response.statusCode());
        }
    }

    public <T> T readBody(HttpResponse<String> response, Class<T> responseType) {
        if (response.body() == null || response.body().isBlank()) {
            throw new HttpClientException.HttpResponseException("Response body is null or empty");
        }

        try {
            return objectMapper.readValue(response.body(), responseType);
        } catch (IOException e) {
            throw new HttpClientException.HttpResponseException("Could not parse response body", e);
        }
    }
}