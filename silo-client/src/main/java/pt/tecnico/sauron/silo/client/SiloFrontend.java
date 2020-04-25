package pt.tecnico.sauron.silo.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class SiloFrontend implements AutoCloseable {
    private final ManagedChannel channel;
    private final SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub;

    public SiloFrontend(String zooHost, String zooPort, String repN) throws ZKNamingException {

        Random random = new Random();
        final String path;
        System.out.println(zooHost + ":" + zooPort);
        ZKNaming zkNaming = new ZKNaming(zooHost,zooPort);
        ArrayList<ZKRecord> recs = new ArrayList<>(zkNaming.listRecords("/grpc/sauron/silo"));

        for(ZKRecord r : recs) {
            System.out.println(r.getURI());
            System.out.println(r.getPath());
        }

        if(repN.equals(""))
            path = recs.get(random.nextInt(recs.size())).getPath();
        else
            path = "/grpc/sauron/silo/" + repN;

        System.out.println(path);

        // lookup
        ZKRecord record = zkNaming.lookup(path);
        String target = record.getURI();

        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        // Create a blocking stub.
        stub = SiloOperationsServiceGrpc.newBlockingStub(channel);
    }

    public CamJoinResponse camJoin(CamJoinRequest request) {
        return stub.camJoin(request);
    }

    public CamInfoResponse getCamInfo(CamInfoRequest request) {
        return stub.camInfo(request);
    }

    public ReportResponse reportObs(ReportRequest request) {
        return stub.report(request);
    }

    public TrackResponse trackObj(TrackRequest request) { return stub.track(request); }

    public TrackMatchResponse trackMatchObj(TrackMatchRequest request) {return stub.trackMatch(request);}

    public TraceResponse traceObj(TraceRequest request) {
        return stub.trace(request);
    }

    public PingResponse ctrlPing(PingRequest request) { return stub.ctrlPing(request);}

    public ClearResponse ctrlClear(ClearRequest request) { return stub.ctrlClear(request); }

    public InitResponse ctrlInit(InitRequest request) { return stub.ctrlInit(request); }


    @Override
    public final void close() {
        channel.shutdown();
    }
}