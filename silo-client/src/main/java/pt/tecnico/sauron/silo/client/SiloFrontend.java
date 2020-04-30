package pt.tecnico.sauron.silo.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.tecnico.sauron.silo.client.requests.*;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.util.*;


public class SiloFrontend implements AutoCloseable {

    private String currentPath;
    private List<String> attempts = new ArrayList<>();
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

        try{
            ctrlPing("Checking server availability");//PING
        } catch (StatusRuntimeException e){
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

        try{
            ctrlPing("Checking server availability");//PING
        } catch (StatusRuntimeException e){
            renewConnection();
        }

    }

    //when a replica crashes, frontend reconnects to a random other replica
    public void renewConnection() throws ZKNamingException, NoServersAvailableException {
        System.err.println("Replica " + getRepN() + " at " + getTarget() +" is down");
        System.out.println("Trying to reconnect to another replica" );

        while(true){
            try {
                this.channel.shutdownNow();

                this.target = getServerTarget(this.host, this.port, "");

                System.out.println(isStatic);


                this.channel = ManagedChannelBuilder.forTarget(this.target).usePlaintext().build();

                // Create a blocking stub.
                this.stub = SiloOperationsServiceGrpc.newBlockingStub(channel);

                ctrlPing("Checking server availability");//PING
                break;
            } catch (RuntimeException e){
                System.err.println("Replica " + getRepN() + " at " + getTarget() +" is down");
                System.out.println("Trying to reconnect to another replica" );
            }
        }
        System.out.println("Reconnected to replica " + this.repN + " at " + this.target);


    }

    public ClientResponse runPreviousCommand(){

        //Run previous command
        ClientResponse response = this.previousRequest.runRequest(stub);


        //Send response in cache if received response aint updated
        if (this.previousRequest.isQuery()) {
            if (happensBefore(response.getResponseTSMap()))
                this.responseCache.addEntry(this.previousRequest.getFunctionAndArgs(), response);
            else
                return this.responseCache.getLastRead(this.previousRequest.getFunctionAndArgs(), response);
        }

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        System.out.println("Frontend received answer with TS " +(Arrays.toString(convertTimestamp(response.getResponseTSMap()))));

        this.attempts.clear();
        this.attempts.add(this.currentPath);

        return response;

    }


    public CamJoinResponse camJoin(String camName, double latitude, double longitude) {

        //Builds request and saves it in case of lost connection
        CamJoin request = new CamJoin();
        //Builds grpc request
        request.buildRequest(camName, latitude, longitude, this.prevTS, getUUID());


        ClientResponse response = request.runRequest(this.stub);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        System.out.println("Frontend received answer with TS " +(Arrays.toString(convertTimestamp(response.getResponseTSMap()))));


        return response.getCamJoinResponse();
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

        //Send response in cache if received response aint updated
        if (happensBefore(response.getResponseTSMap()))
            this.responseCache.addEntry(serviceDesc, response);
        else
            return this.responseCache.getLastRead(serviceDesc, response).getCamInfoResponse();

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        System.out.println("Frontend received answer with TS " +(Arrays.toString(convertTimestamp(response.getResponseTSMap()))));


        return response.getCamInfoResponse();
    }

    public ReportResponse reportObs(String camName, List<List<String>> observations) {


        //Builds request and saves it in case of lost connection
        Report request = new Report();
        //Builds grpc request
        request.buildRequest(camName, observations, this.prevTS, getUUID());
        this.previousRequest = request;

        ClientResponse response = this.previousRequest.runRequest(this.stub);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        System.out.println("Frontend received answer with TS " +(Arrays.toString(convertTimestamp(response.getResponseTSMap()))));

        return response.getReportResponse();
    }


    public TrackResponse trackObj(String type, String id) {

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

        ClientResponse response = this.previousRequest.runRequest(this.stub);

        //Send response in cache if received response aint updated
        if (happensBefore(response.getResponseTSMap()))
            this.responseCache.addEntry(serviceDesc, response);
        else
            return this.responseCache.getLastRead(serviceDesc, response).getTrackResponse();

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        System.out.println("Frontend received answer with TS " +(Arrays.toString(convertTimestamp(response.getResponseTSMap()))));

        return response.getTrackResponse();
    }

