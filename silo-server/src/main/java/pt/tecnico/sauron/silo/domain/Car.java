package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
import pt.tecnico.sauron.silo.exceptions.SiloException;

import static java.lang.Integer.parseInt;

public class Car implements SiloObject {

    private String id;

    public Car() {
    }

    public Car(String id) {
        checkId(id);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        checkId(id);
        this.id = id;
    }

    private void checkId(String id){
        if(id == null)
            throw new SiloException(ErrorMessage.OBSERVATION_NULL_ID);
        if(!isCarId(id))
            throw new SiloException(ErrorMessage.OBSERVATION_INVALID_ID,"CAR");
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

        if (isNumber(g1)) {
            return containsOnlyCapitalLetters(g2) && containsOnlyCapitalLetters(g3);
        }
        if (isNumber(g2)) {
            return containsOnlyCapitalLetters(g1) && containsOnlyCapitalLetters(g3);
        }
        if (isNumber(g3)) {
            return containsOnlyCapitalLetters(g1) && containsOnlyCapitalLetters(g2);
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
        return "Car{" +
                "id='" + id + '\'' +
                '}';
    }
}
