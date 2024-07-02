package Exceptions;

public class PlayerAlreadyExistsException extends Exception{
    public PlayerAlreadyExistsException(int playerId){
        super("ERROR: Player with an id " + playerId + " already exists.");
    }
}
