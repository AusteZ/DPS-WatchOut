package Player.Gameplay;

import Player.Connections.ReadThread;
import Player.Connections.WriteThread;

import java.util.ArrayList;

public class OtherPlayer {
    private static ArrayList<OtherPlayer> players = new ArrayList<OtherPlayer>();
    public int id;
    public int coordX;
    public int coordY;
    public WriteThread writeThread;
    public ReadThread readThread;
    public boolean active = false;
    public synchronized static void addOtherPlayer(OtherPlayer otherPlayer)
    { 
        synchronized (players) {
            players.add(otherPlayer);
        }
    }
    public static ArrayList<OtherPlayer> getPlayerList(){
        synchronized (players) {
            return new ArrayList<OtherPlayer>(players);
        }
    }
}
