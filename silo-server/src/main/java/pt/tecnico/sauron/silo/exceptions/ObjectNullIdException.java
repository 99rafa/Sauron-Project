package pt.tecnico.sauron.silo.exceptions;

public class ObjectNullIdException extends RuntimeException {

    public ObjectNullIdException(){
        super(ErrorMessage.OBJECT_NULL_ID.label);
    }

}
