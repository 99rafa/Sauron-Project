package pt.tecnico.sauron.silo.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GossipMessage {
    private List<LogRecords> log;
    private Map<Integer, Integer> repTs = new ConcurrentHashMap<>();

    public GossipMessage() {
    }

    public GossipMessage(List<LogRecords> log, Map<Integer, Integer> repTs) {
        this.log = log;
        this.repTs = repTs;
    }

    public List<LogRecords> getLog() {
        return log;
    }

    public void setLog(List<LogRecords> log) {
        this.log = log;
    }

    public Map<Integer, Integer> getRepTs() {
        return repTs;
    }

    public void setRepTs(Map<Integer, Integer> repTs) {
        this.repTs = repTs;
    }
}
