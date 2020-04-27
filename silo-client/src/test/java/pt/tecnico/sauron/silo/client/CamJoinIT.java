package pt.tecnico.sauron.silo.client;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.CamJoinRequest;
import pt.tecnico.sauron.silo.grpc.CamJoinResponse;
import pt.tecnico.sauron.silo.grpc.ClearRequest;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import static io.grpc.Status.ALREADY_EXISTS;
import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CamJoinIT extends BaseIT {

    static SiloFrontend frontend;

    static {
        try {
            frontend = new SiloFrontend("localhost", "2181", "");
        } catch (ZKNamingException e) {
            e.printStackTrace();
        }
    }


    // one-time initialization and clean-up
    @BeforeAll
    public static void oneTimeSetUp() {

    }

    @AfterAll
    public static void oneTimeTearDown() {
        ClearRequest clearRequest = ClearRequest.newBuilder().build();
        frontend.ctrlClear(clearRequest);
    }

    // initialization and clean-up for each test

    @BeforeEach
    public void setUp() {
        ClearRequest request = ClearRequest.newBuilder().build();
        frontend.ctrlClear(request);
    }

    @AfterEach
    public void tearDown() {

    }

    @Test
    public void joinNonUniqueCamera() {
        String camName = "Vale das Mos";

        CamJoinRequest request = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(13.3).setLongitude(51.2).build();
        CamJoinResponse response = frontend.camJoin(request);

    }

    @Test
    public void join2CamerasWithSameNameDifferentLocation() {
        String camName = "Vale das Mos";
        double lat1 = 13.2;
        double lat2 = 11.2;
        double log = 31.2;

        CamJoinRequest request = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(lat1).setLongitude(log).build();
        frontend.camJoin(request);
        CamJoinRequest request2 = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(lat2).setLongitude(log).build();


        assertEquals(
                ALREADY_EXISTS.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.camJoin(request2))
                        .getStatus()
                        .getCode());

    }

    @Test
    public void join2CamerasWithSameNameSameLocation() {
        String camName = "Vale das Mos";
        double lat = 13.2;
        double log = 31.2;

        CamJoinRequest request = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(lat).setLongitude(log).build();
        frontend.camJoin(request);
        CamJoinRequest request2 = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(lat).setLongitude(log).build();
        frontend.camJoin(request2);
    }


    @Test
    public void joinCameraWithMoreThan15CharsName() {
        String camName = "UMACOISARANDOMQUEMELEMBREIETEMMUITOMAISQUE15";
        double lat = 13.2;
        double log = 31.2;

        CamJoinRequest request = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(lat).setLongitude(log).build();
        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.camJoin(request))
                        .getStatus()
                        .getCode());
    }

    @Test
    public void joinCameraWithLessThan3CharsName() {
        String camName = "te";
        double lat = 13.2;
        double log = 31.2;

        CamJoinRequest request = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(lat).setLongitude(log).build();
        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.camJoin(request))
                        .getStatus()
                        .getCode());
    }

    @Test
    public void joinCameraWithInvalidCoords() {
        String camName = "Vale das Mos";
        double log1 = 200.2;
        double log2 = -10.2;
        double lat1 = -100.2;
        double lat2 = 100.0;
        double lat = 13.2;
        double log = 31.2;

        CamJoinRequest request1 = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(lat).setLongitude(log1).build();
        CamJoinRequest request2 = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(lat).setLongitude(log2).build();
        CamJoinRequest request3 = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(lat1).setLongitude(log).build();
        CamJoinRequest request4 = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(lat2).setLongitude(log).build();

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.camJoin(request1))
                        .getStatus()
                        .getCode());

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.camJoin(request2))
                        .getStatus()
                        .getCode());

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.camJoin(request3))
                        .getStatus()
                        .getCode());

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.camJoin(request4))
                        .getStatus()
                        .getCode());


    }


}
