package server.command.exceptions;

public class NotEnoughUnitsException extends RuntimeException{
    public NotEnoughUnitsException(){

    }

    public NotEnoughUnitsException(String message){
        super(message);
    }
}
