package pt.tecnico.sauron.silo.exceptions;

public enum ErrorMessage {

    CAMERA_NAME_INVALID("The camera name %s must be between 3 and 15 characters"),
    CAMERA_NAME_NULL("The camera name cannot be null"),

    COORDINATES_INVALID_LATITUDE("The camera latitude %l must be between -90 and 90"),
    COORDINATES_INVALID_LONGITUDE("The camera longitude %l must be between 0 and 180"),
    COORDINATES_NULL_LATITUDE("The camera latitude must not be null"),
    COORDINATES_NULL_LONGITUDE("The camera longitude must not be null"),

    OBSERVATION_NULL_TYPE("The observation type cannot be null"),
    OBSERVATION_INVALID_TYPE("The type %s does not exist"),
    OBSERVATION_INVALID_ID("The id is invalid for the given type %s"),
    OBSERVATION_NULL_ID("The observation Id cannot be null"),
    OBSERVATION_INVALID_DATE("The observation date %s is invalid"),
    OBSERVATION_NULL_DATE("The observation date cannot be null");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}