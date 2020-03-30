package pt.tecnico.sauron.silo.client;

import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.ClearRequest;

public class CamInfoIT extends BaseIT {
    SiloFrontend frontend = new SiloFrontend("localhost", 8080);


    // one-time initialization and clean-up
    @BeforeAll
    public static void oneTimeSetUp(){

    }

    @AfterAll
    public static void oneTimeTearDown() {

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
    public void camInfoFromExistingCam(){

    }

    @Test
    public void camInfoFromNonExistingCam(){

    }

}
