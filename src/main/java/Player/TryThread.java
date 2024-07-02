package Player;

import java.util.ArrayList;

public class TryThread extends Thread{
    public static volatile ArrayList<String> hi = new ArrayList<>();
    
    public synchronized void run(){
        if(Thread.currentThread().isInterrupted() ){
            System.out.println("labas");
        }else {
            System.out.println("labas13568");
        }
    }
}
