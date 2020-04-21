package pt.tecnico.sauron.silo.exceptions;

public class ObservationNullTypeException extends RuntimeException {
    public ObservationNullTypeException() {
        super(ErrorMessage.OBSERVATION_NULL_TYPE.label);
    }
}
