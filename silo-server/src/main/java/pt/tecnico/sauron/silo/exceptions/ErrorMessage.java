package pt.tecnico.sauron.silo.exceptions;

public enum ErrorMessage {

    CAMERA_NAME_INVALID("The camera name %s must be between 3 and 15 characters"),
    CAMERA_NAME_NULL("The camera name cannot be null"),


    COORDINATES_INVALID_LATITUDE("The camera latitude %l must be between -90 and 90"),
    COORDINATES_INVALID_LONGITUDE("The camera longitude %l must be between 0 and 180"),
    COORDINATES_NULL_LATITUDE("The camera latitude must not be null"),
    COORDINATES_NULL_LONGITUDE("The camera longitude must not be null");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}