package administration_client.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import dtos.AverageDto;
import dtos.PlayerInfo;
import dtos.PlayersDto;
import dtos.TimestampsDto;
import jakarta.ws.rs.core.UriBuilder;
import library.HttpClientWrapper;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

public class AdminServerClient {
    private final static Logger LOGGER = Logger.getLogger(AdminServerClient.class.getName());

    private final static String GET_PLAYERS_PATH = "/players/list";
    private final static String ANALYTICS_BETWEEN_PATH = "/analytics/getvaluesbetweentimestamps";
    private final static String ANALYTICS_LAST_PATH = "/analytics/getlastnmeasurements/{playerId}/{count}";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String getPlayersUrl;
    private final String analyticsBetweenUrl;
    private final String analyticsLastUrl;

    private final HttpClientWrapper httpClientWrapper;

    public AdminServerClient(String url, HttpClientWrapper httpClientWrapper) {
        this.getPlayersUrl = url + GET_PLAYERS_PATH;
        this.analyticsBetweenUrl = url + ANALYTICS_BETWEEN_PATH;
        this.analyticsLastUrl = url + ANALYTICS_LAST_PATH;
        this.httpClientWrapper = httpClientWrapper;

    }

    public List<PlayerInfo> getAllPlayers() {
        URI uri = URI.create(getPlayersUrl);
        PlayersDto playersDto = httpClientWrapper.getRequest(uri, PlayersDto.class);
        return playersDto.playersList();
    }

    public double getMeasurementAverageBetweenTimestamps(Long startTimestamp, Long endTimestamp) {
        TimestampsDto timestampsDto = new TimestampsDto(startTimestamp, endTimestamp);
        URI uri = URI.create(analyticsBetweenUrl);
        AverageDto averageDto = httpClientWrapper.postRequestWithResponse(uri, timestampsDto, AverageDto.class);
        return averageDto.average();
    }

    public double getLatestMeasurementAverage(int playerId, int count) {
        URI uri = UriBuilder.fromUri(analyticsLastUrl)
                .resolveTemplate("playerId", playerId)
                .resolveTemplate("count", count)
                .build();

        AverageDto averageDto = httpClientWrapper.getRequest(uri, AverageDto.class);
        return averageDto.average();
    }
}
