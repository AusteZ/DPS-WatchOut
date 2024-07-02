package Beans;

import Exceptions.PlayerAlreadyExistsException;
import Exceptions.UnitializedPlayerException;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
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
            throw new UnitializedPlayerException();
        for(PlayerInfo p : players) {
            if(p.getId() == player.getId())
                throw new PlayerAlreadyExistsException(p.getId());
        }
        players.add(player);
    }
}
