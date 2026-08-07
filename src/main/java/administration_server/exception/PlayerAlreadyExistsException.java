package administration_server.exception;

public class PlayerAlreadyExistsException extends Exception{
    public PlayerAlreadyExistsException(String thing){
        super("ERROR: Player with an " + thing + " already exists.");
    }
}
