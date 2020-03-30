package pt.tecnico.sauron.silo.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.grpc.*;



public class SiloFrontend implements AutoCloseable {
    private final ManagedChannel channel;
    private final SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub;

    public SiloFrontend(String host, int port) {

        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

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

    public TrackResponse trackObj(TrackRequest request) {
        return stub.track(request);
    }

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