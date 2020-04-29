package pt.tecnico.sauron.silo.exceptions;

public class DuplicateOperationException extends Exception {

    public DuplicateOperationException() {
        super(ErrorMessage.DUPLICATE_OPERATION.label);
    }
}
