package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
import pt.tecnico.sauron.silo.exceptions.SiloException;
import pt.tecnico.sauron.silo.grpc.Type;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Silo {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private List<Camera> cameras = new CopyOnWriteArrayList<>();


    public Silo() {
    }

    public Silo(List<Camera> cameras) {
        this.cameras = cameras;
    }

    public Observation trackObject(Type type, String id){

        List<Observation> observations = new ArrayList<>();

        if (type == Type.UNKNOWN){
            throw new SiloException(ErrorMessage.OBJECT_INVALID_TYPE);
        }

        if(id == null || id.strip().length() == 0)
            throw new SiloException(ErrorMessage.OBJECT_NULL_ID);

        if(type == null)
            throw new SiloException(ErrorMessage.OBJECT_NULL_TYPE);

        for(Camera c : this.cameras){
            for (Observation o : c.getObservations()) {
                if (o.getType() == type && o.getId().equals(id))
                    observations.add(o);
            }
        }

        if(observations.isEmpty())
            throw new SiloException(ErrorMessage.NO_SUCH_OBJECT);

        observations.sort(Observation::compareTo);
        Collections.reverse(observations);

        return observations.get(0);
    }

    public List<Observation> trackMatchObject(Type type, String  partialId){

        List<Observation> observations = new ArrayList<>();

        if (type == Type.UNKNOWN)
            throw new SiloException(ErrorMessage.OBJECT_INVALID_TYPE);

        if(partialId == null || partialId.strip().length() == 0)
            throw new SiloException(ErrorMessage.OBJECT_NULL_ID);

        if(type == null)
            throw new SiloException(ErrorMessage.OBJECT_NULL_TYPE);

        //If it doesnt have *, it is a simple track
        if(!partialId.contains("*")){
            observations.add(trackObject(type,partialId));
            return observations;
        }


        //If it just *, throw error
        if(partialId.equals("*"))
            throw new SiloException(ErrorMessage.OBJECT_INVALID_PART_ID);

        String[] arr = partialId.split("\\*",2);
        String pre = arr[0];
        String suf = arr[1];


        //More than 1 * throw error
        if(pre.contains("*") || suf.contains("*"))
            throw new SiloException(ErrorMessage.OBJECT_INVALID_PART_ID);
        for(Camera c : this.cameras){
            for(Observation o : c.getObservations()) {

                if (o.getType() == type) {

                    //No prefix ex-> *77
                    if (pre.length() == 0 && o.getId().endsWith(suf)) {
                         assertMostRecentObservation(observations,o);
                    }

                        //No suffix ex-> 77*
                    else if (suf.length() == 0 && o.getId().startsWith(pre)) {
                        assertMostRecentObservation(observations,o);
                    }

                        //Having both prefix and suffix ex-> 22*7
                    else if (o.getId().startsWith(pre) && o.getId().endsWith(suf)) {
                        assertMostRecentObservation(observations,o);
                    }

                }
            }
        }

        if(observations.isEmpty())
            throw new SiloException(ErrorMessage.NO_SUCH_OBJECT);

        observations.sort(Observation::compareTo);

        return observations;
    }

    public void assertMostRecentObservation(List<Observation> observations, Observation obs) {

        int i = 0;
        boolean changed = false;

        for(Observation o : observations) {

            if(o.getId().equals(obs.getId())) {
                changed = true;
                if (o.getDateTime().isBefore(obs.getDateTime())) {
                    observations.set(i, obs);
                }
            }
            i++;
        }
        if (!changed) observations.add(obs);
    }

    public List<Observation> traceObject(Type type, String id){

        List<Observation> res = new ArrayList<>();

        if (type == Type.UNKNOWN)
            throw new SiloException(ErrorMessage.OBJECT_INVALID_TYPE);

        if(id == null || id.strip().length() == 0)
            throw new SiloException(ErrorMessage.OBJECT_NULL_ID);

        if(type == null)
            throw new SiloException(ErrorMessage.OBJECT_NULL_TYPE);

        for(Camera c : this.cameras) {
            for (Observation o : c.getObservations()) {

                if (o.getType() == type && o.getId().equals(id))
                    res.add(o);
            }
        }
        if(res.isEmpty())
            throw new SiloException(ErrorMessage.NO_SUCH_OBJECT);

        res.sort(Observation::compareTo);
        Collections.reverse(res);

        return res;
    }

    public boolean checkIfCameraExists(String camName){
        for(Camera c : this.cameras){
            if(c.getName().equals(camName))
                return true;
        }
        return false;
    }

    public synchronized Camera getCameraByName(String camName){
        if(camName.equals(null))
            throw new SiloException(ErrorMessage.CAMERA_NAME_NULL);
        for(Camera c : this.cameras){
            if(c.getName().equals(camName))
                return c;
        }
        //Throw Exception
        throw new SiloException(ErrorMessage.NO_SUCH_CAMERA_NAME,camName);
    }

    public synchronized void  addCamera(Camera camera) {

        for(Camera c : this.cameras){
            if(c.getName().equals(camera.getName()) && !c.equals(camera))
                throw new SiloException(ErrorMessage.CAMERA_NAME_NOT_UNIQUE);
            if(c.getName().equals(camera.getName()))
                return;
        }
        System.out.println("Camera with name:" + camera.getName() + " and latitude:" + camera.getLat() + " and longitude:"
                + camera.getLog()+ " added to silo");
        this.cameras.add(camera);
    }

    public synchronized List<Camera> getCameras() {
        return this.cameras;
    }

    public synchronized void setCameras(List<Camera> cameras) {
        this.cameras = cameras;
    }


    @Override
    public synchronized String toString() {
        return "Silo{" +
                "cameras=" + this.cameras +
                '}';
    }

}
