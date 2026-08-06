package administration_server.service;

import Exceptions.PlayerAlreadyExistsException;
import Exceptions.UnitializedPlayerException;
import administration_server.repository.PlayerRepository;
import dtos.PlayerInfo;
import dtos.PlayersDto;

public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public PlayersDto getPlayers() {
        return new PlayersDto(playerRepository.getPlayersList());
    }

    public void registerPlayer(PlayerInfo player) throws PlayerAlreadyExistsException, UnitializedPlayerException {
        if (player.listeningPort() < 0 || player.id() < 0)
            throw new UnitializedPlayerException("ERROR: There is no (or invalid) listening port and id provided. Ids and Listening ports have to be a whole natural number.");

        playerRepository.registerPlayer(player);
    }
}
