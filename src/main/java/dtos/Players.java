package dtos;

import Exceptions.PlayerAlreadyExistsException;
import Exceptions.UnitializedPlayerException;

import java.util.ArrayList;
import java.util.List;

public class Players {
    private List<PlayerInfo> players;
    private static Players instance;

    private Players() {
        players = new ArrayList<PlayerInfo>();
    }

    public synchronized static Players getInstance() {
        if (instance == null) {
            instance = new Players();
        }
        return instance;
    }

    public synchronized List<PlayerInfo> getPlayersList() {
        return new ArrayList<>(players);
    }

    public synchronized void registerPlayer(PlayerInfo player) throws PlayerAlreadyExistsException, UnitializedPlayerException {
        if (player.listeningPort() < 0 || player.id() < 0)
            throw new UnitializedPlayerException("ERROR: There is no (or invalid) listening port and id provided. Ids and Listening ports have to be a whole natural number.");
        for (PlayerInfo p : players) {
            if (p.id() == player.id())
                throw new PlayerAlreadyExistsException("id " + p.id());
            if (p.listeningPort() == player.listeningPort())
                throw new PlayerAlreadyExistsException("listening port " + p.listeningPort());
        }
        players.add(player);
    }
}
