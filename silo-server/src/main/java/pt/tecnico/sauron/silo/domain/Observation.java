package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
import pt.tecnico.sauron.silo.exceptions.SiloException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Integer.parseInt;

public class Observation {

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private SiloObject type;

    private LocalDateTime dateTime;

    public Observation() {
    }

    public Observation(SiloObject type, LocalDateTime dateTime) {

        checkType(type);
        this.type = type;
        checkDate(dateTime);
        this.dateTime = dateTime;

    }


    public SiloObject get_type() {
        return this.type;
    }

    public void set_type(SiloObject type) {
        this.type = type;
    }

    public LocalDateTime getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }


    private void checkType(SiloObject type) {
        if (type == null)
            throw new SiloException(ErrorMessage.OBSERVATION_NULL_TYPE);
    }

    private void checkDate(LocalDateTime date) {
        if (date == null)
            throw new SiloException(ErrorMessage.OBSERVATION_NULL_DATE);

        //Observation Date is in the future (This Ain't Back to the Future)
        if (date.isAfter(LocalDateTime.now())) {
            throw new SiloException(ErrorMessage.OBSERVATION_INVALID_DATE, date.format(formatter));
        }
    }


    @Override
    public String toString() {
        return "Observation{" +
                "type=" + type +
                ", dateTime=" + dateTime +
                '}';
    }


}
