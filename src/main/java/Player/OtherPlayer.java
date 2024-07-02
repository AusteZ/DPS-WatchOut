package Player;

import Player.Threads.ReadThread;
import Player.Threads.WriteThread;

import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;

public class OtherPlayer {
    private static ArrayList<OtherPlayer> players = new ArrayList<OtherPlayer>();
    public int id;
    public int coordX;
    public int coordY;
    public WriteThread writeThread;
    public ReadThread readThread;
    public Status status = null;
    public enum Status{
        Active,
        Seeker,
        Out,
    }
    public synchronized static void addOtherPlayer(OtherPlayer otherPlayer){
        players.add(otherPlayer);
    }
    public static ArrayList<OtherPlayer> getPlayerList(){
        return players;
    }
}
