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

    public camJoinResponse camJoin(camJoinRequest request) {
        return stub.camJoin(request);
    }

    public camInfoResponse getCamInfo(camInfoRequest request) {
        return stub.camInfo(request);
    }

    public reportResponse reportObs(reportRequest request) {
        return stub.report(request);
    }

    public trackResponse trackObj(trackRequest request) {
        return stub.track(request);
    }

    public traceResponse traceObj(traceRequest request) {
        return stub.trace(request);
    }
    

    @Override
    public final void close() {
        channel.shutdown();
    }
}