package administration_server.service;

import administration_server.exception.PlayerAlreadyExistsException;
import administration_server.exception.UninitializedPlayerException;
import administration_server.mapper.PlayerMapper;
import administration_server.repository.PlayerRepository;
import administration_server.repository.dao.PlayerDao;
import administration_server.utils.CoordinateGeneratorUtil;
import dtos.CoordinatesDto;
import dtos.PlayerInfo;
import dtos.PlayersDto;
import dtos.RegistrationResponse;

import java.util.List;

public final class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public PlayersDto getPlayers() {
        List<PlayerDao> players = playerRepository.getPlayerList();
        List<PlayerInfo> playerDtoList = PlayerMapper.toDto(players);
        return new PlayersDto(playerDtoList);
    }

    public RegistrationResponse registerPlayer(PlayerInfo player) throws PlayerAlreadyExistsException, UninitializedPlayerException {
        validatePlayer(player);

        List<PlayerDao> players = playerRepository.getPlayerList();
        List<PlayerInfo> playerDtoList = PlayerMapper.toDto(players);

        PlayerDao playerDao = PlayerMapper.toDao(player);
        playerRepository.registerPlayer(playerDao);

        CoordinatesDto coordinates = CoordinateGeneratorUtil.generateStartingCoordinates();
        return new RegistrationResponse(playerDtoList, coordinates);
    }

    private void validatePlayer(PlayerInfo player) throws UninitializedPlayerException {
        if(player == null || player.listeningPort() < 0 || player.id() < 0){
            throw new UninitializedPlayerException("The player was not initialized");
        }
    }
}
