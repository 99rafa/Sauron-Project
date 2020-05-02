package pt.tecnico.sauron.silo.exceptions;

public class CameraNameInvalidException extends Exception {

    public CameraNameInvalidException() {
        super(ErrorMessage.CAMERA_NAME_INVALID.label);
    }

}
