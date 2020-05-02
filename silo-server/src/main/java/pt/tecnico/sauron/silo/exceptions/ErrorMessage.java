package pt.tecnico.sauron.silo.exceptions;

public enum ErrorMessage {


    CAMERA_NAME_INVALID("The camera name must be between 3 and 15 characters"),
    CAMERA_NAME_NULL("The camera name cannot be null"),
    CAMERA_NAME_NOT_UNIQUE("The camera name must be unique"),
    NO_SUCH_CAMERA_NAME("No such camera name %s"),


    COORDINATES_INVALID("The camera %s is invalid"),
    COORDINATES_NULL("The camera coordinates must not be null"),

    NULL_TYPE("The type cannot be null/empty"),
    INVALID_TYPE("The type %s does not exist"),
    NULL_ID("The Id cannot be null/empty"),
    INVALID_ID("The id is invalid for the given type %s"),
    NULL_DATE("The date cannot be null"),
    INVALID_DATE("The date %s is invalid"),

    NO_SUCH_OBJECT("The object with id %s does not exist"),

    DUPLICATE_OPERATION("Duplicate request sent");

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