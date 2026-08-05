package Player.Connections;

import proto.coordinates.CoordinatesOuterClass;
import proto.messages.MessageOuterClass.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class WriteThread extends Thread {
    private final OutputStream outputStream;
    private final Queue queue = new Queue();

    public WriteThread(Socket socket) throws IOException {
        outputStream = socket.getOutputStream();
    }

    public void writeCoordinates(CoordinatesOuterClass.Coordinates coordinates) throws IOException {
        coordinates.writeTo(outputStream);
    }

    public void writeMessage(Message message) {
        queue.put(message);
    }

    public void run() {
        while (true) {
            try {
                queue.take().writeDelimitedTo(outputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
