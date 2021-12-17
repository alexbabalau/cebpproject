package exceptions;

public class NegativeBalanceException extends RuntimeException{
    public NegativeBalanceException(){
    }

    public NegativeBalanceException(String message){
        super(message);
    }
}
