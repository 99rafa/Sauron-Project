package pt.tecnico.sauron.silo;

import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.domain.Camera;
import pt.tecnico.sauron.silo.domain.Silo;
import pt.tecnico.sauron.silo.grpc.*;

public class SiloServiceImp extends SiloOperationsServiceGrpc.SiloOperationsServiceImplBase  {

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

        Camera camera = silo.getCameraInfo(request.getCamName());
        CamInfoResponse response = CamInfoResponse.newBuilder().setLatitude(camera.get_lat()).setLongitude(camera.get_log()).build();

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

        ClearResponse response = ClearResponse.newBuilder().setStatus(ServerStatus.RUNNING).build();

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



}
