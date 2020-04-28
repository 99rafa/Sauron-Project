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

    private SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub;

    public SiloFrontend(String zooHost, String zooPort, String repN) throws ZKNamingException {

        this.host = zooHost;
        this.port = zooPort;
        String target = getServerTarget(zooHost, zooPort, repN);

        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        // Create a blocking stub.
        this.stub = SiloOperationsServiceGrpc.newBlockingStub(channel);
    }

    public SiloFrontend(String zooHost, String zooPort, String repN, Map<Integer, Integer> preTS) throws ZKNamingException {

        this.host = zooHost;
        this.port = zooPort;
        this.prevTS = preTS;
        String target = getServerTarget(zooHost, zooPort, repN);

        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        // Create a blocking stub.
        this.stub = SiloOperationsServiceGrpc.newBlockingStub(channel);
    }

    //when a replica crashes, frontend reconnects to a random other replica
    public void renewConnection() throws ZKNamingException {
        this.channel.shutdownNow();
        String target = getServerTarget(this.host, this.port, "");

        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        // Create a blocking stub.
        this.stub = SiloOperationsServiceGrpc.newBlockingStub(channel);

        //Run previous command
        this.previousRequest.runRequest(stub);

    }


    public CamJoinResponse camJoin(CamJoinRequest request) {

        //Builds request and saves it in case of lost connection
        ClientRequest cliRequest = ClientRequest.newBuilder().setCamJoinRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();
        this.previousRequest = new CamJoin(cliRequest);

        ClientResponse response = stub.camJoin(cliRequest);//Update request

        //Nova thread
        //GetResponse

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        return response.getCamJoinResponse();
    }

    public CamInfoResponse getCamInfo(CamInfoRequest request) {

        //Entry for response cache -> funtion name, args...
        List<String> serviceDesc = new ArrayList<>();
        serviceDesc.add("CamInfo");
        serviceDesc.add(request.getCamName());

        ClientRequest cliRequest = ClientRequest.newBuilder().setCamInfoRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();
        this.previousRequest = new CamInfo(cliRequest);

        ClientResponse response = stub.camInfo(cliRequest);

        //Send response in cache if received response aint updated
        if (happensBefore(response.getResponseTSMap()))
            this.responseCache.addEntry(serviceDesc, response);
        else
            return this.responseCache.getLastRead(serviceDesc, response).getCamInfoResponse();

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        return response.getCamInfoResponse();
    }

    public ReportResponse reportObs(ReportRequest request) {

        ClientRequest cliRequest = ClientRequest.newBuilder().setReportRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();
        this.previousRequest = new Report(cliRequest);

        ClientResponse response = stub.report(cliRequest);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        return response.getReportResponse();
    }

    public TrackResponse trackObj(TrackRequest request) {

        //Entry for response cache -> funtion name, args...
        List<String> serviceDesc = new ArrayList<>();
        serviceDesc.add("TrackObject");
        serviceDesc.add(request.getId());
        serviceDesc.add(request.getType());

        ClientRequest cliRequest = ClientRequest.newBuilder().setTrackRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();
        this.previousRequest = new Track(cliRequest);

        ClientResponse response = stub.track(cliRequest);

        //Send response in cache if received response aint updated
        if (happensBefore(response.getResponseTSMap()))
            this.responseCache.addEntry(serviceDesc, response);
        else
            return this.responseCache.getLastRead(serviceDesc, response).getTrackResponse();

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        return response.getTrackResponse();
    }

    public TrackMatchResponse trackMatchObj(TrackMatchRequest request) {

        //Entry for response cache -> funtion name, args...
        List<String> serviceDesc = new ArrayList<>();
        serviceDesc.add("TrackMatchObject");
        serviceDesc.add(request.getSubId());
        serviceDesc.add(request.getType());

        ClientRequest cliRequest = ClientRequest.newBuilder().setTrackMatchRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();
        this.previousRequest = new TrackMatch(cliRequest);

        ClientResponse response = stub.trackMatch(cliRequest);

        //Send response in cache if received response aint updated
        if (happensBefore(response.getResponseTSMap()))
            this.responseCache.addEntry(serviceDesc, response);
        else
            return this.responseCache.getLastRead(serviceDesc, response).getTrackMatchResponse();

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        return response.getTrackMatchResponse();
    }

    public TraceResponse traceObj(TraceRequest request) {

        //Entry for response cache -> funtion name, args...
        List<String> serviceDesc = new ArrayList<>();
        serviceDesc.add("TraceObject");
        serviceDesc.add(request.getId());
        serviceDesc.add(request.getType());

        ClientRequest cliRequest = ClientRequest.newBuilder().setTraceRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();
        this.previousRequest = new Trace(cliRequest);

        ClientResponse response = stub.trace(cliRequest);


        //Send response in cache if received response aint updated


        if (happensBefore(response.getResponseTSMap()))
            this.responseCache.addEntry(serviceDesc, response);
        else
            return this.responseCache.getLastRead(serviceDesc, response).getTraceResponse();

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        return response.getTraceResponse();
    }

    public PingResponse ctrlPing(PingRequest request) {

        ClientRequest cliRequest = ClientRequest.newBuilder().setPingRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();
        this.previousRequest = new Ping(cliRequest);

        ClientResponse response = stub.ctrlPing(cliRequest);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        return response.getPingResponse();
    }

    public ClearResponse ctrlClear(ClearRequest request) {
        ClientRequest cliRequest = ClientRequest.newBuilder().setClearRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();
        this.previousRequest = new Clear(cliRequest);

        ClientResponse response = stub.ctrlClear(cliRequest);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        return response.getClearResponse();
    }


    public InitResponse ctrlInit(InitRequest request) {
        ClientRequest cliRequest = ClientRequest.newBuilder().setInitRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();
        this.previousRequest = new Init(cliRequest);

        ClientResponse response = stub.ctrlInit(cliRequest);

        //Merge Timestamps
        mergeTS(response.getResponseTSMap());

        return response.getInitResponse();

    }

    private String getServerTarget(String zooHost, String zooPort, String repN) throws ZKNamingException {

        Random random = new Random();
        final String path;
        ZKNaming zkNaming = new ZKNaming(zooHost, zooPort);
        ArrayList<ZKRecord> recs = new ArrayList<>(zkNaming.listRecords("/grpc/sauron/silo"));

        if (repN.equals(""))
            path = recs.get(random.nextInt(recs.size())).getPath();
        else
            path = "/grpc/sauron/silo/" + repN;

        //this.instance = Integer.parseInt(path.split("/")[4]);

        System.out.println(path);

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

    @Override
    public final void close() {
        channel.shutdown();
    }
}