package pt.tecnico.sauron.silo.client.requests;

import pt.tecnico.sauron.silo.grpc.ClientRequest;
import pt.tecnico.sauron.silo.grpc.ClientResponse;
import pt.tecnico.sauron.silo.grpc.SiloOperationsServiceGrpc;

public class CamJoin extends Request {

    public CamJoin(ClientRequest request) {
        super(request);
    }

    @Override
    public ClientResponse runRequest(SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub) {
        return stub.camJoin(getRequest());
    }
}
