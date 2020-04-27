package pt.tecnico.sauron.silo.api;

import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.exceptions.DuplicateOperationException;
import pt.tecnico.sauron.silo.grpc.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerRequestHandler {

    private Integer replicaNumber;

    private Map<Integer, Integer> replicaTS = new ConcurrentHashMap<>();

    private List<LogRecords> updateLog = new CopyOnWriteArrayList<>();

    private Map<Integer, Integer> valueTS = new ConcurrentHashMap<>();

    private List<String> executedOpsTable = new CopyOnWriteArrayList<>();


    public ServerRequestHandler(Integer replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    //respond to an update request by the client
    public synchronized LogRecords processUpdateRequest(String op, ClientRequest request, StreamObserver<ClientResponse> responseObserver) {


        //Sends Exception when operation Id is in the executed operations--> Protects duplicate requests
        if (isInExecutedUpdates(request.getOpId())) {
            throw new DuplicateOperationException();
        }

        increaseReplicaTS(this.replicaNumber);

        // timestamp associated with update is prevTS and the entry i associated with the current replica is = replicaTS[i]
        Map<Integer, Integer> updateTS = new HashMap<>(request.getPrevTSMap());
        updateTS.put(this.replicaNumber, this.replicaTS.get(this.replicaNumber));

        LogRecords logRecord = new LogRecords(this.replicaNumber, updateTS, request.getPrevTSMap(), request.getOpId(), new Operation(op, request, responseObserver));

        updateLog.add(logRecord);

        return logRecord;

    }

    //processing update log after receiving gossip message
    public synchronized void processLog() {
        //sorting list by timestamp

        //TODO:sacar apenas os que tem prevTS <= valueTS
        this.updateLog.sort((l1, l2) -> happensBeforeInteger(l1.getPrevTS(), l2.getPrevTS()));
        //TODO:fazer os updates
        //TODO:update replica state
    }

    //apply updates to the replica
    public synchronized void updateReplicaState(LogRecords logRecord) {
        mergeTS(this.valueTS, logRecord.getTimestamp());
        this.executedOpsTable.add(logRecord.getId());

    }

    //merging the updates from gossip with the replica own pending updates
    public synchronized void mergeIncomingLog(GossipMessage g) {
        for (LogRecords r : g.getLog()) {
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
        for (LogRecords lr : this.updateLog) {
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

    public List<LogRecords> getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(List<LogRecords> updateLog) {
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

        for (Map.Entry<Integer, Integer> entryA : a.entrySet()) {
            Integer valueB = b.getOrDefault(entryA.getKey(), 0);
            if (entryA.getValue() > valueB) return -1;
        }
        return 1;
    }


}
