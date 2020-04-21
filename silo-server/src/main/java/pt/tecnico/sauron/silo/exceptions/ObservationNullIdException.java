package pt.tecnico.sauron.silo.exceptions;

public class ObservationNullIdException extends RuntimeException {
    public ObservationNullIdException() {
        super(ErrorMessage.OBSERVATION_NULL_ID.label);
    }
}
