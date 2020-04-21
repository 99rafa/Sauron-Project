package pt.tecnico.sauron.silo.exceptions;

public class ObservationInvalidTypeException extends RuntimeException {
    public ObservationInvalidTypeException(String type) {
        super(String.format(ErrorMessage.OBSERVATION_INVALID_TYPE.label,type));
    }
}
