package pt.tecnico.sauron.silo;

import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Observation;
import pt.tecnico.sauron.silo.domain.Silo;
import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
import pt.tecnico.sauron.silo.exceptions.SiloException;
import pt.tecnico.sauron.silo.grpc.*;


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


        CamInfoResponse response = CamInfoResponse.newBuilder().build(); //TO DO

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


    }
}
