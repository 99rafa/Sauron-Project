package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
import pt.tecnico.sauron.silo.exceptions.SiloException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Integer.parseInt;

public class Observation {

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public enum ObjectType{
        PERSON,
        CAR
    }

    private ObjectType type;
    private String id;
    private LocalDateTime dateTime;

    public Observation() {
    }

    public Observation(ObjectType type, String id, LocalDateTime dateTime) {

        checkType(type);
        this.type = type;
        checkId(id);
        this.id = id;
        checkDate(dateTime);
        this.dateTime = dateTime;

    }


    public ObjectType get_type() {
        return this.type;
    }

    public void set_type(ObjectType type) {
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }


    private void checkType(ObjectType type){
        if(type == null)
            throw new SiloException(ErrorMessage.OBSERVATION_NULL_TYPE);
    }

    private void checkId(String id){
        if (id == null)
            throw new SiloException(ErrorMessage.OBSERVATION_NULL_ID);

        if (this.type == ObjectType.PERSON) {
            //person Id must be an integer
            if(!isNumber(id))
                // (OPTIONAL) This is here just to throw a more specific exception
                throw new SiloException(ErrorMessage.OBSERVATION_INVALID_ID, this.type.toString());
        }
        if (this.type == ObjectType.CAR) {
            if(!checkCarId(id))
                throw new SiloException(ErrorMessage.OBSERVATION_INVALID_ID,this.type.toString());
        }
    }
    private void checkDate(LocalDateTime date){
        if(date == null)
            throw new SiloException(ErrorMessage.OBSERVATION_NULL_DATE);

        //Observation Date is in the future (This Ain't Back to the Future)
        if(date.isAfter(LocalDateTime.now())){
            throw new SiloException(ErrorMessage.OBSERVATION_INVALID_DATE,date.format(formatter));
        }
    }


    private boolean checkCarId(String id){
        String g1 = "";
        String g2 = "";
        String g3 = "";

        if(id.length() != 6)
            return false;

        g1 = this.id.substring(0,1);
        g2 = this.id.substring(2,3);
        g3 = this.id.substring(4,5);

        if(isNumber(g1)){
            if(!containsOnlyCapitalLetters(g2) || !containsOnlyCapitalLetters(g3))
                return false;
        }
        else if(isNumber(g2)){
            if(!containsOnlyCapitalLetters(g1) || !containsOnlyCapitalLetters(g3))
                return false;
        }
        else if(isNumber(g3)){
            if(!containsOnlyCapitalLetters(g1) || !containsOnlyCapitalLetters(g2))
                return false;
        }
        else{
            return false;
        }
        return true;
    }

    private boolean isNumber(String s){
        try{
            parseInt(s);
        }
        catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    private boolean containsOnlyCapitalLetters(String s){
        for(int i = 0; i < s.length();i++){
            if(s.charAt(i) < 'A' || s.charAt(i) > 'Z')
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Observation{" +
                "type=" + type +
                ", id='" + id + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }


}
