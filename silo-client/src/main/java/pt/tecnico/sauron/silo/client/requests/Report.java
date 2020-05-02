package pt.tecnico.sauron.silo.client.requests;

import pt.tecnico.sauron.silo.grpc.*;

import java.util.List;
import java.util.Map;

public class Report extends Request {

    public Report() {
        super();
    }

    @Override
    public ClientResponse runRequest(SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub) {
        return stub.report(getRequest());
    }

    public void buildRequest(String camName, List<List<String>> observations, Map<Integer, Integer> prevTs, String opId) {
        ReportRequest.Builder builder = ReportRequest.newBuilder()
                .setCamName(camName);

        for (List<String> obs : observations) {

            builder.addObservation(ObservationMessage.newBuilder().setType(obs.get(0)).setId(obs.get(1)).setDatetime(obs.get(2)).build());

        }

        ClientRequest request = ClientRequest.newBuilder()
                .setReportRequest(builder.build())
                .putAllPrevTS(prevTs)
                .setOpId(opId).build();

        setRequest(request);
    }
}
