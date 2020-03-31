package pt.tecnico.sauron.silo.client;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.CamInfoRequest;
import pt.tecnico.sauron.silo.grpc.CamJoinRequest;

import static io.grpc.Status.NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CamInfoIT extends BaseIT {
    static SiloFrontend frontend = new SiloFrontend("localhost", 8080);


    // one-time initialization and clean-up
    @BeforeAll
    public static void oneTimeSetUp(){
        CamJoinRequest camJoinRequest = CamJoinRequest.newBuilder()
                .setCamName("Vale das Mos")
                .setLatitude(12.2)
                .setLongitude(12.2).build();

        frontend.camJoin(camJoinRequest);
    }

    @AfterAll
    public static void oneTimeTearDown() {

    }

    // initialization and clean-up for each test

    @BeforeEach
    public void setUp() {

}

    @AfterEach
    public void tearDown() {

    }

    @Test
    public void camInfoFromExistingCam(){
        String camName = "Vale das Mos";
        CamInfoRequest camInfoRequest = CamInfoRequest.newBuilder().setCamName(camName).build();

        frontend.getCamInfo(camInfoRequest);


    }

    @Test
    public void camInfoFromNonExistingCam(){
        String camName = "Not Vale";
        CamInfoRequest request = CamInfoRequest.newBuilder().setCamName(camName).build();

        assertEquals(
                NOT_FOUND.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.getCamInfo(request))
                        .getStatus()
                        .getCode());
    }

}
