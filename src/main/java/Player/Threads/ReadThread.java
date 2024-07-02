package Player.Threads;

import com.google.protobuf.GeneratedMessageV3;
import proto.messages.ElectionOuterClass.Election;
import proto.messages.MessageOuterClass.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ReadThread extends Thread{
    private InputStream inputStream;
    public Queue queue;

    public ReadThread(Socket socket, Queue queue) throws IOException {
        inputStream = socket.getInputStream();
        this.queue = queue;
    }
    

    public InputStream getInputStream() {
        return inputStream;
    }

    public void run(){
        while(true) {
            try {
                queue.put(Message.parseDelimitedFrom(inputStream));
                //System.out.println(queue.take());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
