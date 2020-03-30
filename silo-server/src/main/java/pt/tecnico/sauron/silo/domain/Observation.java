package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
import pt.tecnico.sauron.silo.exceptions.SiloException;
import pt.tecnico.sauron.silo.grpc.Type;

import java.time.LocalDateTime;

import static java.lang.Integer.parseInt;

public class Observation implements Comparable<Observation> {

    private Type type;

    private LocalDateTime dateTime;

    private String id;

    public Observation() {
    }

    public Observation(Type type, String id, LocalDateTime dateTime) {

        checkType(type);
        this.type = type;
        checkDate(dateTime);
        this.dateTime = dateTime;
        checkId(id);
        this.id = id;

    }

    public LocalDateTime getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private void checkType(Type type) {
        if (type == null)
            throw new SiloException(ErrorMessage.OBSERVATION_NULL_TYPE);

    }

    private void checkDate(LocalDateTime date) {
        if (date == null)
            throw new SiloException(ErrorMessage.OBSERVATION_NULL_DATE);

        //Observation Date is in the future (This Ain't Back to the Future)
        if (date.isAfter(LocalDateTime.now())) {
            throw new SiloException(ErrorMessage.OBSERVATION_INVALID_DATE, date.format(Silo.formatter));
        }
    }

    private void checkId(String id) {
        if (id == null || id.strip().length() == 0)
            throw new SiloException(ErrorMessage.OBSERVATION_NULL_ID);
        if (this.type == Type.PERSON) {
            if (!isNumber(id))
                throw new SiloException(ErrorMessage.OBSERVATION_INVALID_ID, type.toString());
        }
        if (this.type == Type.CAR) {
            if (!isCarId(id))
                throw new SiloException(ErrorMessage.OBSERVATION_INVALID_ID, type.toString());
        }
    }


    private boolean isCarId(String id) {
        String g1;
        String g2;
        String g3;

        if (id.length() != 6)
            return false;

        g1 = id.substring(0, 2);
        g2 = id.substring(2, 4);
        g3 = id.substring(4, 6);


        if (containsOnlyCapitalLetters(g1)) {
            return isNumber(g2) && isNumber(g3);
        }
        if (containsOnlyCapitalLetters(g2)) {
            return isNumber(g1) && isNumber(g3);
        }
        if (containsOnlyCapitalLetters(g3)) {
            return isNumber(g1) && isNumber(g2);
        }
        return false;
    }

    private boolean isNumber(String s) {
        try {
            parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


    private boolean containsOnlyCapitalLetters(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) < 'A' || s.charAt(i) > 'Z')
                return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "Observation{" +
                "type=" + type +
                ", dateTime=" + dateTime +
                '}';
    }


    @Override
    public int compareTo(Observation observation) {
        return this.dateTime.compareTo(observation.getDateTime());
    }
}
