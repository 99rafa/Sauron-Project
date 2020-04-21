package pt.tecnico.sauron.silo.exceptions;

public class InvalidCoordinatesException extends RuntimeException {

    public InvalidCoordinatesException(){
        super(ErrorMessage.COORDINATES_INVALID.label);
    }

}
