package pt.tecnico.sauron.silo.client.requests;

import pt.tecnico.sauron.silo.grpc.ClientRequest;
import pt.tecnico.sauron.silo.grpc.ClientResponse;
import pt.tecnico.sauron.silo.grpc.SiloOperationsServiceGrpc;

import java.util.List;

public abstract class Request {

    private ClientRequest request;

    private boolean isQuery;

    private List<String> functionAndArgs;

    public Request(ClientRequest request) {
        this.request = request;
        this.isQuery = false;
    }

    public Request(ClientRequest request, List<String> functionAndArgs) {

        this.request = request;
        this.functionAndArgs = functionAndArgs;
        this.isQuery = true;

    }

    public abstract ClientResponse runRequest(SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub);

    public ClientRequest getRequest() {
        return request;
    }

    public boolean isQuery() {
        return isQuery;
    }

    public List<String> getFunctionAndArgs() {
        return functionAndArgs;
    }
}
