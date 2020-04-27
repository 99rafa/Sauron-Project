package pt.tecnico.sauron.silo.exceptions;

public class CameraNameInvalidException extends RuntimeException {

    public CameraNameInvalidException() {
        super(ErrorMessage.CAMERA_NAME_INVALID.label);
    }

}
