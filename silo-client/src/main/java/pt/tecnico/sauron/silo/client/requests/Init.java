package pt.tecnico.sauron.silo.client.requests;

import pt.tecnico.sauron.silo.grpc.ClientRequest;
import pt.tecnico.sauron.silo.grpc.ClientResponse;
import pt.tecnico.sauron.silo.grpc.InitRequest;
import pt.tecnico.sauron.silo.grpc.SiloOperationsServiceGrpc;

import java.util.Map;

public class Init extends Request {

    public Init() {
        super();
    }

    @Override
    public ClientResponse runRequest(SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub) {
        return stub.ctrlInit(getRequest());
    }

    public void buildRequest(Map<Integer, Integer> prevTs, String opId) {
        ClientRequest request = ClientRequest.newBuilder()
                .setInitRequest(
                        InitRequest.newBuilder().build()
                )
                .putAllPrevTS(prevTs)
                .setOpId(opId).build();

        setRequest(request);
    }
}
