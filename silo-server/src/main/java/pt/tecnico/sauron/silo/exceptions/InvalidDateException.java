package pt.tecnico.sauron.silo.exceptions;

public class InvalidDateException extends Exception {

    public InvalidDateException() {
        super(ErrorMessage.NULL_DATE.label);
    }

    public InvalidDateException(String date) {
        super(String.format(ErrorMessage.INVALID_DATE.label, date));
    }
}
