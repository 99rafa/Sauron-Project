package pt.tecnico.sauron.silo.api;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import pt.tecnico.sauron.silo.exceptions.InvalidCoordinatesException;
import pt.tecnico.sauron.silo.grpc.GossipRequest;
import pt.tecnico.sauron.silo.grpc.SiloOperationsServiceGrpc;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerGossipGateway extends InvalidCoordinatesException implements AutoCloseable {

    private List<ManagedChannel> channels = new ArrayList<>();
    private Map<String, SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub> stubs = new HashMap<>();

    public ServerGossipGateway(String zooHost, String zooPort, String repN) throws ZKNamingException {

        ZKNaming zkNaming = new ZKNaming(zooHost, zooPort);

        for (ZKRecord record : zkNaming.listRecords("/grpc/sauron/silo")) {
            if (record.getPath().contains(repN))
                continue;
            String target = record.getURI();

            ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            this.channels.add(channel);
            this.stubs.put(target, SiloOperationsServiceGrpc.newBlockingStub(channel));
        }

    }

    public List<String> gossip(GossipRequest request, GossipRequest backUp, List<String> missingReps) {
        List<String> unavailable = new ArrayList<>();

        for (Map.Entry<String, SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub> stub : this.stubs.entrySet()) {
            System.out.println("Contacting replica at " + stub.getKey() + " sending updates...");
            try {
                if (missingReps.contains(stub.getKey())) {
                    stub.getValue().gossip(backUp);
                }
                else stub.getValue().gossip(request);
                System.out.println("Contact with replica at "+ stub.getKey() + " successful");
            }
            catch (StatusRuntimeException e) {
                if (e.getStatus().getCode().equals(Status.Code.UNAVAILABLE)) {
                    System.out.println("Caught exception while contacting replica at " + stub.getKey() + ".Skipping...");
                    unavailable.add(stub.getKey());
                }
            }
        }
        return unavailable;
    }


    @Override
    public void close() {
        for (ManagedChannel channel : this.channels)
            channel.shutdownNow();
    }
}
