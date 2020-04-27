package pt.tecnico.sauron.silo.exceptions;

public class DuplicateOperationException extends RuntimeException {

    public DuplicateOperationException() {
        super(ErrorMessage.DUPLICATE_OPERATION.label);
    }
}
