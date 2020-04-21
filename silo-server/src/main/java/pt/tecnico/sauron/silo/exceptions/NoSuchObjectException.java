package pt.tecnico.sauron.silo.exceptions;

public class NoSuchObjectException extends RuntimeException {
    public NoSuchObjectException() {
        super(ErrorMessage.NO_SUCH_OBJECT.label);
    }

    public NoSuchObjectException(String id) {
        super(String.format(ErrorMessage.NO_SUCH_OBJECT.label, id));
    }
}
