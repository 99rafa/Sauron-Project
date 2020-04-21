package pt.tecnico.sauron.silo.exceptions;

public class InvalidCoordinatesException extends RuntimeException {

    public InvalidCoordinatesException(){
        super(ErrorMessage.COORDINATES_NULL.label);
    }

    public InvalidCoordinatesException(String cord) {
        super(String.format(ErrorMessage.COORDINATES_INVALID.label,cord));
    }
}
