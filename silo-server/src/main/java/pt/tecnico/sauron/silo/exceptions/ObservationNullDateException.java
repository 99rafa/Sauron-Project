package pt.tecnico.sauron.silo.exceptions;

public class ObservationNullDateException extends RuntimeException {
    public ObservationNullDateException() {
        super(ErrorMessage.OBSERVATION_NULL_DATE.label);
    }
}
