package Exceptions;

public class UnitializedPlayerException extends Exception{
    public UnitializedPlayerException(){
        super("ERROR: There is no (or invalid) listening port and id provided. Ids and Listening ports have to be a whole natural number.");
    }
}
