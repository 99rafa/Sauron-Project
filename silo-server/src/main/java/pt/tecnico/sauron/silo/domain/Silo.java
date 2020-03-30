package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
import pt.tecnico.sauron.silo.exceptions.SiloException;
import pt.tecnico.sauron.silo.grpc.Type;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class Silo {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private List<Camera> cameras = new ArrayList<>();
    private List<Observation> observations = new ArrayList<>();

    public Silo() {
    }

    public Silo(List<Camera> cameras, List<Observation> observations) {
        this.cameras = cameras;
        this.observations = observations;
    }

    public Observation trackObject(Type type, String id){
        sortObservations();

        for(Observation o : this.observations){
            if(o.getType() == type && o.getId() == id)
                return o;
        }

        throw new SiloException(ErrorMessage.NO_SUCH_OBSERVATION);
    }

    public Observation trackMatchObject(Type type, String  partialId){
        sortObservations();



        throw new SiloException(ErrorMessage.NO_SUCH_OBSERVATION);
    }


    public void addCamera(Camera camera) {
        this.cameras.add(camera);
    }

    public void addObservation(Observation observation) {
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

    private void sortObservations(){
        this.observations.sort(Observation::compareTo);
    }


    @Override
    public String toString() {
        return "Silo{" +
                "_cameras=" + this.cameras +
                ", _observations=" + this.observations +
                '}';
    }

}
