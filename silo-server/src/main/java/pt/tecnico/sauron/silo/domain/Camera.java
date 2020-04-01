package pt.tecnico.sauron.silo.domain;


import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
import pt.tecnico.sauron.silo.exceptions.SiloException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Camera {

    private String name;
    private double lat;
    private double log;
    private List<Observation> observations = new CopyOnWriteArrayList<>();

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

    public List<Observation> getObservations() {
        return observations;
    }

    public void setObservations(List<Observation> observations) {
        this.observations = observations;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        checkName(name);
        this.name = name;
    }

    public double getLat() {
        return this.lat;
    }

    public void setLat(double lat) {
        checkLatitude(lat);
        this.lat = lat;
    }

    public double getLog() {
        return this.log;
    }

    public void setLog(double log) {
        checkLongitude(log);
        this.log = log;
    }

    public void addObservation(Observation observation) {
        this.observations.add(observation);
        System.out.println("Added observation for object id:" + observation.getId() + " and Type:" + observation.getType() +
               " on " + observation.getDateTime()+ " in camera " + this.name);
    }

    public void sortObservations(){
        this.observations.sort(Observation::compareTo);
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
                ", observations=" + observations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Camera camera = (Camera) o;
        return Double.compare(camera.lat, lat) == 0 &&
                Double.compare(camera.log, log) == 0 &&
                name.equals(camera.name);
    }

}
