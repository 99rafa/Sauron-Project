package pt.tecnico.sauron.silo.client;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.CamInfoResponse;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import static io.grpc.Status.NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CamInfoIT extends BaseIT {
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

        frontend.camJoin("Vale das Mos", 12.2, 12.2);
    }

    @AfterAll
    public static void oneTimeTearDown() {
        frontend.ctrlClear();
    }

    // initialization and clean-up for each test

    @BeforeEach
    public void setUp() {

    }

    @AfterEach
    public void tearDown() {

    }

    @Test
    public void camInfoFromExistingCam() {
        String camName = "Vale das Mos";

        CamInfoResponse response = frontend.getCamInfo(camName);

        assertEquals((Double) 12.2, (Double) response.getLatitude());
        assertEquals((Double) 12.2, (Double) response.getLongitude());


    }

    @Test
    public void camInfoFromNonExistingCam() {
        String camName = "Not Vale";

        assertEquals(
                NOT_FOUND.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.getCamInfo(camName))
                        .getStatus()
                        .getCode());
    }

}
