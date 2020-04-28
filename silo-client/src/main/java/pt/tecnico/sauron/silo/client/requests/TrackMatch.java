package pt.tecnico.sauron.silo.client.requests;

import pt.tecnico.sauron.silo.grpc.ClientRequest;
import pt.tecnico.sauron.silo.grpc.ClientResponse;
import pt.tecnico.sauron.silo.grpc.SiloOperationsServiceGrpc;

import java.util.List;

public class TrackMatch extends Request{

    public TrackMatch(ClientRequest request, List<String> functionAndArgs) {

        super(request, functionAndArgs);

    }
    @Override
    public ClientResponse runRequest(SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub) {
        return stub.trackMatch(getRequest());
    }
}
