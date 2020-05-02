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

    //replicas down on previous gossip
    private List<String> missingReplicas = new CopyOnWriteArrayList<>();

    //log sent to replicas previously down
    private GossipRequest.Builder backupGossip = GossipRequest.newBuilder();


    public ServerRequestHandler(Integer replicaNumber) {
        this.replicaNumber = replicaNumber;
    }


    //handler to successful gossip
    //log can be erased
    public void successfulGossipHandler() {
        //clear backUp log after sending a gossip to all the replicas containing the update log
        cleanBackupGossip();
        cleanUpdateLog();
        this.missingReplicas.clear();
    }

    public void missedGossipHandler(List<String> missingReplicas) {
        //clear backUp log after sending a gossip to all the replicas containing the update log
        cleanUpdateLog();
        this.missingReplicas = missingReplicas;
    }

    //respond to an update request by the client
    public synchronized LogRecord processUpdateRequest(String op, ClientRequest request, StreamObserver<ClientResponse> responseObserver) throws DuplicateOperationException {


        //Sends Exception when operation Id is in the executed operations --> Protects duplicate requests
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

        this.updateLog.clear();
    }

    public synchronized void cleanBackupGossip() {

        this.backupGossip.clear();
    }

    //apply updates to the replica
    public synchronized void updateReplicaState(LogRecord logRecord) {

        mergeTS(this.valueTS, logRecord.getTimestamp());
        this.executedOpsTable.add(logRecord.getId());

    }

    //merging the updates from gossip with the replica own pending updates
    public synchronized void mergeIncomingLog(GossipMessage g) {
        for (LogRecord r : g.getLog()) {

            if (!happensBefore(r.getTimestamp(),this.replicaTS) && !this.replicaTS.equals(r.getTimestamp()))
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

            this.backupGossip.addLog(lrRequest);
        }
        this.backupGossip.putAllRepTs(this.replicaTS);
        return gRequest.build();
    }

    public Map<Integer, Integer> getValueTS() {
        return valueTS;
    }

    public List<String> getMissingReplicas() {
        return missingReplicas;
    }

    public GossipRequest.Builder getBackupGossip() {
        return backupGossip;
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
