package pt.tecnico.sauron.silo.client.requests;

import pt.tecnico.sauron.silo.grpc.ClientRequest;
import pt.tecnico.sauron.silo.grpc.ClientResponse;
import pt.tecnico.sauron.silo.grpc.SiloOperationsServiceGrpc;
import pt.tecnico.sauron.silo.grpc.TrackMatchRequest;

import java.util.List;
import java.util.Map;

public class TrackMatch extends Request {

    public TrackMatch(List<String> functionAndArgs) {
        super(functionAndArgs);
    }

    @Override
    public ClientResponse runRequest(SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub) {
        return stub.trackMatch(getRequest());
    }

    public void buildRequest(String type, String id, Map<Integer, Integer> prevTs, String opId) {
        ClientRequest request = ClientRequest.newBuilder()
                .setTrackMatchRequest(
                        TrackMatchRequest.newBuilder()
                                .setType(type)
                                .setSubId(id).build()
                )
                .putAllPrevTS(prevTs)
                .setOpId(opId).build();

        setRequest(request);
    }
}
