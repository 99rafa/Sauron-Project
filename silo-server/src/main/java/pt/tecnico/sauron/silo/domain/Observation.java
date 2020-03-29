package pt.tecnico.sauron.silo.domain;

import java.time.LocalDateTime;

public class Observation {

    public enum Type{
        PERSON,
        CAR
    }

    private Type _type;
    private int id;
    private LocalDateTime dateTime;

    public Observation() {
    }

    public Observation(Type _type, int id, LocalDateTime dateTime) {
        this._type = _type;
        this.id = id;
        this.dateTime = dateTime;
    }


    public Type get_type() {
        return _type;
    }

    public void set_type(Type _type) {
        this._type = _type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
