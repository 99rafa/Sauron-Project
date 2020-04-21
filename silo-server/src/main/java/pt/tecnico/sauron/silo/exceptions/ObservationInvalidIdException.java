package pt.tecnico.sauron.silo.exceptions;

public class ObservationInvalidIdException extends RuntimeException {
    public ObservationInvalidIdException(String type) {
        super(String.format(ErrorMessage.OBJECT_INVALID_ID.label,type));
    }
}
