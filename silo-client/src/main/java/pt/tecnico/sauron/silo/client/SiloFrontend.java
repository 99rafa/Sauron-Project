package pt.tecnico.sauron.silo.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.client.requests.*;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.util.*;


public class SiloFrontend implements AutoCloseable {

    private ResponseCache responseCache = new ResponseCache();
    private Request previousRequest;
    private ManagedChannel channel;
    private String host;
    private String port;
    private Map<Integer, Integer> prevTS = new HashMap<>();
    private String target;
    private String repN;

    private SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub;

    public SiloFrontend(String zooHost, String zooPort, String repN) throws ZKNamingException {

        this.host = zooHost;
        this.port = zooPort;
        this.target = getServerTarget(zooHost, zooPort, repN);

        this.channel = ManagedChannelBuilder.forTarget(this.target).usePlaintext().build();

        // Create a blocking stub.
        this.stub = SiloOperationsServiceGrpc.newBlockingStub(channel);
    }

    public SiloFrontend(String zooHost, String zooPort, String repN, Map<Integer, Integer> preTS) throws ZKNamingException {

        this.host = zooHost;
        this.port = zooPort;
        this.prevTS = preTS;
        this.target = getServerTarget(zooHost, zooPort, repN);

        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        // Create a blocking stub.
        this.stub = SiloOperationsServiceGrpc.newBlockingStub(channel);
    }

    //when a replica crashes, frontend reconnects to a random other replica
    public void renewConnection() throws ZKNamingException {
        this.channel.shutdownNow();
        this.target = getServerTarget(this.host, this.port, "");


        this.channel = ManagedChannelBuilder.forTarget(this.target).usePlaintext().build();


        // Create a blocking stub.
        this.stub = SiloOperationsServiceGrpc.newBlockingStub(channel);


        System.out.println("Reconnected to replica " + this.repN + " at " + this.target);


    }

    public ClientResponse runPreviousCommand() {

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

        return response;

    }


    public CamJoinResponse camJoin(String camName, double latitude, double longitude) {

        //Builds request and saves it in case of lost connection
        CamJoin request = new CamJoin();
        //Builds grpc request
        request.buildRequest(camName, latitude, longitude, this.prevTS, getUUID());
        this.previousRequest = request;


        ClientResponse response = this.previousRequest.runRequest(this.stub);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

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

        return response.getTrackResponse();
    }

    public TrackMatchResponse trackMatchObj(String type, String id) {

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

        return response.getTraceResponse();
    }

    public PingResponse ctrlPing(String inputCommand) {

        //Entry for response cache -> function name, args...
        List<String> serviceDesc = new ArrayList<>();
        serviceDesc.add("Ping");
        serviceDesc.add(inputCommand);

        //Builds request and saves it in case of lost connection
        Ping request = new Ping(serviceDesc);
        //Builds grpc request
        request.buildRequest(inputCommand, this.prevTS, getUUID());
        this.previousRequest = request;

        ClientResponse response = this.previousRequest.runRequest(this.stub);

        if (happensBefore(response.getResponseTSMap()))
            this.responseCache.addEntry(serviceDesc, response);
        else
            return this.responseCache.getLastRead(serviceDesc, response).getPingResponse();

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        return response.getPingResponse();
    }

    public ClearResponse ctrlClear() {

        //Builds request and saves it in case of lost connection
        Clear request = new Clear();
        //Builds grpc request
        request.buildRequest(this.prevTS, getUUID());
        this.previousRequest = request;
        ClientResponse response = this.previousRequest.runRequest(this.stub);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

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

        return response.getInitResponse();

    }

    private String getServerTarget(String zooHost, String zooPort, String repN) throws ZKNamingException {

        Random random = new Random();
        final String path;
        ZKNaming zkNaming = new ZKNaming(zooHost, zooPort);
        ArrayList<ZKRecord> recs = new ArrayList<>(zkNaming.listRecords("/grpc/sauron/silo"));

        //hack to catch zkNaming exception because there is no server available
        if (recs.size() == 0) repN = "1";

        if (repN.equals(""))

            path = recs.get(random.nextInt(recs.size())).getPath();

        else
            path = "/grpc/sauron/silo/" + repN;

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

    private int[] convertTimestamp(Map<Integer,Integer> timestamp) throws ZKNamingException {

        ZKNaming zkNaming = new ZKNaming(this.host, this.port);
        int numberOfReplicas = (new ArrayList<>(zkNaming.listRecords("/grpc/sauron/silo"))).size();

        int[] timestampArray = new int[numberOfReplicas] ;

        for (Map.Entry<Integer,Integer> entry : timestamp.entrySet())
            timestampArray[entry.getKey()-1] = entry.getValue();

        return timestampArray;
    }

    @Override
    public final void close() {
        channel.shutdown();
    }
}