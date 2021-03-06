package pt.tecnico.sauron.silo.exceptions;

public class NoSuchCameraNameException extends Exception {
    public NoSuchCameraNameException(String name) {
        super(String.format(ErrorMessage.NO_SUCH_CAMERA_NAME.label, name));
    }
}
