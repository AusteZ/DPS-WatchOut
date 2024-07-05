package Administration_Client;

import Beans.*;
import Extensions.ClientResponseExtension;

import Player.HRSimulation.HRSendToServerThread;
import Player.Player;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import io.opencensus.common.ServerStatsEncoding;

public class RestConnection{
    private static String path;
    private static Client client;
    public static void setConnection(String host, int port) {
        RestConnection.path = "http://" + host + ":" + port;
        client = Client.create();
    }
    public static void getPlayers(){
        
        ClientResponse clientResponse = ClientResponseExtension.getRequest(client,path+"/players/list");
        System.out.println(clientResponse.toString());
        Players players = clientResponse.getEntity(Players.class);
        for (PlayerInfo p : players.getPlayersList()) {
            System.out.println("id: " + p.getId());
        }
    }
    public static void getMeasurementsBetweenTimestamps(Long startTimestamp, Long endTimestamp){
        
        PlayerInfo playerInfo = new PlayerInfo(Player.getId(),Player.getListeningPort());
        Timestamps timestamps = new Timestamps(startTimestamp,endTimestamp);
        ClientResponse clientResponse = ClientResponseExtension.postRequest(client,path+"/analytics/getvaluesbetweentimestamps", timestamps);
        if(clientResponse.getStatus() == 200) {
            Double average = clientResponse.getEntity(Average.class).getAverage();
            System.out.println("The heart rate average between " + startTimestamp + " and " + endTimestamp + "  " + average);
        } else if (clientResponse.getStatus() == 409) {
            System.out.println(clientResponse.getEntity(String.class));
        }
    }
    public static void getLastNMeasurements(Integer id, Integer n){
        ClientResponse clientResponse = ClientResponseExtension.getRequest(client,path+"/analytics/getlastnmeasurements/" + id + "/" + n);
        System.out.println(clientResponse.toString());
        Double average = clientResponse.getEntity(Average.class).getAverage();
        System.out.println("The " + n + " last heart rate average of " + id + " is " + average);
    }
}
