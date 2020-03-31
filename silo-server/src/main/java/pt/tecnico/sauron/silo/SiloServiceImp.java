package pt.tecnico.sauron.silo;

import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Observation;
import pt.tecnico.sauron.silo.domain.Silo;
import pt.tecnico.sauron.silo.exceptions.ErrorMessage;
import pt.tecnico.sauron.silo.exceptions.SiloException;
import pt.tecnico.sauron.silo.grpc.*;

import java.time.LocalDateTime;
import java.util.List;

import static io.grpc.Status.ALREADY_EXISTS;
import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;


public class SiloServiceImp extends SiloOperationsServiceGrpc.SiloOperationsServiceImplBase {

    private Silo silo = new Silo();


    @Override
    public void camJoin(CamJoinRequest request, StreamObserver<CamJoinResponse> responseObserver) {

        try {

            Camera camera = new Camera(request.getCamName(), request.getLatitude(), request.getLongitude());
            silo.addCamera(camera);
            CamJoinResponse response = CamJoinResponse.newBuilder().build();

            // Send a single response through the stream.
            responseObserver.onNext(response);
            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();

        } catch (SiloException e) {
            if(e.getErrorMessage() == ErrorMessage.CAMERA_NAME_NOT_UNIQUE)
                responseObserver.onError(ALREADY_EXISTS.withDescription(e.getMessage()).asRuntimeException());
            if(e.getErrorMessage() == ErrorMessage.CAMERA_NAME_INVALID
                    || e.getErrorMessage() == ErrorMessage.CAMERA_NAME_NULL
                    || e.getErrorMessage() == ErrorMessage.COORDINATES_INVALID_LATITUDE
                    || e.getErrorMessage() == ErrorMessage.COORDINATES_INVALID_LONGITUDE
                    || e.getErrorMessage() == ErrorMessage.COORDINATES_NULL_LATITUDE
                    || e.getErrorMessage() == ErrorMessage.COORDINATES_NULL_LONGITUDE)
                responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }

    }

    @Override
    public void camInfo(CamInfoRequest request, StreamObserver<CamInfoResponse> responseObserver) {

        try {
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

        } catch (SiloException e) {
            if(e.getErrorMessage() == ErrorMessage.NO_SUCH_CAMERA_NAME)
                responseObserver.onError(NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
            if(e.getErrorMessage() == ErrorMessage.CAMERA_NAME_NULL)
                responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }

    }

    @Override
    public void track(TrackRequest request, StreamObserver<TrackResponse> responseObserver) {
        System.out.println(silo);
        try {

            Type type = request.getType();
            String id = request.getId();
            Observation result;


            if (type == Type.UNRECOGNIZED)
                throw new SiloException(ErrorMessage.OBJECT_INVALID_TYPE, type.toString());

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

            // Send a single response through the stream.
            responseObserver.onNext(response);

            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();

        } catch (SiloException e) {
            if(e.getErrorMessage() == ErrorMessage.OBSERVATION_NULL_ID
                || e.getErrorMessage() == ErrorMessage.OBJECT_NULL_TYPE)
                responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
            if(e.getErrorMessage() == ErrorMessage.NO_SUCH_OBJECT)
                responseObserver.onError(NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }

    }

    @Override
    public void trackMatch(TrackMatchRequest request, StreamObserver<TrackMatchResponse> responseObserver) {


        try {
            Type type = request.getType();
            String id = request.getSubId();
            List<Observation> result;
            TrackMatchResponse.Builder builder = TrackMatchResponse.newBuilder();

            if (type == Type.UNRECOGNIZED)
                throw new SiloException(ErrorMessage.OBJECT_INVALID_TYPE, type.toString());

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


            // Send a single response through the stream.
            responseObserver.onNext(response);

            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();

        } catch (SiloException e) {
            if(e.getErrorMessage() == ErrorMessage.OBJECT_NULL_ID
                    || e.getErrorMessage() == ErrorMessage.OBJECT_NULL_TYPE
                    || e.getErrorMessage() == ErrorMessage.OBJECT_INVALID_PART_ID)
                responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
            if(e.getErrorMessage() == ErrorMessage.NO_SUCH_OBJECT)
                responseObserver.onError(NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }

    }


    @Override
    public void trace(TraceRequest request, StreamObserver<TraceResponse> responseObserver) {

        try {

            Type type = request.getType();
            String id = request.getId();
            List<Observation> result;
            TraceResponse.Builder builder = TraceResponse.newBuilder();
            if (type == Type.UNRECOGNIZED)
                throw new SiloException(ErrorMessage.OBJECT_INVALID_TYPE, type.toString());

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


            // Send a single response through the stream.
            responseObserver.onNext(response);

            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();

        } catch (SiloException e) {
            if(e.getErrorMessage() == ErrorMessage.OBJECT_NULL_ID
                    || e.getErrorMessage() == ErrorMessage.OBJECT_NULL_TYPE)
                responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
            if(e.getErrorMessage() == ErrorMessage.NO_SUCH_OBJECT)
                responseObserver.onError(NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }

    }

    @Override
    public void report(ReportRequest request, StreamObserver<ReportResponse> responseObserver) {
        try {
            String camName = request.getCamName();
            List<ObservationMessage> observationMessages;

            if (silo.checkIfCameraExists(camName)) {

                Camera cam = silo.getCameraByName(camName);

                observationMessages = request.getObservationList();
                for (ObservationMessage om : observationMessages) {
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

            // Send a single response through the stream.
            responseObserver.onNext(response);

            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();

            System.out.println(silo);

        } catch (SiloException e) {
            if(e.getErrorMessage() == ErrorMessage.NO_SUCH_CAMERA_NAME)
                responseObserver.onError(NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
            if(e.getErrorMessage() == ErrorMessage.CAMERA_NAME_NULL
                    || e.getErrorMessage() == ErrorMessage.OBSERVATION_NULL_TYPE
                    || e.getErrorMessage() == ErrorMessage.OBSERVATION_NULL_ID
                    || e.getErrorMessage() == ErrorMessage.OBSERVATION_INVALID_DATE
                    || e.getErrorMessage() == ErrorMessage.OBSERVATION_INVALID_ID
                    || e.getErrorMessage() == ErrorMessage.OBSERVATION_NULL_DATE)
                responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }

    }

    @Override
    public void ctrlPing(PingRequest request, StreamObserver<PingResponse> responseObserver) {

        String inputText = request.getInputCommand();

        if (inputText == null || inputText.isBlank()) {
            responseObserver.onError(INVALID_ARGUMENT
                    .withDescription("Input cannot be empty!").asRuntimeException());
        }

        String output = "Hello " + inputText + "!\n" + "The server is running!";
        PingResponse response = PingResponse.newBuilder().setOutputText(output).build();

        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    }

    @Override
    public void ctrlClear(ClearRequest request, StreamObserver<ClearResponse> responseObserver) {

        //silo.clearData();
        ClearResponse response = ClearResponse.newBuilder().build();

        //Clears server info
        silo = new Silo();

        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    }

    @Override
    public void ctrlInit(InitRequest request, StreamObserver<InitResponse> responseObserver) {

        InitResponse response = InitResponse.newBuilder().build();

        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();


    }


}
