package pt.tecnico.sauron.silo.domain;


import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
import pt.tecnico.sauron.silo.exceptions.SiloException;

public class Camera{

    private String _name;
    private double _lat;
    private double _log;

    public Camera() {
    }

    public Camera(String name, double lat, double log){
        checkName(name);
        checkLatitude(lat);
        checkLongitude(log);
        this._name = name;
        this._lat = lat;
        this._log = log;
    }




    public String get_name() {
        return _name;
    }

    public void set_name(String name) {
        this._name = name;
    }

    public double get_lat() {
        return _lat;
    }

    public void set_lat(double lat) {
        this._lat = lat;
    }

    public double get_log() {
        return _log;
    }

    public void set_log(double log) {
        this._log = log;
    }

    private void checkName(String name){
        if(_name == null)
            throw new SiloException(ErrorMessage.CAMERA_NAME_NULL);
        if(_name.length() <3 || _name.length() > 15)
            throw new SiloException(ErrorMessage.CAMERA_NAME_INVALID,name);
    }

    private void checkLatitude(Double lat){

        if(lat == null)
            throw new SiloException(ErrorMessage.COORDINATES_NULL_LATITUDE);

        if(lat < -90 || lat > 90)
            throw new SiloException(ErrorMessage.COORDINATES_INVALID_LATITUDE,lat);

    }

    private void checkLongitude(Double log){

        if(log == null)
            throw new SiloException(ErrorMessage.COORDINATES_NULL_LONGITUDE);

        if(log < 0 || log > 180)
            throw new SiloException(ErrorMessage.COORDINATES_INVALID_LONGITUDE,log);

    }
}
