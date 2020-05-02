package pt.tecnico.sauron.silo.api;

import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.grpc.ClientRequest;
import pt.tecnico.sauron.silo.grpc.ClientResponse;

public class Operation extends GossipMessage {

    String operation;
    ClientRequest request;
    StreamObserver<ClientResponse> observer;

    public Operation(String operation, ClientRequest request) {
        this.operation = operation;
        this.request = request;
    }

    public Operation(String operation, ClientRequest request, StreamObserver<ClientResponse> observer) {
        this.operation = operation;
        this.request = request;
        this.observer = observer;
    }

    public String getOperation() {
        return operation;
    }

    public ClientRequest getRequest() {
        return request;
    }

    public StreamObserver<ClientResponse> getObserver() {
        return observer;
    }
}
