package administration_server.repository;

import administration_server.exception.PlayerAlreadyExistsException;
import administration_server.repository.dao.PlayerDao;

import java.util.ArrayList;
import java.util.List;

public final class PlayerRepository {
    private final List<PlayerDao> players = new ArrayList<>();

    public synchronized List<PlayerDao> getPlayerList() {
        return new ArrayList<>(players);
    }

    public synchronized void registerPlayer(PlayerDao player) throws PlayerAlreadyExistsException {
        for (PlayerDao p : players) {
            if (p.id() == player.id())
                throw new PlayerAlreadyExistsException("id " + p.id());
            if (p.listeningPort() == player.listeningPort())
                throw new PlayerAlreadyExistsException("listening port " + p.listeningPort());
        }

        players.add(player);
    }
}
