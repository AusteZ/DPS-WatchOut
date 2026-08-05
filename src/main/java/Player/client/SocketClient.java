package Player.client;

import Player.Connections.EvaluateMessagesThread;
import Player.Connections.Queue;
import Player.Connections.ReadThread;
import Player.Connections.WriteThread;
import Player.repository.dao.OtherPlayer;
import Player.repository.dao.Player;
import dtos.PlayerInfo;
import proto.coordinates.CoordinatesOuterClass;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketClient {
    private final ServerSocket welcomeSocket;
    private final Queue queue;

    public SocketClient(int listeningPort) throws IOException {
        this.queue = new Queue();
        EvaluateMessagesThread.startInstance(queue);
        welcomeSocket = new ServerSocket(listeningPort);
    }

    public OtherPlayer registerWithOtherPlayer(CoordinatesOuterClass.Coordinates playerCoordinates, PlayerInfo otherPlayerInfo) throws IOException {
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
        return new ReadThread(otherPlayerWriteSocket, queue);
    }

    private OtherPlayer createOtherPlayer(CoordinatesOuterClass.Coordinates coords, WriteThread writeThread, ReadThread readThread) throws IOException {
        Player player = new Player(coords.getListeningPort(), coords.getId());
        return new OtherPlayer(player, writeThread, readThread);
    }

}
