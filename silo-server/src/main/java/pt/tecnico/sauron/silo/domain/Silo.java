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


    public Silo() {
    }

    public Silo(List<Camera> cameras) {
        this.cameras = cameras;
    }

    public Observation trackObject(Type type, String id){

        List<Observation> observations = new ArrayList<>();

        if(id == null || id.strip().length() == 0)
            throw new SiloException(ErrorMessage.OBSERVATION_NULL_ID);

        for(Camera c : this.cameras){
            for (Observation o : c.getObservations()) {
                if (o.getType() == type && o.getId() == id)
                    observations.add(o);
            }
        }

        if(observations.isEmpty())
            throw new SiloException(ErrorMessage.NO_SUCH_OBSERVATION);

        observations.sort(Observation::compareTo);

        return observations.get(0);
    }

    public Observation trackMatchObject(Type type, String  partialId){

        List<Observation> observations = new ArrayList<>();

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
        for(Camera c : this.cameras){
            for(Observation o : c.getObservations()) {
                if (o.getType() == type) {

                    //No prefix ex-> *77
                    if (pre.length() == 0 && o.getId().endsWith(suf)) observations.add(o);

                        //No suffix ex-> 77*
                    else if (suf.length() == 0 && o.getId().startsWith(pre)) observations.add(o);

                        //Having both prefix and suffix ex-> 22*7
                    else if (o.getId().startsWith(pre) && o.getId().endsWith(suf)) observations.add(o);

                }
            }
        }

        if(observations.isEmpty())
            throw new SiloException(ErrorMessage.NO_SUCH_OBSERVATION);

        observations.sort(Observation::compareTo);

        return observations.get(0);
    }

    public List<Observation> traceObject(Type type, String id){

        List<Observation> res = new ArrayList<>();

        if(id == null || id.strip().length() == 0)
            throw new SiloException(ErrorMessage.OBSERVATION_NULL_ID);

        for(Camera c : this.cameras) {
            for (Observation o : c.getObservations()) {
                if (o.getType() == type && o.getId() == id)
                    res.add(o);
            }
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

    public Camera getCameraByName(String name){
        for(Camera c : this.cameras){
            if(c.getName() == name)
                return c;
        }
        //Throw Exception
        throw new SiloException(ErrorMessage.NO_SUCH_CAMERA_NAME,name);
    }

    public void addCamera(Camera camera) {
        for(Camera c : this.cameras){
            if(c.getName().equals(camera.getName()) && !c.equals(camera))
                throw new SiloException(ErrorMessage.CAMERA_NAME_NOT_UNIQUE);
        }

        this.cameras.add(camera);
    }

    public List<Camera> getCameras() {
        return this.cameras;
    }

    public void setCameras(List<Camera> cameras) {
        this.cameras = cameras;
    }



    @Override
    public String toString() {
        return "Silo{" +
                "cameras=" + this.cameras +
                '}';
    }

}
