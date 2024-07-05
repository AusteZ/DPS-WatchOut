package Player.Connections;

import Beans.PlayerInfo;
import Beans.RegistrationResponse;
import Extensions.ClientResponseExtension;
import Player.HRSimulation.HRSendToServerThread;
import Player.Player;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import java.util.List;

public class RestConnection {
    public static List<PlayerInfo> registerPlayerToRest(String serverAddress){

        Client client = Client.create();
        ClientResponse clientResponse = null;

        String postPath = "/players/registration";
        PlayerInfo playerInfo = new PlayerInfo(Player.getId(),Player.getListeningPort());
        clientResponse = ClientResponseExtension.postRequest(client,serverAddress+postPath,playerInfo);
        if(clientResponse.getStatus() == 200) {
            RegistrationResponse registrationResponse = clientResponse.getEntity(RegistrationResponse.class);
            System.out.println("Coordinates: " + registrationResponse.getCoordinateX() + " " + registrationResponse.getCoordinateY());

            Player.setCoordXAndCoordY(registrationResponse.getCoordinateX(),registrationResponse.getCoordinateY());
            HRSendToServerThread.addClient(client, serverAddress);
            return registrationResponse.getPlayerList();
        } else if (clientResponse.getStatus() == 409) {
            System.out.println(clientResponse.getEntity(String.class));
        }
        return null;

    }
}
