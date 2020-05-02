package pt.tecnico.sauron.silo.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GossipMessage {
    private List<LogRecord> log;
    private Map<Integer, Integer> repTs = new ConcurrentHashMap<>();

    public GossipMessage() {
    }

    public GossipMessage(List<LogRecord> log, Map<Integer, Integer> repTs) {
        this.log = log;
        this.repTs = repTs;
    }

    public List<LogRecord> getLog() {
        return log;
    }

    public void setLog(List<LogRecord> log) {
        this.log = log;
    }

    public Map<Integer, Integer> getRepTs() {
        return repTs;
    }

    public void setRepTs(Map<Integer, Integer> repTs) {
        this.repTs = repTs;
    }
}
