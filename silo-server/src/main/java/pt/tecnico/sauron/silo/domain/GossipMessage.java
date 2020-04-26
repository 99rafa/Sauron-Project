package pt.tecnico.sauron.silo.domain;

import java.util.ArrayList;
import java.util.List;

public class GossipMessage {
    private LogRecords log;
    private List<Integer> repTs = new ArrayList<>();

    public GossipMessage() {
    }

    public GossipMessage(LogRecords log, List<Integer> repTs) {
        this.log = log;
        this.repTs = repTs;
    }

    public LogRecords getLog() {
        return log;
    }

    public void setLog(LogRecords log) {
        this.log = log;
    }

    public List<Integer> getRepTs() {
        return repTs;
    }

    public void setRepTs(List<Integer> repTs) {
        this.repTs = repTs;
    }
}
