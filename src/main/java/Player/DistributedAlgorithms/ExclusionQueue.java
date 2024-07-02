package Player.DistributedAlgorithms;

import Player.Threads.WriteThread;

import java.util.ArrayList;


public class ExclusionQueue{
    private ArrayList<WriteThread> queue = new ArrayList<WriteThread>();
    public synchronized void put(WriteThread writeThread) {
        queue.add(writeThread);
        notify();
    }
    public synchronized WriteThread take() {
        try {
            if (queue.isEmpty()) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return queue.remove(0);
    }
}