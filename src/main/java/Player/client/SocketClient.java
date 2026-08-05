package Player.client;

import Player.Connections.ReadThread;
import Player.Connections.WriteThread;
import Player.repository.dao.OtherPlayer;
import Player.repository.dao.Player;
import Player.service.MessagingService;
import proto.coordinates.CoordinatesOuterClass;

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

    public OtherPlayer registerWithOtherPlayer(CoordinatesOuterClass.Coordinates playerCoordinates, dtos.PlayerInfo otherPlayerInfo) throws IOException {
        WriteThread otherPlayerWriteThread = createWriteThread(otherPlayerInfo.ipAddress(), otherPlayerInfo.listeningPort());
        otherPlayerWriteThread.writeCoordinates(playerCoordinates);

        ReadThread otherPlayerReadThread = createReadThread();

        CoordinatesOuterClass.Coordinates coords = otherPlayerReadThread.getCoordinates();

        return createOtherPlayer(coords, otherPlayerWriteThread, otherPlayerReadThread);
    }

    public OtherPlayer acceptRegistrationWithOtherPlayers(CoordinatesOuterClass.Coordinates coordinates) throws IOException {
        ReadThread otherPlayerReadThread = createReadThread();

        CoordinatesOuterClass.Coordinates coords = otherPlayerReadThread.getCoordinates();

        WriteThread otherPlayerWriteThread = createWriteThread("localhost", coords.getListeningPort());
        otherPlayerWriteThread.writeCoordinates(coordinates);

        return createOtherPlayer(coords, otherPlayerWriteThread, otherPlayerReadThread);
    }

    private WriteThread createWriteThread(String host, int port) throws IOException {
        Socket otherPlayerReadSocket = new Socket(host, port);
        return new WriteThread(otherPlayerReadSocket);
    }

    private ReadThread createReadThread() throws IOException {
        Socket otherPlayerWriteSocket = welcomeSocket.accept();
        return new ReadThread(otherPlayerWriteSocket, messagingService);
    }

    private OtherPlayer createOtherPlayer(CoordinatesOuterClass.Coordinates coords, WriteThread writeThread, ReadThread readThread) throws IOException {
        Player player = new Player(coords.getListeningPort(), coords.getId());
        return new OtherPlayer(player, writeThread, readThread);
    }

}
