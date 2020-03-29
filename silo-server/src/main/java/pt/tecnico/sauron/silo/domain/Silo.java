package pt.tecnico.sauron.silo.domain;

import java.util.ArrayList;
import java.util.List;

public class Silo {

    private List<Camera> _cameras = new ArrayList<>();
    private List<Observation> _observations = new ArrayList<>();

    public Silo() {
    }

    public Silo(List<Camera> _cameras, List<Observation> _observations) {
        this._cameras = _cameras;
        this._observations = _observations;
    }


    public void addCamera(Camera camera){
        this._cameras.add(camera);
    }

    public void addObservation(Observation observation){
        this._observations.add(observation);
    }

    public List<Camera> get_cameras() {
        return _cameras;
    }

    public void set_cameras(List<Camera> _cameras) {
        this._cameras = _cameras;
    }

    public List<Observation> get_observations() {
        return _observations;
    }

    public void set_observations(List<Observation> _observations) {
        this._observations = _observations;
    }

    @Override
    public String toString() {
        return "Silo{" +
                "_cameras=" + _cameras +
                ", _observations=" + _observations +
                '}';
    }
}
