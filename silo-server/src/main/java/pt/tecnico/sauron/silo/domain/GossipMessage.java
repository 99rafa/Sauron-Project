package pt.tecnico.sauron.silo.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GossipMessage {
    private LogRecords log;
    private Map<Integer, Integer> repTs = new ConcurrentHashMap<Integer, Integer>();

    public GossipMessage() {
    }

    public GossipMessage(LogRecords log, Map<Integer, Integer> repTs) {
        this.log = log;
        this.repTs = repTs;
    }

    public LogRecords getLog() {
        return log;
    }

    public void setLog(LogRecords log) {
        this.log = log;
    }

    public Map<Integer, Integer> getRepTs() {
        return repTs;
    }

    public void setRepTs(Map<Integer, Integer> repTs) {
        this.repTs = repTs;
    }
}
