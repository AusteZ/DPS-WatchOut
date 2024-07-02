package Administration_Client;

import Beans.PlayerInfo;
import Beans.Players;
import Extensions.ClientResponseExtension;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class RestConnection{
    private static String path;
    private static Client client;
    public static void setConnection(String host, int port, String getPath) {
        RestConnection.path = "http://" + host + ":" + port + getPath;
        client = Client.create();
    }
    public static void getPlayers(){
        
        ClientResponse clientResponse = ClientResponseExtension.getRequest(client,path);
        System.out.println(clientResponse.toString());
        Players players = clientResponse.getEntity(Players.class);
        for (PlayerInfo p : players.getPlayersList()) {
            System.out.println("id: " + p.getId());
        }
    }
}
