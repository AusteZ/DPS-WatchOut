package player.repository.dao;

import player.Connections.ReadThread;
import player.Connections.WriteThread;

public class OtherPlayer {
    Player player;
    WriteThread writeThread;
    ReadThread readThread;

    public OtherPlayer(Player player, WriteThread writeThread, ReadThread readThread)
    {
        this.player = player;
        this.writeThread = writeThread;
        this.writeThread.start();
        this.readThread = readThread;
        this.readThread.start();
    }

    public Player player() {
        return player;
    }

    public WriteThread writeThread(){
        return writeThread;
    }

    public ReadThread readThread(){
        return readThread;
    }
}
