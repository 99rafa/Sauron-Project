package pt.tecnico.sauron.silo.api;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
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
    private Map<String,SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub> stubs = new HashMap<>();
    private String target;

    public ServerGossipGateway(String zooHost, String zooPort, String repN) throws ZKNamingException {

        ZKNaming zkNaming = new ZKNaming(zooHost, zooPort);

        for (ZKRecord record : zkNaming.listRecords("/grpc/sauron/silo")) {
            if (record.getPath().contains(repN))
                continue;
            String target = record.getURI();
            this.target = target;

            String[] segments = record.getPath().split("/");
            // Grab the last segment
            String replicaNumber = segments[segments.length - 1];

            ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            this.channels.add(channel);
            this.stubs.put(replicaNumber,SiloOperationsServiceGrpc.newBlockingStub(channel));
        }

    }

    public void gossip(GossipRequest request) {
        for (Map.Entry<String,SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub> stub : this.stubs.entrySet()) {
            System.out.println("Contacting replica "+  stub.getKey() + " at " + target + stub.getKey() + " sending updates");
            stub.getValue().gossip(request);
        }
    }


    @Override
    public void close() {
        for (ManagedChannel channel : this.channels)
            channel.shutdownNow();
    }
}
