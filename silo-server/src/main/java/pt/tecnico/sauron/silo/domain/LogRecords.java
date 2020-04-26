package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.grpc.ClearResponse;
import pt.tecnico.sauron.silo.grpc.ClientRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogRecords {
    private int repN;
    private Map<Integer, Integer> timestamp = new ConcurrentHashMap<Integer, Integer>();
    private ClientRequest op;
    private Map<Integer, Integer> prevTS = new ConcurrentHashMap<Integer, Integer>();
    private String id;

    public LogRecords() {
    }

    public LogRecords(int repN, Map<Integer, Integer> timestamp, ClientRequest op, Map<Integer, Integer> prev, String id) {
        this.repN = repN;
        this.timestamp = timestamp;
        this.op = op;
        this.prevTS = prev;
        this.id = id;
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
}
