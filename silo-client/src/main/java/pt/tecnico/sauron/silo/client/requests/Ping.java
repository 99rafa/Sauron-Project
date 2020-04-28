package pt.tecnico.sauron.silo.client.requests;

import pt.tecnico.sauron.silo.grpc.ClientRequest;
import pt.tecnico.sauron.silo.grpc.ClientResponse;
import pt.tecnico.sauron.silo.grpc.SiloOperationsServiceGrpc;

public class Ping extends Request  {

    public Ping(ClientRequest request) {
        super(request);
    }

    @Override
    public ClientResponse runRequest(SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub) {
        return stub.ctrlPing(getRequest());
    }
}
