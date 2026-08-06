package player.client;

import player.repository.dao.OtherPlayer;
import player.repository.dao.Player;
import player.repository.dao.ReadThread;
import player.repository.dao.WriteThread;
import player.service.messaging.MessagingService;
import proto.coordinates.RegistrationRequestOuterClass.RegistrationRequest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketClient {
    private final ServerSocket welcomeSocket;
    private final MessagingService messagingService;

    public SocketClient(ServerSocket welcomeSocket, MessagingService messagingService) throws IOException {
        this.messagingService = messagingService;
        this.welcomeSocket = welcomeSocket;
    }

    public OtherPlayer registerWithOtherPlayer(RegistrationRequest registrationRequest, dtos.PlayerInfo otherPlayerInfo) throws IOException {
        WriteThread otherPlayerWriteThread = createWriteThread(otherPlayerInfo.ipAddress(), otherPlayerInfo.listeningPort());
        otherPlayerWriteThread.writeRegistrationRequest(registrationRequest);

        ReadThread otherPlayerReadThread = createReadThread();

        RegistrationRequest otherPlayerRegistrationRequest = otherPlayerReadThread.getRegistrationRequest();

        return createOtherPlayer(otherPlayerRegistrationRequest, otherPlayerWriteThread, otherPlayerReadThread);
    }

    public OtherPlayer acceptRegistrationWithOtherPlayers(RegistrationRequest registrationRequest) throws IOException {
        ReadThread otherPlayerReadThread = createReadThread();
        RegistrationRequest otherPlayerRegistrationRequest = otherPlayerReadThread.getRegistrationRequest();

        WriteThread otherPlayerWriteThread = createWriteThread("localhost", otherPlayerRegistrationRequest.getListeningPort());
        otherPlayerWriteThread.writeRegistrationRequest(registrationRequest);

        return createOtherPlayer(registrationRequest, otherPlayerWriteThread, otherPlayerReadThread);
    }

    private WriteThread createWriteThread(String host, int port) throws IOException {
        Socket otherPlayerReadSocket = new Socket(host, port);
        return new WriteThread(otherPlayerReadSocket);
    }

    private ReadThread createReadThread() throws IOException {
        Socket otherPlayerWriteSocket = welcomeSocket.accept();
        return new ReadThread(otherPlayerWriteSocket, messagingService);
    }

    private OtherPlayer createOtherPlayer(RegistrationRequest request, WriteThread writeThread, ReadThread readThread) {
        Player player = new Player(request.getListeningPort(), request.getId());
        return new OtherPlayer(player, writeThread, readThread);
    }
}
