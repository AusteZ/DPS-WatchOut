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

    public synchronized static Players getInstance(){
        if(instance == null) {
            instance = new Players();
        }
        return instance;
    }
    public synchronized List<PlayerInfo> getPlayersList() {
        return new ArrayList<>(players);
    }
    public synchronized void RegisterPlayer(PlayerInfo player) throws PlayerAlreadyExistsException, UnitializedPlayerException {
        if(player.getListeningPort() < 0 || player.getId() < 0)
            throw new UnitializedPlayerException("ERROR: There is no (or invalid) listening port and id provided. Ids and Listening ports have to be a whole natural number.");
        for(PlayerInfo p : players) {
            if(p.getId() == player.getId())
                throw new PlayerAlreadyExistsException("id " + p.getId());
            if(p.getListeningPort() == player.getListeningPort())
                throw new PlayerAlreadyExistsException("listening port " + p.getListeningPort());
        }
        players.add(player);
    }
}
