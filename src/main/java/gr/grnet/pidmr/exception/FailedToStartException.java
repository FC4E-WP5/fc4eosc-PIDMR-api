package gr.grnet.pidmr.exception;

public class FailedToStartException extends RuntimeException{

    public FailedToStartException(String message){
        super(message);
    }
}