    public TrackMatchResponse trackMatchObj(String type, String id)  {

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

        ClientResponse response = this.previousRequest.runRequest(this.stub);

        //Send response in cache if received response aint updated
        if (happensBefore(response.getResponseTSMap()))
            this.responseCache.addEntry(serviceDesc, response);
        else
            return this.responseCache.getLastRead(serviceDesc, response).getTrackMatchResponse();

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        System.out.println("Frontend received answer with TS " +(Arrays.toString(convertTimestamp(response.getResponseTSMap()))));


        return response.getTrackMatchResponse();
    }

    public TraceResponse traceObj(String type, String id) {

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

        ClientResponse response = this.previousRequest.runRequest(this.stub);


        //Send response in cache if received response aint updated


        if (happensBefore(response.getResponseTSMap()))
            this.responseCache.addEntry(serviceDesc, response);
        else
            return this.responseCache.getLastRead(serviceDesc, response).getTraceResponse();

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        System.out.println("Frontend received answer with TS " +(Arrays.toString(convertTimestamp(response.getResponseTSMap()))));


        return response.getTraceResponse();
    }


    public PingResponse ctrlPing(String inputCommand)  {

        //Entry for response cache -> function name, args...
        List<String> serviceDesc = new ArrayList<>();
        serviceDesc.add("Ping");
        serviceDesc.add(inputCommand);

        //Builds request and saves it in case of lost connection
        Ping request = new Ping(serviceDesc);
        //Builds grpc request
        request.buildRequest(inputCommand, this.prevTS, getUUID());

        ClientResponse response = request.runRequest(this.stub);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        return response.getPingResponse();
    }

    public ClearResponse ctrlClear()  {

        //Builds request and saves it in case of lost connection
        Clear request = new Clear();
        //Builds grpc request
        request.buildRequest(this.prevTS, getUUID());
        this.previousRequest = request;
        ClientResponse response = this.previousRequest.runRequest(this.stub);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        System.out.println("Frontend received answer with TS " +(Arrays.toString(convertTimestamp(response.getResponseTSMap()))));


        return response.getClearResponse();
    }


    public InitResponse ctrlInit() {

        //Builds request and saves it in case of lost connection
        Init request = new Init();
        //Builds grpc request
        request.buildRequest(this.prevTS, getUUID());
        this.previousRequest = request;

        ClientResponse response = this.previousRequest.runRequest(this.stub);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        System.out.println("Frontend received answer with TS " +(Arrays.toString(convertTimestamp(response.getResponseTSMap()))));

        return response.getInitResponse();

    }

    private String getServerTarget(String zooHost, String zooPort, String repN) throws ZKNamingException, NoServersAvailableException {

        Random random = new Random();
        String path;
        ZKNaming zkNaming = new ZKNaming(zooHost, zooPort);
        ArrayList<ZKRecord> recs = new ArrayList<>(zkNaming.listRecords("/grpc/sauron/silo"));

        if (recs.size() == 0 || recs.size() == attempts.size() || this.isStatic) throw new NoServersAvailableException();

        if (repN.equals("")){

            path = recs.get(random.nextInt(recs.size())).getPath();
            while(attempts.contains(path)) {
                path = recs.get(random.nextInt(recs.size())).getPath();
            }
        }

        else
            path = "/grpc/sauron/silo/" + repN;

        this.attempts.add(path);
        this.currentPath = path;

        //this.instance = ;

        System.out.println(path);

        String[] segments = path.split("/");

        this.repN = segments[segments.length-1];


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

    private int[] convertTimestamp(Map<Integer,Integer> timestamp) {
        try {
            ZKNaming zkNaming = new ZKNaming(this.host, this.port);
            int numberOfReplicas = (new ArrayList<>(zkNaming.listRecords("/grpc/sauron/silo"))).size();

            int[] timestampArray = new int[numberOfReplicas];

            for (Map.Entry<Integer, Integer> entry : timestamp.entrySet())
                timestampArray[entry.getKey() - 1] = entry.getValue();

            return timestampArray;
        }
        catch (ZKNamingException e) {
            System.err.println("Server could not be found or no servers available");
        }

        return null;
    }

    @Override
    public final void close() {
        channel.shutdown();
    }
}