package pt.tecnico.sauron.silo.exceptions;

public class CameraNameNullException extends Exception {

    public CameraNameNullException() {
        super(ErrorMessage.CAMERA_NAME_NULL.label);
    }
}
