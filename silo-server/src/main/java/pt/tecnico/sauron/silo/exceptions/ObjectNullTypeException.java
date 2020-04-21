package pt.tecnico.sauron.silo.exceptions;

public class ObjectNullTypeException extends RuntimeException {

    public ObjectNullTypeException(){
        super(ErrorMessage.OBJECT_NULL_TYPE.label);
    }

}
