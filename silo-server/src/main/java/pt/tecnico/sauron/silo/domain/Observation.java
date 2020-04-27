package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.InvalidDateException;
import pt.tecnico.sauron.silo.exceptions.InvalidIdException;

import java.time.LocalDateTime;

public class Observation implements Comparable<Observation> {

    private String type;

    private LocalDateTime dateTime;

    private String id;

    private String camName;

    public Observation() {
    }

    public Observation(String type, String id, LocalDateTime dateTime, String camName) {

        this.type = type;
        //Checks if valid date
        checkDate(dateTime);
        this.dateTime = dateTime;
        //Checks if valid Id
        checkId(id);
        this.id = id;
        this.camName = camName;

    }

    public synchronized String getCamName() {
        return camName;
    }

    public synchronized void setCamName(String camName) {
        this.camName = camName;
    }

    public synchronized LocalDateTime getDateTime() {
        return this.dateTime;
    }

    public synchronized void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public synchronized String getId() {
        return id;
    }

    public synchronized void setId(String id) {
        this.id = id;
    }

    private void checkDate(LocalDateTime date) {
        //Null date
        if (date == null)
            throw new InvalidDateException();

        //Observation Date is in the future (This Ain't Back to the Future)
        if (date.isAfter(LocalDateTime.now())) {
            throw new InvalidDateException(date.format(Silo.formatter));
        }
    }

    private void checkId(String id) {
        //Null or empty ID
        if (id == null || id.strip().length() == 0)
            throw new InvalidIdException();

        //Checks person id
        if (this.type.equals("PERSON")) {
            if (!isNumber(id))
                throw new InvalidIdException(this.type);
        }
        //Checks car Id
        if (this.type.equals("CAR")) {
            if (!isCarId(id))
                throw new InvalidIdException(this.type);
        }
    }

    //Checks if it is a valid car id
    private boolean isCarId(String id) {
        String g1;
        String g2;
        String g3;

        //Car plate length is 6
        if (id.length() != 6)
            return false;

        //Brakes full id in 3 sub groups
        g1 = id.substring(0, 2);
        g2 = id.substring(2, 4);
        g3 = id.substring(4, 6);


        //Checks if it matches the portuguese plate format
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

    //Checks if string is a number
    private boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


    //Checks if string only contains capital letters
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

    public int customSort(Observation observation) {
        return this.getId().compareTo(observation.getId());
    }
}
