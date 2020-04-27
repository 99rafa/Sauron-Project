package pt.tecnico.sauron.silo.api;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.grpc.GossipRequest;
import pt.tecnico.sauron.silo.grpc.SiloOperationsServiceGrpc;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.util.ArrayList;
import java.util.List;

public class ServerGossipGateway implements AutoCloseable {

    private List<ManagedChannel> channels = new ArrayList<>();
    private List<SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub> stubs = new ArrayList<>();

    public ServerGossipGateway(String zooHost, String zooPort, String repN) throws ZKNamingException {

        ZKNaming zkNaming = new ZKNaming(zooHost, zooPort);

        for (ZKRecord record : zkNaming.listRecords("/grpc/sauron/silo")) {
            if (record.getPath().contains(repN))
                continue;
            String target = record.getURI();
            ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            this.channels.add(channel);
            this.stubs.add(SiloOperationsServiceGrpc.newBlockingStub(channel));
        }

    }

    public void gossip(GossipRequest request) {
        for (SiloOperationsServiceGrpc.SiloOperationsServiceBlockingStub stub : this.stubs)
            stub.gossip(request);
    }


    @Override
    public void close() {
        for (ManagedChannel channel : this.channels)
            channel.shutdownNow();
    }
}
