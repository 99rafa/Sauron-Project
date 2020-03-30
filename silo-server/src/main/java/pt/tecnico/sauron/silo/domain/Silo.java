package pt.tecnico.sauron.silo.domain;

import java.util.ArrayList;
import java.util.List;

public class Silo {

    private List<Camera> cameras = new ArrayList<>();
    private List<Observation> observations = new ArrayList<>();

    public Silo() {
    }

    public Silo(List<Camera> _cameras, List<Observation> _observations) {
        this.cameras = _cameras;
        this.observations = _observations;
    }


    public void addCamera(Camera camera){
        this.cameras.add(camera);
    }

    public void addObservation(Observation observation){
        this.observations.add(observation);
    }

    public List<Camera> get_cameras() {
        return this.cameras;
    }

    public void set_cameras(List<Camera> cameras) {
        this.cameras = cameras;
    }

    public List<Observation> get_observations() {
        return this.observations;
    }

    public void set_observations(List<Observation> observations) {
        this.observations = observations;
    }

    @Override
    public String toString() {
        return "Silo{" +
                "_cameras=" + this.cameras +
                ", _observations=" + this.observations +
                '}';
    }
}
