package Beans;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class PlayerInfo {
    private int id;
    private int listeningPort;
    private String ipAddress;
    
    public PlayerInfo() {
        this.id = -1;
        this.listeningPort = -1;
        this.ipAddress = "";
    }
    public PlayerInfo(int id, int listeningPort) {
        this.id = id;
        this.listeningPort = listeningPort;
        this.ipAddress = "localhost";
    }
    public int getId(){
        return id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public String getIpAddress(){
        return ipAddress;
    }
    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }
    
    public int getListeningPort(){
        return listeningPort;
    }
    
    public void setListeningPort(int listeningPort){
        this.listeningPort = listeningPort;
    }
    
    public int[] generateCoordinates(){
        return new int[]{getListeningPort(), getId()};
    }
}
