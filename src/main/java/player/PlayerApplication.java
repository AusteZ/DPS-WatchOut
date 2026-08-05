package player;

import player.HRSimulation.HRSendToServerThread;
import player.client.AdminServerClient;
import player.client.SocketClient;
import player.listener.MqttListener;
import player.repository.OtherPlayerRepository;
import player.repository.dao.GameState;
import player.repository.dao.Player;
import player.service.ActiveGameService;
import player.service.ElectionService;
import player.service.EliminationService;
import player.service.MessagingService;
import player.service.MovementService;
import player.service.RegistrationService;
import player.userinterface.CliController;
import player.userinterface.UserInterface;
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
