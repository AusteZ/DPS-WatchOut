package Beans;

import administration_server.Generator.CoordinateGenerator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistrationResponse {
    private List<PlayerInfo> playerList;
    private int coordinateX;
    private int coordinateY;
    public RegistrationResponse() {
        playerList = Players.getInstance().getPlayersList();
        int[] coordinates = CoordinateGenerator.generateStartingPosition();
        coordinateX = coordinates[0];
        coordinateY = coordinates[1];
    }
    public List<PlayerInfo> getPlayerList() {
        return playerList;
    }
    public void setPlayerList(List<PlayerInfo> playerList) {
        this.playerList = playerList;
    }
    public int getCoordinateX() {
        return coordinateX;
    }
    public void setCoordinateX(int coordx) {
        this.coordinateX = coordx;
    }
    public int getCoordinateY() {
        return coordinateY;
    }
    public void setCoordinateY(int coordy) {
        this.coordinateY = coordy;
    }
    
}
