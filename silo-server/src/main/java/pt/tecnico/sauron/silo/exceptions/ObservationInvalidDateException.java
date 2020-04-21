package pt.tecnico.sauron.silo.exceptions;

public class ObservationInvalidDateException extends RuntimeException {
    public ObservationInvalidDateException(String date) {
        super(String.format(ErrorMessage.OBSERVATION_INVALID_DATE.label,date));
    }
}
