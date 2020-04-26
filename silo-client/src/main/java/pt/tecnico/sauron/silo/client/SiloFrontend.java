package pt.tecnico.sauron.silo.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class SiloFrontend implements AutoCloseable {
    private  ManagedChannel channel;
    private String host;
    private String port;
    private Map<Integer,Integer> prevTS = new HashMap<>();

    private SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub;

    public SiloFrontend(String zooHost, String zooPort, String repN) throws ZKNamingException {

        this.host = zooHost;
        this.port = zooPort;
        String target = getServerTarget(zooHost,zooPort,repN);

        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        // Create a blocking stub.
        this.stub = SiloOperationsServiceGrpc.newBlockingStub(channel);
    }

    public CamJoinResponse camJoin(CamJoinRequest request) {
        ClientRequest cliRequest = ClientRequest.newBuilder().setCamJoinRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();

        ClientResponse response = stub.camJoin(cliRequest);

        //Merge Timestamps
        mergeTS(response.getUpdateTSMap());

        return response.getCamJoinResponse();
    }

    public CamInfoResponse getCamInfo(CamInfoRequest request) {
        ClientRequest cliRequest = ClientRequest.newBuilder().setCamInfoRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();

        ClientResponse response = stub.camJoin(cliRequest);

        //Merge Timestamps
        mergeTS(response.getUpdateTSMap());

        return response.getCamInfoResponse();
    }

    public ReportResponse reportObs(ReportRequest request) {
        ClientRequest cliRequest = ClientRequest.newBuilder().setReportRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();

        ClientResponse response = stub.camJoin(cliRequest);

        //Merge Timestamps
        mergeTS(response.getUpdateTSMap());

        return response.getReportResponse();
    }

    public TrackResponse trackObj(TrackRequest request) {
        ClientRequest cliRequest = ClientRequest.newBuilder().setTrackRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();

        ClientResponse response = stub.camJoin(cliRequest);

        //Merge Timestamps
        mergeTS(response.getUpdateTSMap());

        return response.getTrackRequest();
    }

    public TrackMatchResponse trackMatchObj(TrackMatchRequest request) {
        ClientRequest cliRequest = ClientRequest.newBuilder().setTrackMatchRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();

        ClientResponse response = stub.camJoin(cliRequest);

        //Merge Timestamps
        mergeTS(response.getUpdateTSMap());

        return response.getTrackMatchResponse();
    }

    public TraceResponse traceObj(TraceRequest request) {
        ClientRequest cliRequest = ClientRequest.newBuilder().setTraceRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();

        ClientResponse response = stub.camJoin(cliRequest);

        //Merge Timestamps
        mergeTS(response.getUpdateTSMap());

        return response.getTraceResponse();
    }

    public PingResponse ctrlPing(PingRequest request) {
        ClientRequest cliRequest = ClientRequest.newBuilder().setPingRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();

        ClientResponse response = stub.camJoin(cliRequest);

        //Merge Timestamps
        mergeTS(response.getUpdateTSMap());

        return response.getPingResponse();
    }

    public ClearResponse ctrlClear(ClearRequest request) {
        ClientRequest cliRequest = ClientRequest.newBuilder().setClearRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();

        ClientResponse response = stub.camJoin(cliRequest);

        //Merge Timestamps
        mergeTS(response.getUpdateTSMap());

        return response.getClearResponse();
    }

    public InitResponse ctrlInit(InitRequest request) {
        ClientRequest cliRequest = ClientRequest.newBuilder().setInitRequest(request).putAllPrevTS(this.prevTS).setOpId(getUUID()).build();

        ClientResponse response = stub.camJoin(cliRequest);

        //Merge Timestamps
        mergeTS(response.getUpdateTSMap());

        return response.getInitResponse();

    }

    private String getServerTarget(String zooHost, String zooPort, String repN) throws ZKNamingException {

        Random random = new Random();
        final String path;
        ZKNaming zkNaming = new ZKNaming(zooHost,zooPort);
        ArrayList<ZKRecord> recs = new ArrayList<>(zkNaming.listRecords("/grpc/sauron/silo"));

        if(repN.equals(""))
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

    private String getUUID(){
        return UUID.randomUUID().toString();
    }

    private void mergeTS(Map<Integer,Integer> map){
        for(Integer key : map.keySet()){
            if(this.prevTS.containsKey(key))
                this.prevTS.put(key,Integer.max(this.prevTS.get(key),map.get(key)));
            else
                this.prevTS.put(key,map.get(key));
        }
    }


    @Override
    public final void close() {
        channel.shutdown();
    }
}