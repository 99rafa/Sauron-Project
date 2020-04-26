package pt.tecnico.sauron.silo;

import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.api.GossipMessage;
import pt.tecnico.sauron.silo.api.LogRecords;
import pt.tecnico.sauron.silo.api.Operation;
import pt.tecnico.sauron.silo.domain.*;
import pt.tecnico.sauron.silo.domain.Silo;
import pt.tecnico.sauron.silo.exceptions.*;
import pt.tecnico.sauron.silo.grpc.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.grpc.Status.ALREADY_EXISTS;
import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;


public class SiloServiceImp extends SiloOperationsServiceGrpc.SiloOperationsServiceImplBase {

    private Integer replicaNumber;

    private Silo silo = new Silo();

    private Map<Integer, Integer> replicaTS = new ConcurrentHashMap<>();

    private List<LogRecords> updateLog = new CopyOnWriteArrayList<>();

    private Map<Integer, Integer> valueTS = new ConcurrentHashMap<>();

    private List<String> executedOpsTable = new CopyOnWriteArrayList<>();

    private List<Operation> pendingQueries = new ArrayList<>();


    public SiloServiceImp(Integer repN) {
        this.replicaNumber = repN;
    }


    public void camJoinAux(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {

        try {

            Camera camera = new Camera(request.getCamJoinRequest().getCamName(), request.getCamJoinRequest().getLatitude(), request.getCamJoinRequest().getLongitude());
            silo.addCamera(camera);
            CamJoinResponse response = CamJoinResponse.newBuilder().build();

            ClientResponse clientResponse = ClientResponse.newBuilder().setCamJoinResponse(response).build();

            // Send a single response through the stream.
            responseObserver.onNext(clientResponse);
            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();

        } catch ( CameraNameNotUniqueException e){
            responseObserver.onError(ALREADY_EXISTS.withDescription(e.getMessage()).asRuntimeException());
        } catch ( CameraNameInvalidException  |
                  CameraNameNullException     |
                  InvalidCoordinatesException e ) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void camJoin(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {
        processUpdateRequest("CamJoin", request, responseObserver);
    }

    public void camInfoAux(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {

        try {
            String camName = request.getCamInfoRequest().getCamName();
            Camera camera = silo.getCameraByName(camName);

            CamInfoResponse response = CamInfoResponse.newBuilder()
                    .setLatitude(camera.getLat())
                    .setLongitude(camera.getLog())
                    .build();

            ClientResponse clientResponse = ClientResponse.newBuilder().setCamInfoResponse(response).build();

            // Send a single response through the stream.
            responseObserver.onNext(clientResponse);

            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();

        } catch ( NoSuchCameraNameException e ){
            responseObserver.onError(NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch ( CameraNameNullException e ){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }


    }

    @Override
    public void camInfo(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {
        processReadRequest("CamInfo", request, responseObserver);

    }


    public void trackAux(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {

        try {


            String type = request.getTrackRequest().getType();
            checkType(type);

            String id = request.getTrackRequest().getId();

            Observation result;

            result = silo.trackObject(type, id);

            //Build Observation Message
            ObservationMessage observationMessage = ObservationMessage.newBuilder()
                    .setId(result.getId())
                    .setType(result.getType())
                    .setDatetime(result.getDateTime().format(Silo.formatter))
                    .setCamName(result.getCamName())
                    .build();

            TrackResponse response = TrackResponse.newBuilder()
                    .setObservation(observationMessage)
                    .build();

            System.out.println("Sending most recent observation of object with id:" + id + " and type:" +type + "..." );

            ClientResponse clientResponse = ClientResponse.newBuilder().setTrackResponse(response).build();


            // Send a single response through the stream.
            responseObserver.onNext(clientResponse);

            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();

        } catch( InvalidIdException   |
                 InvalidTypeException e ){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch ( NoSuchObjectException e ){
            responseObserver.onError(NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }

    }

    @Override
    public void track(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {
        processReadRequest("Track", request, responseObserver);

    }


    public void trackMatchAux(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {


        try {


            String type = request.getTrackMatchRequest().getType();
            checkType(type);

            String id = request.getTrackMatchRequest().getSubId();
            List<Observation> result;
            TrackMatchResponse.Builder builder = TrackMatchResponse.newBuilder();


            result = silo.trackMatchObject(type, id);

            for (Observation o : result) {
                //Build Observation Message
                ObservationMessage observationMessage = ObservationMessage.newBuilder()
                        .setId(o.getId())
                        .setType(o.getType())
                        .setDatetime(o.getDateTime().format(Silo.formatter))
                        .setCamName(o.getCamName())
                        .build();

                builder.addObservation(observationMessage);
            }


            TrackMatchResponse response = builder.build();

            System.out.println("Sending most recent observations of objects with partialid:" + id + " and type:" +type + "...");

            ClientResponse clientResponse = ClientResponse.newBuilder().setTrackMatchResponse(response).build();


            // Send a single response through the stream.
            responseObserver.onNext(clientResponse);

            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();

        } catch ( InvalidTypeException   |
                  InvalidIdException     e ){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch ( NoSuchObjectException e ){
            responseObserver.onError(NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }

    }

    @Override
    public void trackMatch(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {
        processReadRequest("TrackMatch", request, responseObserver);

    }


    public void traceAux(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {

        try {

            String type = request.getTraceRequest().getType();
            checkType(type);

            String id = request.getTraceRequest().getId();
            List<Observation> result;
            TraceResponse.Builder builder = TraceResponse.newBuilder();

            result = silo.traceObject(type, id);

            for (Observation o : result) {
                //Build Observation Message
                ObservationMessage observationMessage = ObservationMessage.newBuilder()
                        .setId(o.getId())
                        .setType(o.getType())
                        .setDatetime(o.getDateTime().format(Silo.formatter))
                        .setCamName(o.getCamName())
                        .build();

                builder.addObservation(observationMessage);
            }


            TraceResponse response = builder.build();


            System.out.println("Sending trace path of object with id:" + id + " and type:" +type + "...");

            ClientResponse clientResponse = ClientResponse.newBuilder().setTraceResponse(response).build();

            // Send a single response through the stream.
            responseObserver.onNext(clientResponse);

            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();

        } catch ( InvalidIdException   |
                  InvalidTypeException e ){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch ( NoSuchObjectException e){
            responseObserver.onError(NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }


    }

    @Override
    public void trace(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {
        processReadRequest("Trace", request, responseObserver);

    }



    public void reportAux(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {
        try {

            String camName = request.getReportRequest().getCamName();
            List<ObservationMessage> observationMessages;

            if (silo.checkIfCameraExists(camName)) {

                Camera cam = silo.getCameraByName(camName);

                observationMessages = request.getReportRequest().getObservationList();
                for (ObservationMessage om : observationMessages) {
                    checkType(om.getType());
                    cam.addObservation(new Observation(om.getType()
                            , om.getId()
                            , LocalDateTime.parse(om.getDatetime(), Silo.formatter)
                            , camName
                    ));
                }
            }
            else{
                responseObserver.onError(NOT_FOUND.withDescription("No such camera").asRuntimeException());
                return;
            }

            ReportResponse response = ReportResponse.newBuilder().build();

            ClientResponse clientResponse = ClientResponse.newBuilder().setReportResponse(response).build();


            // Send a single response through the stream.
            responseObserver.onNext(clientResponse);

            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();

        } catch ( NoSuchCameraNameException e ){
            responseObserver.onError(NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch ( CameraNameNullException  |
                  InvalidTypeException     |
                  InvalidIdException       |
                  InvalidDateException     e){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }

    }

    @Override
    public void report(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {
        processUpdateRequest("Report",request, responseObserver);

    }

    public void ctrlPingAux(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {

        String inputText = request.getPingRequest().getInputCommand();

        if (inputText == null || inputText.isBlank()) {
            responseObserver.onError(INVALID_ARGUMENT
                    .withDescription("Input cannot be empty!").asRuntimeException());
            return;
        }

        String output = "Hello " + inputText + "!\n" + "The server is running!";
        PingResponse response = PingResponse.newBuilder().setOutputText(output).build();
        System.out.println("Ping request received");

        ClientResponse clientResponse = ClientResponse.newBuilder().setPingResponse(response).build();


        // Send a single response through the stream.
        responseObserver.onNext(clientResponse);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    }

    @Override
    public void ctrlPing(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {
        processReadRequest("Ping" ,request, responseObserver);

    }


    @Override
    public void ctrlClear(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {

        //silo.clearData();
        ClearResponse response = ClearResponse.newBuilder().build();

        //Clears server info
        silo = new Silo();
        System.out.println("System state cleared");

        ClientResponse clientResponse = ClientResponse.newBuilder().setClearResponse(response).build();

        // Send a single response through the stream.
        responseObserver.onNext(clientResponse);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    }

    @Override
    public void ctrlInit(ClientRequest request, StreamObserver<ClientResponse> responseObserver) {

        InitResponse response = InitResponse.newBuilder().build();

        ClientResponse clientResponse = ClientResponse.newBuilder().setInitResponse(response).build();


        // Send a single response through the stream.
        responseObserver.onNext(clientResponse);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();


    }


    //respond to an update request by the client
    public synchronized void processUpdateRequest(String op,ClientRequest request, StreamObserver<ClientResponse> responseObserver) {

        if (isInExecutedUpdates(request.getOpId())) { /*TODO: o que fazer neste caso?*/}

        increaseReplicaTS(this.replicaNumber);

        // timestamp associated with update is prevTS and the entry i associated with the current replica is = replicaTS[i]
        Map<Integer,Integer> updateTS = request.getPrevTSMap();
        updateTS.put(this.replicaNumber, this.replicaTS.get(this.replicaNumber));

        LogRecords logRecord = new LogRecords(this.replicaNumber, updateTS,request.getPrevTSMap(),request.getOpId(), new Operation(op,request,responseObserver));

        updateLog.add(logRecord);


    }


    //respond to a read request by the client
    public synchronized boolean processReadRequest(String operation,ClientRequest request, StreamObserver<ClientResponse> responseObserver) {
        if (happensBefore(request.getPrevTSMap(), this.valueTS)) {
            return true;
        }
        else {
            this.pendingQueries.add(new Operation(operation,request,responseObserver));
            return false;
        }
    }

    public synchronized void updateReplicaState() {

    }

    public synchronized void mergeIncomingLog(GossipMessage g) {
        for ( LogRecords r: g.getLog()) {
            if (happensBefore(this.replicaTS,r.getTimestamp()) && !this.replicaTS.equals(r.getTimestamp())) this.updateLog.add(r);
                /*TODO: equals nao sei se o equals faz sentido tendo em conta os maps podem ser diferentes*/
        }
    }

    public synchronized void increaseReplicaTS(Integer replicaNumber) {
        this.replicaTS.merge(replicaNumber, 1, Integer::sum);
    }


    //checks if update has already been done
    private synchronized boolean isInExecutedUpdates(String operationID) {
        return !this.executedOpsTable.contains(operationID);
    }

    //checks if a happens before b
    private boolean happensBefore(Map<Integer, Integer> a, Map<Integer, Integer> b) {

        boolean isBefore = true;
        for (Map.Entry<Integer, Integer> entryA : a.entrySet()) {
            Integer valueB = b.getOrDefault(entryA.getKey(), 0);
            if ( entryA.getValue() > valueB) isBefore = false;
        }
        return isBefore;
    }


    //Checks if type is valid
    private void checkType(String type){

          if(type == null || type.strip().length() == 0) {
              throw new InvalidTypeException();
          }
          if( !type.equals("PERSON") &&
              !type.equals("CAR") ){
              throw new InvalidTypeException(type);
        }
    }

    public Silo getSilo() {
        return silo;
    }

    public void setSilo(Silo silo) {
        this.silo = silo;
    }

}
