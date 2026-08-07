package player.client;

import dtos.MeasurementListDto;
import dtos.PlayerInfo;
import dtos.RegistrationResponse;
import library.HttpClientWrapper;
import player.repository.dao.Player;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class AdminServerClient {
    private final static Logger LOGGER = Logger.getLogger(AdminServerClient.class.getName());
    private final static String REGISTRATION_PATH = "/players/registration";
    private static final String ANALYTICS_PATH = "/analytics/postmeasurements";

    private final String registrationUrl;
    private final String analyticsUrl;

    private final HttpClientWrapper httpClientWrapper;

    public AdminServerClient(String baseUrl, HttpClientWrapper httpClientWrapper) {
        this.registrationUrl = baseUrl + REGISTRATION_PATH;
        this.analyticsUrl = baseUrl + ANALYTICS_PATH;
        this.httpClientWrapper = httpClientWrapper;
    }

    public RegistrationResponse register(Player self) {
        PlayerInfo playerInfo = new PlayerInfo(self.playerId(), self.playerListeningPort());
        URI uri = URI.create(registrationUrl);
        return httpClientWrapper.postRequestWithResponse(uri, playerInfo, RegistrationResponse.class);
    }

    public void postMeasurements(MeasurementListDto measurementListDto) throws IOException, InterruptedException {
        URI uri = URI.create(analyticsUrl);
        httpClientWrapper.postRequest(uri, measurementListDto);
    }
}
