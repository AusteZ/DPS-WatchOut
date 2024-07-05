package Player.Connections;

import proto.messages.MessageOuterClass.Message;

import java.util.ArrayList;

public class Queue {
    private ArrayList<Message> queue = new ArrayList<Message>();
    public synchronized void put(Message message) {
        queue.add(message);
        notify();
    }
    public synchronized Message take() {
        try {
            while (queue.isEmpty()) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return queue.remove(0);
    }
    
}
