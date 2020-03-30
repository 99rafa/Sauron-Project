package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
import pt.tecnico.sauron.silo.exceptions.SiloException;
import pt.tecnico.sauron.silo.grpc.Type;

import java.io.CharArrayReader;
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

        if(id == null || id.strip().length() == 0)
            throw new SiloException(ErrorMessage.OBSERVATION_NULL_ID);

        sortObservations();

        for(Observation o : this.observations){
            if(o.getType() == type && o.getId() == id)
                return o;
        }

        throw new SiloException(ErrorMessage.NO_SUCH_OBSERVATION);
    }

    public Observation trackMatchObject(Type type, String  partialId){
        sortObservations();

        if(partialId == null || partialId.strip().length() == 0)
            throw new SiloException(ErrorMessage.OBSERVATION_NULL_ID);

        //If it doesnt have *, it is a simple track
        if(!partialId.contains("*"))
            return trackObject(type,partialId);

        //If it just *, throw error
        if(partialId.equals("*"))
            throw new SiloException(ErrorMessage.OBSERVATION_INVALID_PART_ID);

        String[] arr = partialId.split("\\*",2);
        String pre = arr[0];
        String suf = arr[1];


        //More than 1 * throw error
        if(pre.contains("*") || suf.contains("*"))
            throw new SiloException(ErrorMessage.OBSERVATION_INVALID_PART_ID);

        for(Observation o : this.observations){
            if(o.getType() == type){

                //No prefix ex-> *77
                if(pre.length() == 0 && o.getId().endsWith(suf)) return o;

                //No suffix ex-> 77*
                else if(suf.length() == 0 && o.getId().startsWith(pre)) return o;

                //Having both prefix and suffix ex-> 22*7
                else if (o.getId().startsWith(pre) && o.getId().endsWith(suf)) return o;

            }

        }


        throw new SiloException(ErrorMessage.NO_SUCH_OBSERVATION);
    }

    public List<Observation> traceObject(Type type, String id){

        List<Observation> res = new ArrayList<>();

        if(id == null || id.strip().length() == 0)
            throw new SiloException(ErrorMessage.OBSERVATION_NULL_ID);


        for(Observation o : this.observations){
            if(o.getType() == type && o.getId() == id)
                res.add(o);
        }

        if(res.isEmpty())
            throw new SiloException(ErrorMessage.NO_SUCH_OBSERVATION);
        res.sort(Observation::compareTo);

        return res;
    }

    public boolean checkIfCameraExists(String name){
        for(Camera c: this.cameras){
            if(c.getName() == name)
                return true;
        }
        return false;
    }


    public void addCamera(Camera camera) {
        this.cameras.add(camera);
    }

    public void addObservation(Observation observation) {
        this.observations.add(observation);
    }

    public List<Camera> getCameras() {
        return this.cameras;
    }

    public void setCameras(List<Camera> cameras) {
        this.cameras = cameras;
    }

    public List<Observation> getObservations() {
        return this.observations;
    }

    public void setObservations(List<Observation> observations) {
        this.observations = observations;
    }

    private void sortObservations(){
        this.observations.sort(Observation::compareTo);
    }


    @Override
    public String toString() {
        return "Silo{" +
                "cameras=" + this.cameras +
                ", observations=" + this.observations +
                '}';
    }

}
