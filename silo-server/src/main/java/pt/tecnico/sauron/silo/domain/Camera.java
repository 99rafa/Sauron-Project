package pt.tecnico.sauron.silo.domain;


import pt.tecnico.sauron.silo.exceptions.CameraNameInvalidException;
import pt.tecnico.sauron.silo.exceptions.CameraNameNullException;
import pt.tecnico.sauron.silo.exceptions.InvalidCoordinatesException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Camera {

    private String name;
    private double lat;
    private double log;
    private List<Observation> observations = new CopyOnWriteArrayList<>();

    public Camera() {
    }

    public Camera(String name, double lat, double log) throws CameraNameInvalidException, CameraNameNullException, InvalidCoordinatesException {
        //Checks if valid name
        checkName(name);
        this.name = name;
        //checks if valid latitude
        checkLatitude(lat);
        this.lat = lat;
        //checks if valid longitude
        checkLongitude(log);
        this.log = log;
    }

    public synchronized List<Observation> getObservations() {
        return observations;
    }

    public synchronized void setObservations(List<Observation> observations) {
        this.observations = observations;
    }

    public synchronized String getName() {
        return this.name;
    }

    public synchronized void setName(String name) throws CameraNameInvalidException, CameraNameNullException {
        //Checks if valid name
        checkName(name);
        this.name = name;
    }

    public synchronized double getLat() {
        return this.lat;
    }

    public synchronized void setLat(double lat) throws InvalidCoordinatesException {
        //Checks if valid latitude
        checkLatitude(lat);
        this.lat = lat;
    }

    public synchronized double getLog() {
        return this.log;
    }

    public synchronized void setLog(double log) throws InvalidCoordinatesException {
        //checks if valid longitude
        checkLongitude(log);
        this.log = log;
    }

    public synchronized void addObservation(Observation observation) {
        this.observations.add(observation);
        //Just a system message
        System.out.println("Added observation for object id:" + observation.getId() + " and Type:" + observation.getType() +
                " on " + observation.getDateTime() + " in camera " + this.name);
    }

    public void sortObservations() {
        this.observations.sort(Observation::compareTo);
    }

    private void checkName(String name) throws CameraNameNullException, CameraNameInvalidException {
        //Name cant be null
        if (name == null)
            throw new CameraNameNullException();
        //Name length must be between 3 and 15
        if (name.length() < 3 || name.length() > 15)
            throw new CameraNameInvalidException();
    }

    private void checkLatitude(Double lat) throws InvalidCoordinatesException {
        //Latitude must not be null
        if (lat == null)
            throw new InvalidCoordinatesException();

        //Latitude must be between -90 and 90
        if (lat < -90 || lat > 90)
            throw new InvalidCoordinatesException("latitude");

    }

    private void checkLongitude(Double log) throws InvalidCoordinatesException {
        //Longitude must not be null
        if (log == null)
            throw new InvalidCoordinatesException();

        //Longitude must be between 0 and 180
        if (log < 0 || log > 180)
            throw new InvalidCoordinatesException("longitude");

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
