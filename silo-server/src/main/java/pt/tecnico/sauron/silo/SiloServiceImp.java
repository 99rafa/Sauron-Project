package pt.tecnico.sauron.silo;

import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Observation;
import pt.tecnico.sauron.silo.domain.Silo;
import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
import pt.tecnico.sauron.silo.exceptions.SiloException;
import pt.tecnico.sauron.silo.grpc.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class SiloServiceImp extends SiloOperationsServiceGrpc.SiloOperationsServiceImplBase {

    private Silo silo = new Silo();


    @Override
    public void camJoin(CamJoinRequest request, StreamObserver<CamJoinResponse> responseObserver) {

        Camera camera = new Camera(request.getCamName(), request.getLatitude(), request.getLongitude());
        silo.addCamera(camera);
        CamJoinResponse response = CamJoinResponse.newBuilder().build();

        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    }

    @Override
    public void camInfo(CamInfoRequest request, StreamObserver<CamInfoResponse> responseObserver) {


        String camName = request.getCamName();
        Camera camera = silo.getCameraByName(camName);

        CamInfoResponse response = CamInfoResponse.newBuilder()
                .setLatitude(camera.getLat())
                .setLongitude(camera.getLog())
                .build();

        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();

    }

    @Override
    public void track(TrackRequest request, StreamObserver<TrackResponse> responseObserver) {

        Type type = request.getType();
        String id = request.getId();
        Observation result;



        if (type == Type.UNRECOGNIZED)
            throw new SiloException(ErrorMessage.OBSERVATION_INVALID_TYPE, type.toString());

        result = silo.trackObject(type, id);

        //Build Observation Message
        ObservationMessage observationMessage = ObservationMessage.newBuilder()
                .setId(result.getId())
                .setType(result.getType())
                .setDatetime(result.getDateTime().format(Silo.formatter))
                .build();

        TrackResponse response = TrackResponse.newBuilder()
                .setObservation(observationMessage)
                .build();

        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();

    }

    @Override
    public void trackMatch(TrackMatchRequest request, StreamObserver<TrackMatchResponse> responseObserver){

        Type type = request.getType();
        String partialId = request.getSubId();
        Observation result;

        if (type == Type.UNRECOGNIZED)
            throw new SiloException(ErrorMessage.OBSERVATION_INVALID_TYPE, type.toString());

        result = silo.trackMatchObject(type, partialId);

        //Build Observation Message
        ObservationMessage observationMessage = ObservationMessage.newBuilder()
                .setId(result.getId())
                .setType(result.getType())
                .setDatetime(result.getDateTime().format(Silo.formatter))
                .build();

        TrackMatchResponse response = TrackMatchResponse.newBuilder()
                .setObservation(observationMessage)
                .build();


        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();

    }


    @Override
    public void trace(TraceRequest request, StreamObserver<TraceResponse> responseObserver){

        Type type = request.getType();
        String id = request.getId();
        List<Observation> result;
        TraceResponse.Builder builder = TraceResponse.newBuilder();

        if (type == Type.UNRECOGNIZED)
            throw new SiloException(ErrorMessage.OBSERVATION_INVALID_TYPE, type.toString());

        result = silo.traceObject(type, id);

        int i = 0;
        for(Observation o: result) {
            //Build Observation Message
            ObservationMessage observationMessage = ObservationMessage.newBuilder()
                    .setId(o.getId())
                    .setType(o.getType())
                    .setDatetime(o.getDateTime().format(Silo.formatter))
                    .build();

            builder.setObservation(i,observationMessage);
            i++;
        }


        TraceResponse response = builder.build();


        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();

    }

    @Override
    public void report(ReportRequest request, StreamObserver<ReportResponse> responseObserver){

        String camName = request.getCamName();
        List<ObservationMessage> observationMessages = new ArrayList<>();

        if(silo.checkIfCameraExists(camName)){

            Camera cam = silo.getCameraByName(camName);

            observationMessages = request.getObservationList();

            for(ObservationMessage om : observationMessages)
                cam.addObservation(new Observation(om.getType()
                        ,om.getId()
                        ,LocalDateTime.parse(om.getDatetime(),Silo.formatter)
                ));

        }
        System.out.println(silo.getCameras().get(0).getObservations().toString());
        ReportResponse response = ReportResponse.newBuilder().build();

        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();


    }

    @Override
    public void ctrlPing(PingRequest request, StreamObserver<PingResponse> responseObserver) {

        String input = request.getInputCommand();
        PingResponse response = PingResponse.newBuilder().setStatus(ServerStatus.RUNNING).build();

        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    }

    @Override
    public void ctrlClear(ClearRequest request, StreamObserver<ClearResponse> responseObserver) {

        //silo.clearData();
        ClearResponse response = ClearResponse.newBuilder().build();
        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    }

    @Override
    public void ctrlInit(InitRequest request, StreamObserver<InitResponse> responseObserver) {


        //falta aqui a funcao para o porto
        InitResponse response = InitResponse.newBuilder().build();


    }



}
