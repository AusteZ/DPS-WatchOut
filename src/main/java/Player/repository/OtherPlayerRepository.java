package Player.repository;

import Player.Connections.ReadThread;
import Player.Connections.WriteThread;
import Player.repository.dao.OtherPlayer;

import java.util.ArrayList;
import java.util.List;

public class OtherPlayerRepository {
    private static ArrayList<OtherPlayerRepository> oplayers = new ArrayList<OtherPlayerRepository>();
    private final ArrayList<OtherPlayer> players = new ArrayList<>();
    public int id;
    public int coordX;
    public int coordY;
    public WriteThread writeThread;
    public ReadThread readThread;
    public boolean active = false;

    public void addPlayer(OtherPlayer player) {
        synchronized (players) {
            players.add(player);
        }
    }

    public List<OtherPlayer> getPlayerListV2() {
        synchronized (players) {
            return new ArrayList<>(players);
        }
    }

    public synchronized static void addOtherPlayer(OtherPlayerRepository otherPlayerRepository) {
        synchronized (oplayers) {
            oplayers.add(otherPlayerRepository);
        }
    }

    public static ArrayList<OtherPlayerRepository> getPlayerList() {
        synchronized (oplayers) {
            return new ArrayList<OtherPlayerRepository>(oplayers);
        }
    }
}
