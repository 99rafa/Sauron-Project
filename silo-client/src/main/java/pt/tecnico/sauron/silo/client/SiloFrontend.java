package pt.tecnico.sauron.silo.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.tecnico.sauron.silo.client.Exceptions.NoServersAvailableException;
import pt.tecnico.sauron.silo.client.requests.*;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.util.*;


public class SiloFrontend implements AutoCloseable {

    private String currentPath;
    private List<String> attempts = new ArrayList<>();
    private Integer previousAvailableServers = -1;
    private ResponseCache responseCache = new ResponseCache();
    private Request previousRequest;
    private ManagedChannel channel;
    private String host;
    private String port;
    private Map<Integer, Integer> prevTS = new HashMap<>();
    private String target;
    private String repN;
    private boolean isStatic = false;

    private SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub;

    public SiloFrontend(String zooHost, String zooPort, String repN) throws ZKNamingException, NoServersAvailableException {

        this.host = zooHost;
        this.port = zooPort;
        this.target = getServerTarget(zooHost, zooPort, repN);

        if (!repN.equals("")) this.isStatic = true;

        this.channel = ManagedChannelBuilder.forTarget(this.target).usePlaintext().build();

        // Create a blocking stub.
        this.stub = SiloOperationsServiceGrpc.newBlockingStub(channel);

        try {
            ctrlPing();//PING
            System.out.println("Connected to replica " + this.repN + " at " + this.target);
        } catch (StatusRuntimeException e) {
            renewConnection();
        }
    }

    public SiloFrontend(String zooHost, String zooPort, String repN, Map<Integer, Integer> preTS) throws ZKNamingException, NoServersAvailableException {

        this.host = zooHost;
        this.port = zooPort;
        this.prevTS = preTS;
        this.target = getServerTarget(zooHost, zooPort, repN);

        if (repN.equals("")) this.isStatic = true;

        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        // Create a blocking stub.
        this.stub = SiloOperationsServiceGrpc.newBlockingStub(channel);

        try {
            ctrlPing();//PING
            System.out.println("Connected to replica " + this.repN + " at " + this.target);

        } catch (StatusRuntimeException e) {
            renewConnection();
        }

    }

    //when a replica crashes, frontend reconnects to a random other replica
    public void renewConnection() throws ZKNamingException, NoServersAvailableException {
        System.err.println("Replica " + getRepN() + " at " + getTarget() + " is down");
        System.out.println("Trying to reconnect to another replica");

        while (true) {
            try {
                this.channel.shutdownNow();

                this.target = getServerTarget(this.host, this.port, "");


                this.channel = ManagedChannelBuilder.forTarget(this.target).usePlaintext().build();

                // Create a blocking stub.
                this.stub = SiloOperationsServiceGrpc.newBlockingStub(channel);

                ctrlPing();//PING
                break;
            } catch (RuntimeException e) {
                System.err.println("Replica " + getRepN() + " at " + getTarget() + " is down");
                System.out.println("Trying to reconnect to another replica");
            }
        }
        System.out.println("Reconnected to replica " + this.repN + " at " + this.target);


    }

    public ClientResponse runPreviousCommand() {

        //Run previous command
        ClientResponse response;


        //Send response in cache if received response is not updated
        if (this.previousRequest.isQuery()) {
            Map<Integer,Integer> responseTS = getResponseTimestamp();

            convertTimestamp(responseTS);

            if (!happensBefore(responseTS)) {
                //Send response in cache if received response is not updated
                ClientResponse cacheResponse = this.responseCache.getLastRead(this.previousRequest.getFunctionAndArgs());
                if (cacheResponse != null) return cacheResponse;
            }

            response = this.previousRequest.runRequest(this.stub);
            this.responseCache.addEntry(this.previousRequest.getFunctionAndArgs(), response);
        }
        else  {
            response = this.previousRequest.runRequest(stub);
            convertTimestamp(response.getResponseTSMap());
        }

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());


        this.attempts.clear();
        this.attempts.add(this.currentPath);

