package pt.tecnico.sauron.silo.exceptions;

public class SiloException extends RuntimeException{

    private final ErrorMessage errorMessage;

    public SiloException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public SiloException(ErrorMessage errorMessage, String value) {
        this.errorMessage = errorMessage;
    }

    public SiloException(ErrorMessage errorMessage, double value) {
        this.errorMessage = errorMessage;
    }


}
