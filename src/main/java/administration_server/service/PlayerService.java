package administration_server.service;

import administration_server.exception.PlayerAlreadyExistsException;
import administration_server.exception.UninitializedPlayerException;
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

    public RegistrationResponse registerPlayer(PlayerInfo player) throws PlayerAlreadyExistsException, UninitializedPlayerException {
        validatePlayer(player);

        List<PlayerInfo> players = playerRepository.getPlayersList();

        playerRepository.registerPlayer(player);

        CoordinatesDto coordinates = CoordinateGeneratorUtil.generateStartingCoordinates();
        return new RegistrationResponse(players, coordinates);
    }

    private void validatePlayer(PlayerInfo player) throws UninitializedPlayerException {
        if(player == null || player.listeningPort() < 0 || player.id() < 0){
            throw new UninitializedPlayerException("The player was not initialized");
        }
    }
}