        return response;

    }


    public UpdateResponse camJoin(String camName, double latitude, double longitude) {

        //Builds request and saves it in case of lost connection
        CamJoin request = new CamJoin();
        //Builds grpc request
        request.buildRequest(camName, latitude, longitude, this.prevTS, getUUID());


        ClientResponse response = request.runRequest(this.stub);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        convertTimestamp(response.getResponseTSMap());


        return response.getUpdateResponse();
    }

    public CamInfoResponse getCamInfo(String camName) {


        //Entry for response cache -> funtion name, args...
        List<String> serviceDesc = new ArrayList<>();
        serviceDesc.add("CamInfo");
        serviceDesc.add(camName);

        CamInfo request = new CamInfo(serviceDesc);
        //Builds grpc request
        request.buildRequest(camName, this.prevTS, getUUID());
        this.previousRequest = request;


        ClientResponse response = this.previousRequest.runRequest(this.stub);

        convertTimestamp(response.getResponseTSMap());

        //Send response in cache if received response is not updated
        if (happensBefore(response.getResponseTSMap()))
            this.responseCache.addEntry(serviceDesc, response);
        else
            return this.responseCache.getLastRead(serviceDesc).getCamInfoResponse();

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());


        return response.getCamInfoResponse();
    }

    public UpdateResponse reportObs(String camName, List<List<String>> observations) {


        //Builds request and saves it in case of lost connection
        Report request = new Report();
        //Builds grpc request
        request.buildRequest(camName, observations, this.prevTS, getUUID());
        this.previousRequest = request;

        ClientResponse response = this.previousRequest.runRequest(this.stub);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        convertTimestamp(response.getResponseTSMap());

        return response.getUpdateResponse();

    }


    public TrackResponse trackObj(String type, String id) {

        ClientResponse response;

        //Entry for response cache -> function name, args...
        List<String> serviceDesc = new ArrayList<>();
        serviceDesc.add("TrackObject");
        serviceDesc.add(type);
        serviceDesc.add(id);

        //Builds request and saves it in case of lost connection
        Track request = new Track(serviceDesc);
        //Builds grpc request
        request.buildRequest(type, id, this.prevTS, getUUID());
        ClientRequest cliRequest = request.getRequest();
        this.previousRequest = request;

        Map<Integer,Integer> responseTS = getResponseTimestamp();

        convertTimestamp(responseTS);

        if (!happensBefore(responseTS)) {
            //Send response in cache if received response is not updated
            ClientResponse cacheResponse = this.responseCache.getLastRead(serviceDesc);
            if (cacheResponse != null) return cacheResponse.getTrackResponse();
        }

        response = this.previousRequest.runRequest(this.stub);
        this.responseCache.addEntry(serviceDesc, response);

        //Merge Timestamps
        mergeTS(responseTS);


        return response.getTrackResponse();
    }

    public TraceResponse trackMatchObj(String type, String id) {

        ClientResponse response;

        //Entry for response cache -> funtion name, args...
        List<String> serviceDesc = new ArrayList<>();
        serviceDesc.add("TrackMatchObject");
        serviceDesc.add(type);
        serviceDesc.add(id);

        //Builds request and saves it in case of lost connection
        TrackMatch request = new TrackMatch(serviceDesc);
        //Builds grpc request
        request.buildRequest(type, id, this.prevTS, getUUID());
        this.previousRequest = request;

        Map<Integer,Integer> responseTS = getResponseTimestamp();

        convertTimestamp(responseTS);

        if (!happensBefore(responseTS)) {
            //Send response in cache if received response is not updated
            ClientResponse cacheResponse = this.responseCache.getLastRead(serviceDesc);
            if (cacheResponse != null) return cacheResponse.getTraceResponse();
        }

        response = this.previousRequest.runRequest(this.stub);
        this.responseCache.addEntry(serviceDesc, response);

        //Merge Timestamps
        mergeTS(responseTS);


        return response.getTraceResponse();
    }

    public TraceResponse traceObj(String type, String id) {

        ClientResponse response;

        //Entry for response cache -> funtion name, args...
        List<String> serviceDesc = new ArrayList<>();
        serviceDesc.add("TraceObject");
        serviceDesc.add(type);
        serviceDesc.add(id);

        //Builds request and saves it in case of lost connection
        Trace request = new Trace(serviceDesc);
        //Builds grpc request
        request.buildRequest(type, id, this.prevTS, getUUID());
        this.previousRequest = request;

        Map<Integer,Integer> responseTS = getResponseTimestamp();

        convertTimestamp(responseTS);

        if (!happensBefore(responseTS)) {
            //Send response in cache if received response is not updated
            ClientResponse cacheResponse = this.responseCache.getLastRead(serviceDesc);
            if (cacheResponse != null) return cacheResponse.getTraceResponse();
        }

        response = this.previousRequest.runRequest(this.stub);
        this.responseCache.addEntry(serviceDesc, response);
        //Merge Timestamps
        mergeTS(responseTS);


        return response.getTraceResponse();
    }


    public PingResponse ctrlPing() {

        //Entry for response cache -> function name, args...
        List<String> serviceDesc = new ArrayList<>();
        serviceDesc.add("Ping");

        //Builds request and saves it in case of lost connection
        Ping request = new Ping(serviceDesc);
        //Builds grpc request
        request.buildRequest(this.prevTS, getUUID());

        ClientResponse response = request.runRequest(this.stub);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        return response.getPingResponse();
    }

    public UpdateResponse ctrlClear() {

        //Builds request and saves it in case of lost connection
        Clear request = new Clear();
        //Builds grpc request
        request.buildRequest(this.prevTS, getUUID());
        this.previousRequest = request;
        ClientResponse response = this.previousRequest.runRequest(this.stub);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        convertTimestamp(response.getResponseTSMap());

        return response.getUpdateResponse();
    }


    public UpdateResponse ctrlInit() {

        //Builds request and saves it in case of lost connection
        Init request = new Init();
        //Builds grpc request
        request.buildRequest(this.prevTS, getUUID());
        this.previousRequest = request;

        ClientResponse response = this.previousRequest.runRequest(this.stub);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        convertTimestamp(response.getResponseTSMap());

        return response.getUpdateResponse();

    }

    private String getServerTarget(String zooHost, String zooPort, String repN) throws ZKNamingException, NoServersAvailableException {

        Random random = new Random();
        String path;
        ZKNaming zkNaming = new ZKNaming(zooHost, zooPort);
        ArrayList<ZKRecord> recs = new ArrayList<>(zkNaming.listRecords("/grpc/sauron/silo"));

        if (this.previousAvailableServers != -1 && this.previousAvailableServers != recs.size()) this.attempts.clear();

        this.previousAvailableServers = recs.size();

        if (recs.size() == 0 || recs.size() == attempts.size() || this.isStatic)
            throw new NoServersAvailableException();

        if (repN.equals("")) {

            path = recs.get(random.nextInt(recs.size())).getPath();
            while (attempts.contains(path)) {
                path = recs.get(random.nextInt(recs.size())).getPath();
            }
        } else
            path = "/grpc/sauron/silo/" + repN;

        this.attempts.add(path);
        this.currentPath = path;


        String[] segments = path.split("/");

        this.repN = segments[segments.length - 1];


        // lookup
        ZKRecord record = zkNaming.lookup(path);
        return record.getURI();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Map<Integer, Integer> getPrevTS() {
        return prevTS;
    }

    public void setPrevTS(Map<Integer, Integer> prevTS) {
        this.prevTS = prevTS;
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    public String getTarget() {
        return target;
    }

    public String getRepN() {
        return repN;
    }

    private void mergeTS(Map<Integer, Integer> map) {
        for (Integer key : map.keySet()) {
            if (this.prevTS.containsKey(key))
                this.prevTS.put(key, Integer.max(this.prevTS.get(key), map.get(key)));
            else
                this.prevTS.put(key, map.get(key));
        }
    }


    private boolean happensBefore(Map<Integer, Integer> map) {
        boolean isBefore = true;
        for (Map.Entry<Integer, Integer> entryA : this.prevTS.entrySet()) {
            Integer valueB = map.getOrDefault(entryA.getKey(), 0);
            if (entryA.getValue() > valueB) isBefore = false;
        }
        return isBefore;
    }



    //display TS map in order and converted to friendly presentation
    private void convertTimestamp(Map<Integer,Integer> timestamp) {

        ZKNaming zkNaming = new ZKNaming(this.host, this.port);
        ArrayList<ZKRecord> recs = null;
        try {
            recs = new ArrayList<>(zkNaming.listRecords("/grpc/sauron/silo"));
        } catch (ZKNamingException e) {
            e.printStackTrace();
        }
        List<Integer> replicaNumbers = new ArrayList<>();

        for (ZKRecord path: recs) {
            String[] segments = path.getPath().split("/");
            String rep = segments[segments.length - 1];

            replicaNumbers.add(Integer.parseInt(rep));
        }
        Collections.sort(replicaNumbers);

        System.out.print("Response with TS: ");
        for(Integer replica :replicaNumbers) {
            if (timestamp.get(replica) == null) System.out.print( "Rep " + replica + " -> 0; ");
            else System.out.print( "Rep " + replica +" -> " + timestamp.get(replica) + "; ");
        }
        System.out.println();

    }

    public Map<Integer,Integer> getResponseTimestamp() {
        EmptyRequest emptyRequest = EmptyRequest.newBuilder().build();
        return this.stub.timestamp(emptyRequest).getResponseTSMap();
    }

    @Override
    public final void close() {
        channel.shutdown();
    }
}