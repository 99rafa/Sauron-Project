package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Silo {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //our replica current value is the state of this list of cameras
    private List<Camera> cameras = new CopyOnWriteArrayList<>();

    private List<Integer> replicaTS = new CopyOnWriteArrayList<>();

    private List<LogRecords> updateLog = new CopyOnWriteArrayList<>();

    private List<Integer> valueTS = new CopyOnWriteArrayList<>();

    private List<Integer> executedOpsTable = new CopyOnWriteArrayList<>();


    public Silo() {
    }

    public Silo(List<Camera> cameras) {
        this.cameras = cameras;
    }


    public Observation trackObject(String type, String id){

        List<Observation> observations = new ArrayList<>();

        //Null or empty string id
        if(id == null || id.strip().length() == 0)
            throw new InvalidIdException();

        //Null type
        if(type == null || type.strip().length() == 0)
            throw new InvalidTypeException();

        //Retrieves observations with given type and id
        for(Camera c : this.cameras){
            for (Observation o : c.getObservations()) {
                if (o.getType().equals(type) && o.getId().equals(id))
                    observations.add(o);
            }
        }

        //No observations matched
        if(observations.isEmpty())
            throw new NoSuchObjectException(id);

        //Order observations
        observations.sort(Observation::compareTo);
        Collections.reverse(observations);

        return observations.get(0);
    }

    public List<Observation> trackMatchObject(String type, String  partialId){

        List<Observation> observations = new ArrayList<>();

        //Null or empty string id
        if(partialId == null || partialId.strip().length() == 0)
            throw new InvalidIdException();

        //Null type
        if(type == null || type.strip().length() == 0)
            throw new InvalidTypeException();

        //If it doesnt have *, it is a simple track
        if(!partialId.contains("*")){
            observations.add(trackObject(type,partialId));
            return observations;
        }


        //If it just *, throw error
        if(partialId.equals("*"))
            throw new InvalidIdException(type);


        //Gets prefix and suffix from partial ID
        String[] arr = partialId.split("\\*",2);
        String pre = arr[0];
        String suf = arr[1];


        //More than 1 * throw error
        if(pre.contains("*") || suf.contains("*"))
            throw new InvalidIdException(type);

        //Retrieves observations with given type and partial id
        for(Camera c : this.cameras){
            for(Observation o : c.getObservations()) {

                if (o.getType().equals(type)) {

                    //No prefix ex-> *77
                    if (pre.length() == 0 && o.getId().endsWith(suf)) {
                         assertMostRecentObservation(observations,o);
                    }

                    //No suffix ex-> 77*
                    else if (suf.length() == 0 && o.getId().startsWith(pre)) {
                        assertMostRecentObservation(observations,o);
                    }

                    //Having both prefix and suffix ex-> 22*7
                    else if (o.getId().startsWith(pre) && o.getId().endsWith(suf) && o.getId().length() >= (suf.length() + pre.length())) {
                        assertMostRecentObservation(observations,o);
                    }

                }
            }
        }

        //No Observations Matched
        if(observations.isEmpty())
            throw new NoSuchObjectException(partialId);

        //Order observations
        observations.sort(Observation::customSort);

        return observations;
    }

    //Sets most recent observation
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

    public List<Observation> traceObject(String type, String id){

        List<Observation> res = new ArrayList<>();

        //Null id or empty string id
        if(id == null || id.strip().length() == 0)
            throw new InvalidIdException();

        //Null type
        if(type == null || type.strip().length() == 0)
            throw new InvalidTypeException();

        //Retrieve observations for the given type nad id
        for(Camera c : this.cameras) {
            for (Observation o : c.getObservations())
                if (o.getType().equals(type) && o.getId().equals(id))
                    res.add(o);
        }

        //No matched objects
        if(res.isEmpty())
            throw new NoSuchObjectException(id);

        //Order observations
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

        //Camera name null
        if(camName.equals(null))
            throw new CameraNameNullException();

        //Find camera for the given name
        for(Camera c : this.cameras){
            if(c.getName().equals(camName))
                return c;
        }

        //No camera found
        throw new NoSuchCameraNameException(camName);
    }

    public synchronized void  addCamera(Camera camera) {

        //Checks for duplicate cameras
        for(Camera c : this.cameras){
            //Same name different coordinates
            if(c.getName().equals(camera.getName()) && !c.equals(camera))
                throw new CameraNameNotUniqueException();
            //Same name same coordinates
            if(c.getName().equals(camera.getName()))
                return;
        }

        //Just a system message
        System.out.println("Camera with name:" + camera.getName() + " and latitude:" + camera.getLat() + " and longitude:"
                + camera.getLog()+ " added to silo");

        //Add camera to lot of cameras
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
