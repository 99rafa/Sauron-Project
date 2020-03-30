package pt.tecnico.sauron.silo.domain;


import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
import pt.tecnico.sauron.silo.exceptions.SiloException;

public class Camera {

    private String name;
    private double lat;
    private double log;

    public Camera() {
    }

    public Camera(String name, double lat, double log) {
        checkName(name);
        this.name = name;
        checkLatitude(lat);
        this.lat = lat;
        checkLongitude(log);
        this.log = log;
    }


    public String get_name() {
        return this.name;
    }

    public void set_name(String name) {
        checkName(name);
        this.name = name;
    }

    public double get_lat() {
        return this.lat;
    }

    public void set_lat(double lat) {
        checkLatitude(lat);
        this.lat = lat;
    }

    public double get_log() {
        return this.log;
    }

    public void set_log(double log) {
        checkLongitude(log);
        this.log = log;
    }

    private void checkName(String name) {
        if (name == null)
            throw new SiloException(ErrorMessage.CAMERA_NAME_NULL);
        if (name.length() < 3 || name.length() > 15)
            throw new SiloException(ErrorMessage.CAMERA_NAME_INVALID, name);
    }

    private void checkLatitude(Double lat) {

        if (lat == null)
            throw new SiloException(ErrorMessage.COORDINATES_NULL_LATITUDE);

        //Latitude must be between -90 and 90
        if (lat < -90 || lat > 90)
            throw new SiloException(ErrorMessage.COORDINATES_INVALID_LATITUDE, lat);

    }

    private void checkLongitude(Double log) {

        if (log == null)
            throw new SiloException(ErrorMessage.COORDINATES_NULL_LONGITUDE);

        //Longitude must be between 0 and 180
        if (log < 0 || log > 180)
            throw new SiloException(ErrorMessage.COORDINATES_INVALID_LONGITUDE, log);

    }

    @Override
    public String toString() {
        return "Camera{" +
                "name='" + name + '\'' +
                ", lat=" + lat +
                ", log=" + log +
                '}';
    }
}
