package player.service.game;

import player.Connections.WriteThread;
import player.repository.OtherPlayerRepository;
import player.repository.dao.GameState;
import player.repository.dao.OtherPlayer;
import player.repository.dao.Player;
import proto.messages.MessageOuterClass;
import proto.messages.MessageOuterClass.Message;

import java.util.ArrayList;
import java.util.List;

public class MutualExclusionAlgorithmThread extends Thread {
    private long timestamp = -1;
    private final Object timestampLock = new Object();

    private int playersAheadInLineCount = 0;
    private final Object playersAheadInLineCountLock = new Object();

    private boolean permissionForHomebase = false;
    private final Object permissionForHomebaseLock = new Object();

    private final ArrayList<WriteThread> queue = new ArrayList<>();

    private final GameState gameState;
    private final Player localPlayer;
    private final OtherPlayerRepository otherPlayerRepository;
    private final MovementService movementService;

    public MutualExclusionAlgorithmThread(GameState gameState,
                                          Player localPlayer,
                                          OtherPlayerRepository otherPlayerRepository,
                                          MovementService movementService) {
        this.gameState = gameState;
        this.localPlayer = localPlayer;
        this.otherPlayerRepository = otherPlayerRepository;
        this.movementService = movementService;
    }

    public void run() {
        List<OtherPlayer> players = otherPlayerRepository.getPlayerListV2();
        getInLine(players);
        waitToBeFirstInLine();
        securePermissionToGoToHomebase();

        movementService.moveToHomeBase();

        announceEscape(players);
    }

    private void getInLine(List<OtherPlayer> players) {
        synchronized (timestampLock) {
            timestamp = System.nanoTime();
        }
        System.out.println("Exclusion");

        Message exclusion = MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.EXCLUSION)
                .setId(localPlayer.playerId())
                .setTimestamp(timestamp)
                .build();

        for (OtherPlayer player : players) {
            if (player.player().playerId() != gameState.getSeeker().seekerId()) {
                playersAheadInLineCount++;
                player.writeThread().writeMessage(exclusion);
            }
        }
    }

    private void waitToBeFirstInLine() {
        synchronized (playersAheadInLineCountLock) {
            while (playersAheadInLineCount > 0) {
                try {
                    playersAheadInLineCountLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private void securePermissionToGoToHomebase() {
        synchronized (permissionForHomebaseLock) {
            if (!localPlayer.isActive()) {
                while (!queue.isEmpty()) {
                    sendOk(take());
                }
                return;
            }
            permissionForHomebase = true;
            System.out.println("I got permission");

        }
    }

    private void announceEscape(List<OtherPlayer> players) {
        System.out.println("I got away");
        timestamp = -1;
        while (!queue.isEmpty()) {
            sendOk(take());
        }


        for (OtherPlayer player : players) {
            sendOut(player.writeThread());
        }
    }

    public boolean hasPermissionForHomebase() {
        synchronized (permissionForHomebaseLock) {
            return !permissionForHomebase;
        }
    }

    /*public boolean ableToBeEliminated() {
        synchronized (permissionForHomebaseLock) {
            if (!permissionForHomebase) {
                localPlayer.setActive(false);
                System.out.println("I was caught");
            }
            return !PlayerApplication.active;

            if (!permissionForHomebase) {
                localPlayer.setActive(false);
                System.out.println("I was caught");
            }

            return localPlayer.isActive();

            return !permissionForHomebase || !PlayerApplication.active;
        }
    }*/

    public void decreaseCounter() {
        synchronized (playersAheadInLineCountLock) {
            playersAheadInLineCount--;
            playersAheadInLineCountLock.notify();
        }
    }

    private void sendOk(WriteThread writeThread) {
        Message ok = MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.OK)
                .setId(localPlayer.playerId())
                .build();
        writeThread.writeMessage(ok);
    }

    private void sendOut(WriteThread writeThread) {
        Message out = MessageOuterClass.Message.newBuilder()
                .setProtocol(MessageOuterClass.Message.Protocol.ELIMINATED)
                .setId(localPlayer.playerId())
                .build();
        writeThread.writeMessage(out);
    }

    public void exclusionRespond(Message message) {
        OtherPlayer otherPlayer = otherPlayerRepository.getPlayerById(message.getId());
        //System.out.println(MutualExclusionAlgorithmThread.timestamp);
        //System.out.println(message.getTimestamp());
        synchronized (timestampLock) {
            if (timestamp == -1 || timestamp > message.getTimestamp()) {
                sendOk(otherPlayer.writeThread());
                return;
            }
            put(otherPlayer.writeThread());
        }

    }

    private synchronized void put(WriteThread writeThread) {
        queue.add(writeThread);
    }

    private synchronized WriteThread take() {
        return queue.removeFirst();
    }
}
