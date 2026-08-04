package Player.Connections;

import Player.DistributedAlgorithms.ElectionAlgorithmThread;
import Player.PlayerApplication;
import Player.repository.OtherPlayerRepository;
import dtos.PlayerInfo;
import proto.coordinates.CoordinatesOuterClass.Coordinates;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

//TODO: have threads for each registration, not the whole thing
public class ConnectToOtherPlayersThread extends Thread {
    ServerSocket welcomeSocket;
    static boolean startElection = false;
    Coordinates playerCoordinates;
    private static EvaluateMessagesThread evaluation;

    public static boolean getGameStart() {
        return startElection;
    }

    public ConnectToOtherPlayersThread(List<PlayerInfo> playerList, int listeningPort) {
        try {
            evaluation = new EvaluateMessagesThread();
            welcomeSocket = new ServerSocket(listeningPort);
            playerCoordinates = Coordinates.newBuilder()
                    .setId(PlayerApplication.getId())
                    .setCoordX(PlayerApplication.getCoordX())
                    .setCoordY(PlayerApplication.getCoordY())
                    .setListeningPort(PlayerApplication.getListeningPort())
                    .build();
            evaluation.start();
            for (PlayerInfo playerInfo : playerList) {
                OtherPlayerRepository other = new OtherPlayerRepository();

                other.writeThread = new WriteThread(new Socket(playerInfo.ipAddress(), playerInfo.listeningPort()));

                playerCoordinates.writeDelimitedTo(other.writeThread.getOutputStream());
                other.readThread = new ReadThread(welcomeSocket.accept(), evaluation.getMessageQueue());

                Coordinates coords = Coordinates.parseDelimitedFrom(other.readThread.getInputStream());
                other.id = coords.getId();
                other.coordX = coords.getCoordX();
                other.coordY = coords.getCoordY();
                other.writeThread.start();
                other.readThread.start();
                OtherPlayerRepository.addOtherPlayer(other);

            }
            startElection = true;
        } catch (IOException e) {
        }

    }

    public void run() {
        while (true) {
            try {
                Socket connectionSocket = welcomeSocket.accept();
                startElection = false;
                OtherPlayerRepository other = new OtherPlayerRepository();
                other.readThread = new ReadThread(connectionSocket, evaluation.getMessageQueue());

                Coordinates coords = Coordinates.parseDelimitedFrom(other.readThread.getInputStream());

                other.id = coords.getId();
                other.coordX = coords.getCoordX();
                other.coordY = coords.getCoordY();
                other.writeThread = new WriteThread(new Socket("localhost", coords.getListeningPort()));


                playerCoordinates.writeDelimitedTo(other.writeThread.getOutputStream());
                other.writeThread.start();
                other.readThread.start();
                OtherPlayerRepository.addOtherPlayer(other);

                if (PlayerApplication.gamePhase > 0 && PlayerApplication.getId() == ElectionAlgorithmThread.seekerId) {
                    ElectionAlgorithmThread.coordinatorRespond(other);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Problem with socket");
                break;
            }
        }
    }
}
