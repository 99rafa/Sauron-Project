package pt.tecnico.sauron.silo.exceptions;

public class CameraNameNotUniqueException extends RuntimeException {

    public CameraNameNotUniqueException() {
        super(ErrorMessage.CAMERA_NAME_NOT_UNIQUE.label);
    }
}
