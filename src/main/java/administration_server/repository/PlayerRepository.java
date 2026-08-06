package administration_server.repository;

import Exceptions.PlayerAlreadyExistsException;
import dtos.PlayerInfo;

import java.util.ArrayList;
import java.util.List;

public final class PlayerRepository {
    private final List<PlayerInfo> players = new ArrayList<>();

    public synchronized List<PlayerInfo> getPlayersList() {
        return new ArrayList<>(players);
    }

    public synchronized void registerPlayer(PlayerInfo player) throws PlayerAlreadyExistsException {
        for (PlayerInfo p : players) {
            if (p.id() == player.id())
                throw new PlayerAlreadyExistsException("id " + p.id());
            if (p.listeningPort() == player.listeningPort())
                throw new PlayerAlreadyExistsException("listening port " + p.listeningPort());
        }
        players.add(player);
    }
}
