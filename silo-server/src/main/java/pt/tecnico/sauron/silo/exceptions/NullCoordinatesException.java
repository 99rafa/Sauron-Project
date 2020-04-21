package pt.tecnico.sauron.silo.exceptions;

public class NullCoordinatesException extends RuntimeException {

    public NullCoordinatesException(){
        super(ErrorMessage.COORDINATES_NULL.label);
    }
}
