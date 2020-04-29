package pt.tecnico.sauron.silo.exceptions;

import pt.tecnico.sauron.silo.SiloServerApp;

public class CameraNameNullException extends SiloServerApp {

    public CameraNameNullException() {
        super(ErrorMessage.CAMERA_NAME_NULL.label);
    }
}
