package Player.Connections;

import dtos.PlayerInfo;
import Player.DistributedAlgorithms.ElectionAlgorithmThread;
import Player.Gameplay.OtherPlayer;
import Player.Player;
import proto.coordinates.CoordinatesOuterClass.Coordinates;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ConnectToOtherPlayersThread extends Thread{
    ServerSocket welcomeSocket;
    static boolean startElection = false;
    Coordinates playerCoordinates;
    private static EvaluateMessagesThread evaluation;
    
    public static boolean getGameStart(){
        return startElection;
    }
    
    public ConnectToOtherPlayersThread(List<PlayerInfo> playerList, int listeningPort){
        try {
            evaluation = new EvaluateMessagesThread();
            welcomeSocket = new ServerSocket(listeningPort);
            playerCoordinates = Coordinates.newBuilder()
                    .setId(Player.getId())
                    .setCoordX(Player.getCoordX())
                    .setCoordY(Player.getCoordY())
                    .setListeningPort(Player.getListeningPort())
                    .build();
            evaluation.start();
            for(PlayerInfo playerInfo : playerList){
                OtherPlayer other = new OtherPlayer();
                
                other.writeThread = new WriteThread(new Socket(playerInfo.getIpAddress(), playerInfo.getListeningPort()));
                
                playerCoordinates.writeDelimitedTo(other.writeThread.getOutputStream());
                other.readThread = new ReadThread(welcomeSocket.accept(), evaluation.getMessageQueue());
                
                Coordinates coords = Coordinates.parseDelimitedFrom(other.readThread.getInputStream());
                other.id = coords.getId();
                other.coordX = coords.getCoordX();
                other.coordY = coords.getCoordY();
                other.writeThread.start();
                other.readThread.start();
                OtherPlayer.addOtherPlayer(other);
                
            }
            startElection = true;
        } catch (IOException e) {}

    }
    public void run(){
        
        while(true){
            try {
                Socket connectionSocket = welcomeSocket.accept();
                startElection = false;
                OtherPlayer other = new OtherPlayer();
                other.readThread = new ReadThread(connectionSocket, evaluation.getMessageQueue());

                Coordinates coords = Coordinates.parseDelimitedFrom(other.readThread.getInputStream());
                
                other.id = coords.getId();
                other.coordX = coords.getCoordX();
                other.coordY = coords.getCoordY();
                other.writeThread = new WriteThread(new Socket("localhost", coords.getListeningPort()));


                playerCoordinates.writeDelimitedTo(other.writeThread.getOutputStream());
                other.writeThread.start();
                other.readThread.start();
                OtherPlayer.addOtherPlayer(other);
                
                if(Player.gamePhase > 0 && Player.getId() == ElectionAlgorithmThread.seekerId) {
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
