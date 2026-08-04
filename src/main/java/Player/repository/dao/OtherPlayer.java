package Player.repository.dao;

import Player.Connections.ReadThread;
import Player.Connections.WriteThread;

import java.io.InputStream;
import java.io.OutputStream;

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

    public OutputStream writeStream(){
        return writeThread.getOutputStream();
    }

    public InputStream readStream(){
        return readThread.getInputStream();
    }
}
