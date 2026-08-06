package player.Connections;

import player.service.messaging.MessagingService;
import proto.coordinates.RegistrationRequestOuterClass.RegistrationRequest;
import proto.messages.MessageOuterClass.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ReadThread extends Thread {
    private final InputStream inputStream;
    private final MessagingService messagingService;

    public ReadThread(Socket socket, MessagingService messagingService) throws IOException {
        this.inputStream = socket.getInputStream();
        this.messagingService = messagingService;
    }

    public RegistrationRequest getRegistrationRequest() throws IOException {
        return RegistrationRequest.parseDelimitedFrom(inputStream);
    }

    public void run() {
        while (true) {
            try {
                messagingService.putMessage(Message.parseDelimitedFrom(inputStream));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
