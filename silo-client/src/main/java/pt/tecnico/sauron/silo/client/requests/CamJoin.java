package pt.tecnico.sauron.silo.client.requests;

import pt.tecnico.sauron.silo.grpc.CamJoinRequest;
import pt.tecnico.sauron.silo.grpc.ClientRequest;
import pt.tecnico.sauron.silo.grpc.ClientResponse;
import pt.tecnico.sauron.silo.grpc.SiloOperationsServiceGrpc;

import java.util.Map;

public class CamJoin extends Request {

    public CamJoin() {
        super();
    }

    @Override
    public ClientResponse runRequest(SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub) {
        return stub.camJoin(getRequest());
    }

    public void buildRequest(String camName, Double latitude, Double longitude, Map<Integer, Integer> prevTs, String opId) {
        ClientRequest request = ClientRequest.newBuilder()
                .setCamJoinRequest(CamJoinRequest.newBuilder()
                        .setCamName(camName)
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                )
                .putAllPrevTS(prevTs)
                .setOpId(opId).build();

        setRequest(request);
    }

}
