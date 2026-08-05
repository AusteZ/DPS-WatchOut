package Player;

import Player.HRSimulation.HRSendToServerThread;
import Player.client.AdminServerClient;
import Player.client.SocketClient;
import Player.listener.MqttListener;
import Player.repository.OtherPlayerRepository;
import Player.repository.dao.GameState;
import Player.repository.dao.Player;
import Player.service.ActiveGameService;
import Player.service.ElectionService;
import Player.service.EliminationService;
import Player.service.MessagingService;
import Player.service.MovementService;
import Player.service.RegistrationService;
import Player.userinterface.CliController;
import Player.userinterface.UserInterface;
import library.ApplicationResourcesHandler;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.ServerSocket;
import java.net.http.HttpClient;

public class PlayerApplication {

    public static void main(String[] ignoredArgs) throws Exception {
        Player localPlayer = createLocalPlayer();
        GameState gameState = new GameState();

        String url = ApplicationResourcesHandler.getProperty("server.url");
        HttpClient httpClient = HttpClient.newBuilder().build();
        AdminServerClient adminServerClient = new AdminServerClient(url, httpClient);

        OtherPlayerRepository otherPlayerRepository = new OtherPlayerRepository();

        MovementService movementService = new MovementService(localPlayer);
        ActiveGameService activeGameService = new ActiveGameService(gameState, localPlayer, otherPlayerRepository, movementService);
        EliminationService eliminationService = new EliminationService(gameState, localPlayer, otherPlayerRepository, movementService);
        ElectionService electionService = new ElectionService(gameState, localPlayer, otherPlayerRepository, activeGameService, eliminationService);
        MessagingService messagingService = new MessagingService(gameState, localPlayer, otherPlayerRepository, electionService, activeGameService, eliminationService);

        ServerSocket serverSocket = new ServerSocket(localPlayer.playerListeningPort());
        SocketClient socketClient = new SocketClient(serverSocket, messagingService);

        RegistrationService registrationService = new RegistrationService(gameState, localPlayer, adminServerClient, otherPlayerRepository, socketClient);

        registrationService.register();
        new HRSendToServerThread(localPlayer, httpClient, url).start();

        startMqttListener(gameState, electionService);
    }

    private static Player createLocalPlayer() throws Exception {
        UserInterface userInterface = new CliController();
        return userInterface.setupLocalPlayer();
    }

    private static void startMqttListener(GameState gameState, ElectionService electionService) throws MqttException {
        String broker = ApplicationResourcesHandler.getProperty("mqtt.broker");
        int qos = 2;
        new MqttListener(broker, qos, gameState, electionService);
    }
}
