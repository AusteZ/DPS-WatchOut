package player;

import Simulators.Buffer;
import library.ApplicationResourcesHandler;
import org.eclipse.paho.client.mqttv3.MqttException;
import player.client.AdminServerClient;
import player.client.SocketClient;
import player.listener.MqttListener;
import player.repository.GameState;
import player.repository.HRBuffer;
import player.repository.MeasurementValueRepository;
import player.repository.OtherPlayerRepository;
import player.repository.dao.Player;
import player.service.election.ElectionService;
import player.service.game.ActiveGameService;
import player.service.game.EliminationService;
import player.service.game.MovementService;
import player.service.messaging.MessagingService;
import player.service.registration.RegistrationService;
import player.service.simulation.HeartRateSimulationService;
import player.userinterface.CliController;
import player.userinterface.UserInterface;

import java.net.ServerSocket;
import java.net.http.HttpClient;

public class PlayerApplication {

    public static void main(String[] ignoredArgs) throws Exception {
        Player localPlayer = createLocalPlayer();
        GameState gameState = new GameState();

        AdminServerClient adminServerClient = createAdminServerClient();

        OtherPlayerRepository otherPlayerRepository = new OtherPlayerRepository();

        MovementService movementService = new MovementService(localPlayer);
        ActiveGameService activeGameService = new ActiveGameService(gameState, localPlayer, otherPlayerRepository, movementService);
        EliminationService eliminationService = new EliminationService(gameState, localPlayer, otherPlayerRepository, movementService);
        ElectionService electionService = new ElectionService(gameState, localPlayer, otherPlayerRepository, activeGameService, eliminationService);
        MessagingService messagingService = new MessagingService(gameState, localPlayer, otherPlayerRepository, electionService, activeGameService, eliminationService);

        ServerSocket serverSocket = new ServerSocket(localPlayer.playerListeningPort());
        SocketClient socketClient = new SocketClient(serverSocket, messagingService);

        RegistrationService registrationService = new RegistrationService(gameState, localPlayer, adminServerClient, otherPlayerRepository, socketClient);

        Buffer buffer = new HRBuffer();
        MeasurementValueRepository measurementValueRepository = new MeasurementValueRepository();
        HeartRateSimulationService heartRateSimulationService = new HeartRateSimulationService(localPlayer, buffer, adminServerClient, measurementValueRepository);

        registrationService.register();
        heartRateSimulationService.startHeartRateSimulation();
        startMqttListener(gameState, electionService);
    }

    private static Player createLocalPlayer() throws Exception {
        UserInterface userInterface = new CliController();
        return userInterface.setupLocalPlayer();
    }

    private static AdminServerClient createAdminServerClient(){
        String url = ApplicationResourcesHandler.getProperty("server.url");
        HttpClient httpClient = HttpClient.newBuilder().build();
        return new AdminServerClient(url, httpClient);
    }

    private static void startMqttListener(GameState gameState, ElectionService electionService) throws MqttException {
        String broker = ApplicationResourcesHandler.getProperty("mqtt.broker");
        int qos = 2;
        new MqttListener(broker, qos, gameState, electionService);
    }
}
