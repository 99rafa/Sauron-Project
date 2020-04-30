package pt.tecnico.sauron.silo.client;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
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
        frontend.ctrlClear();
    }

    // initialization and clean-up for each test

    @BeforeEach
    public void setUp() {
        frontend.ctrlClear();
    }

    @AfterEach
    public void tearDown() {

    }

    @Test
    public void joinNonUniqueCamera() {
        String camName = "Vale das Mos";

        frontend.camJoin(camName, 13.3, 51.2);

    }

    @Test
    public void join2CamerasWithSameNameDifferentLocation() {
        String camName = "Vale das Mos";
        double lat1 = 13.2;
        double lat2 = 11.2;
        double log = 31.2;

        frontend.camJoin(camName, lat1, log);


        assertEquals(
                ALREADY_EXISTS.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.camJoin(camName, lat2, log))
                        .getStatus()
                        .getCode());

    }

    @Test
    public void join2CamerasWithSameNameSameLocation() {
        String camName = "Vale das Mos";
        double lat = 13.2;
        double log = 31.2;

        frontend.camJoin(camName, lat, log);

        frontend.camJoin(camName, lat, log);
    }


    @Test
    public void joinCameraWithMoreThan15CharsName() {
        String camName = "UMACOISARANDOMQUEMELEMBREIETEMMUITOMAISQUE15";
        double lat = 13.2;
        double log = 31.2;

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.camJoin(camName, lat, log))
                        .getStatus()
                        .getCode());
    }

    @Test
    public void joinCameraWithLessThan3CharsName() {
        String camName = "te";
        double lat = 13.2;
        double log = 31.2;

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.camJoin(camName, lat, log))
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
        ;

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.camJoin(camName, lat, log1))
                        .getStatus()
                        .getCode());

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.camJoin(camName, lat, log2))
                        .getStatus()
                        .getCode());

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.camJoin(camName, lat1, log))
                        .getStatus()
                        .getCode());

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.camJoin(camName, lat2, log))
                        .getStatus()
                        .getCode());


    }


}
