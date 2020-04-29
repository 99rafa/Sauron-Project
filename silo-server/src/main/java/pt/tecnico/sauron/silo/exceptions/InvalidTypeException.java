package pt.tecnico.sauron.silo.exceptions;

public class InvalidTypeException extends Exception {

    public InvalidTypeException() {
        super(ErrorMessage.NULL_TYPE.label);
    }

    public InvalidTypeException(String type) {
        super(String.format(ErrorMessage.INVALID_TYPE.label, type));
    }
}
