package Player.HRSimulation;

import Beans.MeasurementList;
import Beans.MeasurementValue;
import Extensions.ClientResponseExtension;
import Player.Player;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import java.util.List;

public class HRSendToServerThread extends Thread{
    private static Client client;
    private static String serverAddress;
    private static String postPath = "/analytics/postmeasurements";
    
    public static void addClient(Client client, String serverAddress){
        HRSendToServerThread.client = client;
        HRSendToServerThread.serverAddress = serverAddress;
    }
    public void run(){
        ClientResponse clientResponse = null;
        
        
        while(true) {
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<MeasurementValue> list = HRCollectValues.getLastMeasurements();
            if(list.isEmpty())
                continue;
            
            clientResponse = ClientResponseExtension.postRequest(client,serverAddress+postPath,new MeasurementList(Player.getId(),System.currentTimeMillis(),list));
            if(clientResponse.getStatus() != 200) {
                System.out.println(clientResponse.getStatus() + " " + clientResponse.getEntity(String.class));
            }
        }
    }
}
