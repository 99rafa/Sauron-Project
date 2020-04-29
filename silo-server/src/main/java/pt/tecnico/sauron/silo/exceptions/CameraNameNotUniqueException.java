package pt.tecnico.sauron.silo.exceptions;

public class CameraNameNotUniqueException extends Exception {

    public CameraNameNotUniqueException() {
        super(ErrorMessage.CAMERA_NAME_NOT_UNIQUE.label);
    }
}
