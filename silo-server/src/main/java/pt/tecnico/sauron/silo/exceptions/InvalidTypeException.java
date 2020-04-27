package pt.tecnico.sauron.silo.exceptions;

public class InvalidTypeException extends RuntimeException {

    public InvalidTypeException() {
        super(ErrorMessage.NULL_TYPE.label);
    }

    public InvalidTypeException(String type) {
        super(String.format(ErrorMessage.INVALID_TYPE.label, type));
    }
}
