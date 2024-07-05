package Player.Connections;

import proto.messages.MessageOuterClass.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class WriteThread extends Thread{
    private OutputStream outputStream;
    private Queue queue = new Queue();

    public WriteThread(Socket socket) throws IOException {
        outputStream = socket.getOutputStream();
    }
    public OutputStream getOutputStream() {
        return outputStream;
    }
    public void writeMessage(Message message) {
        queue.put(message);
    }
    public void run(){
        while(true) {
            try {
                queue.take().writeDelimitedTo(outputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
