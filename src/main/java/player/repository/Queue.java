package player.repository;

import proto.messages.MessageOuterClass.Message;

import java.util.ArrayList;

public class Queue {
    private final ArrayList<Message> queue = new ArrayList<>();

    public synchronized void put(Message message) {
        queue.add(message);
        this.notify();
    }

    public synchronized Message take() {
        try {
            while (queue.isEmpty()) {
                this.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return queue.remove(0);
    }
}
