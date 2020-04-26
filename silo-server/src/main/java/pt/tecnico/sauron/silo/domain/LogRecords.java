package pt.tecnico.sauron.silo.domain;

import java.util.ArrayList;
import java.util.List;

public class LogRecords {
    private int repN;
    private List<Integer> timestamp = new ArrayList<>();
    private String op;
    private List<Integer> prev = new ArrayList<>();
    private int id;

    public LogRecords() {
    }

    public LogRecords(int repN, List<Integer> timestamp, String op, List<Integer> prev, int id) {
        this.repN = repN;
        this.timestamp = timestamp;
        this.op = op;
        this.prev = prev;
        this.id = id;
    }

    public int getRepN() {
        return repN;
    }

    public void setRepN(int repN) {
        this.repN = repN;
    }

    public List<Integer> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(List<Integer> timestamp) {
        this.timestamp = timestamp;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public List<Integer> getPrev() {
        return prev;
    }

    public void setPrev(List<Integer> prev) {
        this.prev = prev;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
