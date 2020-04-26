package pt.tecnico.sauron.silo.api;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogRecords {
    private int repN;
    private Map<Integer, Integer> timestamp = new ConcurrentHashMap<>();
    private Map<Integer, Integer> prevTS = new ConcurrentHashMap<>();
    private String id;
    Operation operation;

    public LogRecords() {
    }

    public LogRecords(int repN, Map<Integer, Integer> timestamp, Map<Integer, Integer> prev, String id,Operation operation) {
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
