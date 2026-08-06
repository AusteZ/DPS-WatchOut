package player.userinterface;

import player.repository.dao.Player;

public interface UserInterface {
    Player setupLocalPlayer() throws Exception;
}
