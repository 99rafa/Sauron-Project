package pt.tecnico.sauron.silo.exceptions;

public class ObjectInvalidIdException extends RuntimeException {

    public ObjectInvalidIdException(String type) {
        super(String.format(ErrorMessage.OBJECT_INVALID_ID.label,type));
    }
}
