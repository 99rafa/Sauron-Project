package pt.tecnico.sauron.silo.exceptions;

public enum ErrorMessage {



    CAMERA_NAME_INVALID("The camera name must be between 3 and 15 characters"),
    CAMERA_NAME_NULL("The camera name cannot be null"),
    CAMERA_NAME_NOT_UNIQUE("The camera name must be unique"),
    NO_SUCH_CAMERA_NAME("No such camera name %s"),


    COORDINATES_INVALID_LATITUDE("The camera latitude %f must be between -90 and 90"),
    COORDINATES_INVALID_LONGITUDE("The camera longitude %f must be between 0 and 180"),
    COORDINATES_NULL_LATITUDE("The camera latitude must not be null"),
    COORDINATES_NULL_LONGITUDE("The camera longitude must not be null"),

    OBSERVATION_NULL_TYPE("The observation type cannot be null"),
    OBJECT_NULL_TYPE("The object type cannot be null"),
    NO_SUCH_OBSERVATION("No such observation"),
    NO_SUCH_OBJECT("No such object"),
    OBJECT_INVALID_TYPE("The type %s does not exist"),
    OBSERVATION_INVALID_TYPE("The type %s does not exist"),
    OBJECT_INVALID_ID("The id is invalid for the given type %s"),
    OBSERVATION_INVALID_ID("The id is invalid for the given type %s"),
    OBSERVATION_INVALID_PART_ID("The partial id is invalid"),
    OBJECT_INVALID_PART_ID("The partial id is invalid"),
    OBSERVATION_NULL_ID("The observation Id cannot be null/empty"),
    OBJECT_NULL_ID("The object Id cannot be null/empty"),
    OBSERVATION_INVALID_DATE("The observation date %s is invalid"),
    OBSERVATION_NULL_DATE("The observation date cannot be null");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "label='" + label + '\'' +
                '}';
    }
}