package pt.tecnico.sauron.silo.client.requests;

import pt.tecnico.sauron.silo.grpc.CamInfoRequest;
import pt.tecnico.sauron.silo.grpc.ClientRequest;
import pt.tecnico.sauron.silo.grpc.ClientResponse;
import pt.tecnico.sauron.silo.grpc.SiloOperationsServiceGrpc;

import java.util.List;
import java.util.Map;

public class CamInfo extends Request {

    public CamInfo(List<String> functionAndArgs) {

        super(functionAndArgs);

    }

    @Override
    public ClientResponse runRequest(SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub) {
        return stub.camInfo(getRequest());
    }

    public void buildRequest(String camName, Map<Integer, Integer> prevTs, String opId) {
        ClientRequest request = ClientRequest.newBuilder()
                .setCamInfoRequest(
                        CamInfoRequest.newBuilder()
                                .setCamName(camName).build()
                )
                .putAllPrevTS(prevTs)
                .setOpId(opId).build();

        setRequest(request);
    }
}

