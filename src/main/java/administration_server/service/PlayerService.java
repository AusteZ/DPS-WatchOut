package administration_server.service;

import Exceptions.PlayerAlreadyExistsException;
import Exceptions.UnitializedPlayerException;
import administration_server.repository.PlayerRepository;
import administration_server.utils.CoordinateGeneratorUtil;
import dtos.CoordinatesDto;
import dtos.PlayerInfo;
import dtos.PlayersDto;
import dtos.RegistrationResponse;

import java.util.List;

public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public PlayersDto getPlayers() {
        return new PlayersDto(playerRepository.getPlayersList());
    }

    public RegistrationResponse registerPlayer(PlayerInfo player) throws PlayerAlreadyExistsException, UnitializedPlayerException {
        if (player.listeningPort() < 0 || player.id() < 0)
            throw new UnitializedPlayerException("ERROR: There is no (or invalid) listening port and id provided. Ids and Listening ports have to be a whole natural number.");

        List<PlayerInfo> players = playerRepository.getPlayersList();

        playerRepository.registerPlayer(player);

        CoordinatesDto coordinates = CoordinateGeneratorUtil.generateStartingCoordinates();
        return new RegistrationResponse(players, coordinates);
    }
}
