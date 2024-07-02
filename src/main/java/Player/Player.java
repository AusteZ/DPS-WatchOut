package Player;

import Extensions.IntegerExtension;
import Player.Threads.ConnectionThread;
import Beans.PlayerInfo;
import Beans.RegistrationResponse;
import Extensions.ClientResponseExtension;

import com.google.api.Http;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Scanner;

public class Player {
    private static int id;
    private static int listeningPort;
    private static int coordX;
    private static int coordY;
    
    public static int gamePhase;
    public static void main(String[] args) throws IOException, InterruptedException {
        List<PlayerInfo> hi = RestConnection(beginningInterface());
        System.out.println("x: " + coordX + " y: " + coordY);
        (new ConnectionThread(hi, listeningPort)).start();
        //MqttConnection();
        /*Socket s = new Socket("localhost", 9999);*/


    }
    public static int getId(){
        return id;
    }
    public static int getCoordX(){
        return coordX;
    }
    public static int getCoordY(){
        return coordY;
    }
    public static int getListeningPort(){
        return listeningPort;
    }
    
    private static String beginningInterface(){
        Scanner scanner = new Scanner(System.in);
        Integer value = 0;
        while(true) {
            System.out.print("Enter player ID: ");
            value = IntegerExtension.tryParseInt(scanner.nextLine());
            if (value == null || value < 0) {
                System.out.println("Please enter a non-negative number");
                continue;
            }
            break;
        }
        Player.id = value;
        
        while(true) {
            System.out.print("Enter player listening port: ");
            value = IntegerExtension.tryParseInt(scanner.nextLine());
            if (value == null || value < 0) {
                System.out.println("Please enter a non-negative number");
                continue;
            }
            break;
        }
        Player.listeningPort = value;
        
        System.out.print("Enter player server address: ");
        String serverAddress = scanner.nextLine();
        if(serverAddress != "localhost") {
            System.out.println("Due to limitations of the project, address can only be localhost");
        }
        String address = "http://localhost:";
        while(true) {
            System.out.print("Enter server port: ");
            value = IntegerExtension.tryParseInt(scanner.nextLine());
            if (value == null || value < 0) {
                System.out.println("Please enter a non-negative number");
                continue;
            }
            break;
        }
        address += value + "";
        return address;
        
    }
    
    private static List<PlayerInfo> RestConnection(String serverAddress){

        Client client = Client.create();
        ClientResponse clientResponse = null;

        String postPath = "/players/registration";
        PlayerInfo playerInfo = new PlayerInfo(id,listeningPort);
        clientResponse = ClientResponseExtension.postRequest(client,serverAddress+postPath,playerInfo);
        if(clientResponse.getStatus() == 200) {
            RegistrationResponse registrationResponse = clientResponse.getEntity(RegistrationResponse.class);
            System.out.println("HELLO: " + registrationResponse.getCoordinateX() + " " + registrationResponse.getCoordinateY());

            for(PlayerInfo p : registrationResponse.getPlayerList()) {
                System.out.println("Player: " + p.getId());
            }
            coordX = registrationResponse.getCoordinateX();
            coordY = registrationResponse.getCoordinateY();
            return registrationResponse.getPlayerList();
        } else if (clientResponse.getStatus() == 409) {
            System.out.println(clientResponse.getEntity(String.class));
        }
        return null;
        
    }
    
    private static void MqttConnection(){
        MqttClient mqqtClient;
        String broker = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        String topic = "game/flow";
        int qos = 2;

        try {
            mqqtClient = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            //Connect the client
            System.out.println(clientId + " Connecting Broker " + broker);
            mqqtClient.connect(connOpts);
            System.out.println(clientId + " Connected - Thread PID: " + Thread.currentThread().getId());

            //Callback
            mqqtClient.setCallback(new MqttCallback() {

                public void messageArrived(String topic, MqttMessage message) {
                    //Called when a message arrives from the server that matches any subscription made by the client
                    String time = new Timestamp(System.currentTimeMillis()).toString();
                    String receivedMessage = new String(message.getPayload());
                    System.out.println(clientId +" Received a Message! - Callback - Thread PID: " + Thread.currentThread().getId() +
                            "\n\tTime:    " + time +
                            "\n\tTopic:   " + topic +
                            "\n\tMessage: " + receivedMessage +
                            "\n\tQoS:     " + message.getQos() + "\n");

                    System.out.println("\n ***  Press a random key to exit *** \n");

                }

                public void connectionLost(Throwable cause) {
                    System.out.println(clientId + " Connectionlost! cause:" + cause.getMessage()+ "-  Thread PID: " + Thread.currentThread().getId());
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    //Not used here
                }

            });
            System.out.println(clientId + " Subscribing ... - Thread PID: " + Thread.currentThread().getId());
            mqqtClient.subscribe(topic,qos);
            System.out.println(clientId + " Subscribed to topics : " + topic);



        } catch (MqttException me ) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }
    
}
