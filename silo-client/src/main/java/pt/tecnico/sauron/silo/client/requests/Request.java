package pt.tecnico.sauron.silo.client.requests;

import pt.tecnico.sauron.silo.grpc.ClientRequest;
import pt.tecnico.sauron.silo.grpc.ClientResponse;
import pt.tecnico.sauron.silo.grpc.SiloOperationsServiceGrpc;

public abstract class Request {

    private ClientRequest request;

    public Request(ClientRequest request) {
        this.request = request;
    }

    public abstract ClientResponse runRequest(SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub);

    public ClientRequest getRequest() {
        return request;
    }
}
