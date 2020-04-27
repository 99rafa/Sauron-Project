package pt.tecnico.sauron.silo.api;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogRecord {
    Operation operation;
    private int repN;
    private Map<Integer, Integer> timestamp = new ConcurrentHashMap<>();
    private Map<Integer, Integer> prevTS = new ConcurrentHashMap<>();
    private String id;

    public LogRecord() {
    }

    public LogRecord(int repN, Map<Integer, Integer> timestamp, Map<Integer, Integer> prev, String id, Operation operation) {
        this.repN = repN;
        this.timestamp = timestamp;
        this.prevTS = prev;
        this.id = id;
        this.operation = operation;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<Integer, Integer> getPrevTS() {
        return prevTS;
    }

    public void setPrevTS(Map<Integer, Integer> prevTS) {
        this.prevTS = prevTS;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }
}
