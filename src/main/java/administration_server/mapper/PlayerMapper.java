package administration_server.mapper;

import administration_server.repository.dao.PlayerDao;
import dtos.PlayerInfo;

import java.util.List;

public final class PlayerMapper {
    private PlayerMapper() {
        throw new IllegalStateException("Mapper class");
    }

    public static PlayerDao toDao(PlayerInfo playerInfo) {
        return new PlayerDao(playerInfo.id(), playerInfo.listeningPort());
    }

    public static List<PlayerInfo> toDto(List<PlayerDao> playerDaoList) {
        return playerDaoList
                .stream()
                .map(PlayerMapper::toDto)
                .toList();
    }

    private static PlayerInfo toDto(PlayerDao playerDao) {
        return new PlayerInfo(playerDao.id(), playerDao.listeningPort());
    }
}
