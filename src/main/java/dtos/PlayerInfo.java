package dtos;

public record PlayerInfo(int id, String ipAddress, int listeningPort) {
    public PlayerInfo(int id, int listeningPort) {
        this(id, "localhost", listeningPort);
    }
}
