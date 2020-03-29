package pt.tecnico.sauron.silo;

import io.grpc.stub.StreamObserver;
import pt.tecnico.sauron.silo.domain.Silo;
import pt.tecnico.sauron.silo.grpc.CamInfoRequest;
import pt.tecnico.sauron.silo.grpc.CamInfoResponse;
import pt.tecnico.sauron.silo.grpc.CamJoinRequest;
import pt.tecnico.sauron.silo.grpc.CamJoinResponse;

public class SiloServiceImp {

    private Silo silo = new Silo();

    @Override
    public void joinCameraToSilo(CamJoinRequest request, StreamObserver<CamJoinResponse> responseObserver) {

        CamJoinResponse response = CamJoinResponse.newBuilder().build(); //TO DO

        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    }

    @Override
    public void getCameraInfo(CamInfoRequest request, StreamObserver<CamInfoResponse> responseObserver) {


        CamInfoResponse response = CamInfoResponse.newBuilder().build(); //TO DO

        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    }
}
