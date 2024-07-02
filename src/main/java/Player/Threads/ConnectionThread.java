package Player.Threads;

import Beans.PlayerInfo;
import Beans.Players;
import Player.DistributedAlgorithms.ElectionAlgorithmThread;
import Player.OtherPlayer;
import Player.Player;
import proto.coordinates.CoordinatesOuterClass.Coordinates;
import proto.messages.MessageOuterClass.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ConnectionThread extends Thread{
    ServerSocket welcomeSocket;
    boolean startElection = false;
    Coordinates playerCoordinates;
    private static EvaluateMessagesThread evaluation;
    
    //private static Queue messageQueue = new Queue();
    public ConnectionThread(List<PlayerInfo> playerList, int listeningPort){
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
            if(playerList.isEmpty())
                startElection = true;
            //when playerList is not null
            for(PlayerInfo playerInfo : playerList){
                OtherPlayer other = new OtherPlayer();
                
                other.writeThread = new WriteThread(new Socket(playerInfo.getIpAddress(), playerInfo.getListeningPort()));
                
                System.out.println(playerCoordinates);
                playerCoordinates.writeDelimitedTo(other.writeThread.getOutputStream());
                other.readThread = new ReadThread(welcomeSocket.accept(), evaluation.getMessageQueue());
                
                Coordinates coords = Coordinates.parseDelimitedFrom(other.readThread.getInputStream());
                other.id = coords.getId();
                other.coordX = coords.getCoordX();
                other.coordY = coords.getCoordY();
                other.writeThread.start();
                other.readThread.start();
                Message message = Message.newBuilder()
                                .setId(100).setProtocol(Message.Protocol.OK)
                                .build();
                other.writeThread.writeMessage(message);
                OtherPlayer.addOtherPlayer(other);
                
            }
            startElection = true;
            if(Player.getId() == 6 || Player.getId() == 7) {
                ElectionAlgorithmThread.initializeThread();
                System.out.println("Start election");
            }
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
                
                
                
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("error");
            }
        }
    }
}
