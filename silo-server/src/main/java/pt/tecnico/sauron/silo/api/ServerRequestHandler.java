package pt.tecnico.sauron.silo.api;

import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.exceptions.DuplicateOperationException;
import pt.tecnico.sauron.silo.grpc.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ServerRequestHandler {

    private Integer replicaNumber;

    private Map<Integer, Integer> replicaTS = new ConcurrentHashMap<>();

    private List<LogRecord> updateLog = new CopyOnWriteArrayList<>();

    private Map<Integer, Integer> valueTS = new ConcurrentHashMap<>();

    private List<String> executedOpsTable = new CopyOnWriteArrayList<>();

    private boolean logCleanable = true;


    public ServerRequestHandler(Integer replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    //handler to missing gossip for replicas which are currently down
    //log cannot be erased to recover from fault when replica becomes available again
    public void missedGossipHandler() {
        logCleanable = false;
    }

    //handler to successful gossip
    //log can be erased to
    public void successfulGossipHandler() {
        logCleanable = true;
    }

    //respond to an update request by the client
    public synchronized LogRecord processUpdateRequest(String op, ClientRequest request, StreamObserver<ClientResponse> responseObserver) throws DuplicateOperationException {


        //Sends Exception when operation Id is in the executed operations--> Protects duplicate requests
        if (isInExecutedUpdates(request.getOpId())) {
            throw new DuplicateOperationException();
        }

        increaseReplicaTS(this.replicaNumber);


        // timestamp associated with update is prevTS and the entry i associated with the current replica is = replicaTS[i]
        Map<Integer, Integer> updateTS = new HashMap<>(request.getPrevTSMap());
        updateTS.put(this.replicaNumber, this.replicaTS.get(this.replicaNumber));


        return new LogRecord(this.replicaNumber, updateTS, request.getPrevTSMap(), request.getOpId(), new Operation(op, request, responseObserver));

    }

    //add record to log
    public synchronized void addRecordToLog(LogRecord logRecord) {
        this.updateLog.add(logRecord);
    }

    //Get Stable updates
    public synchronized List<LogRecord> getStableUpdates() {

        //Filter and sort updates
        return this.updateLog.stream()
                .filter(update -> !executedOpsTable.contains(update.getId()))
                .sorted((l1, l2) -> happensBeforeInteger(l1.getPrevTS(), l2.getPrevTS()))
                .collect(Collectors.toList());
    }

    public synchronized void removeFromUpdateLog(LogRecord logRecord) {

        this.updateLog.remove(logRecord);
    }

    public synchronized void cleanUpdateLog() {

        if (logCleanable) this.updateLog.clear();
    }

    //apply updates to the replica
    public synchronized void updateReplicaState(LogRecord logRecord) {
        mergeTS(this.valueTS, logRecord.getTimestamp());
        this.executedOpsTable.add(logRecord.getId());

    }

    //merging the updates from gossip with the replica own pending updates
    public synchronized void mergeIncomingLog(GossipMessage g) {
        for (LogRecord r : g.getLog()) {

            if (happensBefore(this.replicaTS, r.getTimestamp()) && !this.replicaTS.equals(r.getTimestamp()))
                this.updateLog.add(r);
        }
        mergeTS(this.replicaTS, g.getRepTs());

    }

    //increase replica's timestamp by one
    public synchronized void increaseReplicaTS(Integer replicaNumber) {
        this.replicaTS.merge(replicaNumber, 1, Integer::sum);
    }


    //checks if update has already been done
    private synchronized boolean isInExecutedUpdates(String operationID) {
        return this.executedOpsTable.contains(operationID);
    }

    //merge 2 timestamps
    private synchronized void mergeTS(Map<Integer, Integer> map1, Map<Integer, Integer> map2) {
        for (Integer key : map2.keySet()) {
            if (map1.containsKey(key))
                map1.put(key, Integer.max(map1.get(key), map2.get(key)));
            else
                map1.put(key, map2.get(key));
        }
    }


    public GossipRequest buildGossipRequest() {
        GossipRequest.Builder gRequest = GossipRequest.newBuilder().putAllRepTs(this.replicaTS);
        for (LogRecord lr : this.updateLog) {
            Operation op = lr.getOperation();

            OperationRequest opRequest = OperationRequest.newBuilder()
                    .setRequest(op.getRequest())
                    .setOp(op.getOperation()).build();

            LogRecordsRequest lrRequest = LogRecordsRequest.newBuilder()
                    .setOperation(opRequest)
                    .setId(lr.getId())
                    .setRepN(lr.getRepN())
                    .putAllPrevTS(lr.getPrevTS())
                    .putAllTimestamp(lr.getTimestamp()).build();
            gRequest.addLog(lrRequest);
        }

        //clear update log after sending a gossip to all the replicas containing the update log
        cleanUpdateLog();

        return gRequest.build();
    }

    public Integer getReplicaNumber() {
        return replicaNumber;
    }

    public void setReplicaNumber(Integer replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public Map<Integer, Integer> getReplicaTS() {
        return replicaTS;
    }

    public void setReplicaTS(Map<Integer, Integer> replicaTS) {
        this.replicaTS = replicaTS;
    }

    public List<LogRecord> getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(List<LogRecord> updateLog) {
        this.updateLog = updateLog;
    }

    public Map<Integer, Integer> getValueTS() {
        return valueTS;
    }

    public void setValueTS(Map<Integer, Integer> valueTS) {
        this.valueTS = valueTS;
    }

    public List<String> getExecutedOpsTable() {
        return executedOpsTable;
    }

    public void setExecutedOpsTable(List<String> executedOpsTable) {
        this.executedOpsTable = executedOpsTable;
    }

    //checks if a happens before b
    private boolean happensBefore(Map<Integer, Integer> a, Map<Integer, Integer> b) {

        for (Map.Entry<Integer, Integer> entryA : a.entrySet()) {
            Integer valueB = b.getOrDefault(entryA.getKey(), 0);
            if (entryA.getValue() > valueB) return false;
        }
        return true;
    }

    private int happensBeforeInteger(Map<Integer, Integer> a, Map<Integer, Integer> b) {

        if (a.equals(b)) return 0;

        for (Map.Entry<Integer, Integer> entryA : a.entrySet()) {
            Integer valueB = b.getOrDefault(entryA.getKey(), 0);
            if (entryA.getValue() > valueB) return 1;
        }
        return -1;
    }


}
