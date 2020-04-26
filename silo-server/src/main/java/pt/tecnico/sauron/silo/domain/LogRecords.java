package pt.tecnico.sauron.silo.domain;

import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.grpc.ClearResponse;
import pt.tecnico.sauron.silo.grpc.ClientRequest;
import pt.tecnico.sauron.silo.grpc.ClientResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogRecords {
    private int repN;
    private Map<Integer, Integer> timestamp = new ConcurrentHashMap<>();
    private ClientRequest op;
    private Map<Integer, Integer> prevTS = new ConcurrentHashMap<>();
    private String id;
    private StreamObserver<ClientResponse> responseObserver;

    public LogRecords() {
    }

    public LogRecords(int repN, Map<Integer, Integer> timestamp, ClientRequest op, Map<Integer, Integer> prev, String id,StreamObserver<ClientResponse> responseObserver) {
        this.repN = repN;
        this.timestamp = timestamp;
        this.op = op;
        this.prevTS = prev;
        this.id = id;
        this.responseObserver = responseObserver;
    }

    public int getRepN() {
        return repN;
    }

    public void setRepN(int repN) {
        this.repN = repN;
    }

    public Map<Integer, Integer> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Map<Integer, Integer> timestamp) {
        this.timestamp = timestamp;
    }

    public ClientRequest getOp() {
        return op;
    }

    public void setOp(ClientRequest op) {
        this.op = op;
    }

    public Map<Integer, Integer> getPrev() {
        return prevTS;
    }

    public void setPrev(Map<Integer, Integer> prev) {
        this.prevTS = prev;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StreamObserver<ClientResponse> getResponseObserver() {
        return responseObserver;
    }

    public void setResponseObserver(StreamObserver<ClientResponse> responseObserver) {
        this.responseObserver = responseObserver;
    }
}
