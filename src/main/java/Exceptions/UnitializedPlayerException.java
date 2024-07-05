package Exceptions;

import proto.messages.MessageOuterClass;

public class UnitializedPlayerException extends Exception{
    public UnitializedPlayerException(String message){
        super(message);
    }
}
