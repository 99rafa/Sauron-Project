package pt.tecnico.sauron.silo.client.requests;

import pt.tecnico.sauron.silo.grpc.ClientRequest;
import pt.tecnico.sauron.silo.grpc.ClientResponse;
import pt.tecnico.sauron.silo.grpc.EmptyRequest;
import pt.tecnico.sauron.silo.grpc.SiloOperationsServiceGrpc;

import java.util.Map;

public class Clear extends Request {

    public Clear() {
        super();
    }


    @Override
    public ClientResponse runRequest(SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub) {
        return stub.ctrlClear(getRequest());
    }

    public void buildRequest(Map<Integer, Integer> prevTs, String opId) {
        ClientRequest request = ClientRequest.newBuilder()
                .setEmptyRequest(
                        EmptyRequest.newBuilder().build()
                )
                .putAllPrevTS(prevTs)
                .setOpId(opId).build();

        setRequest(request);
    }
}
