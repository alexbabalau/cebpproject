package server.command.exceptions;

public class NoSuchCommandException extends RuntimeException {
    public NoSuchCommandException(String message){
        super(message);
    }

    public NoSuchCommandException(){

    }
}
