package Player.repository;

import Player.repository.dao.OtherPlayer;

import java.util.ArrayList;
import java.util.List;

public class OtherPlayerRepository {
    private final ArrayList<OtherPlayer> players = new ArrayList<>();

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

    public OtherPlayer getPlayerById(int playerId) {
        return players
                .stream()
                .filter(other -> other.player().playerId() == playerId)
                .findFirst()
                .get();
    }
}
