package pt.tecnico.sauron.silo.client;

import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.ClearRequest;
import pt.tecnico.sauron.silo.grpc.ClearResponse;
import pt.tecnico.sauron.silo.grpc.InitRequest;
import pt.tecnico.sauron.silo.grpc.InitResponse;

public class InitIT extends BaseIT {

    static SiloFrontend frontend = new SiloFrontend("localhost", 8080);


    // one-time initialization and clean-up
    @BeforeAll
    public static void oneTimeSetUp(){

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
    public void initTest() {
        InitRequest request = InitRequest.newBuilder().build();
        frontend.ctrlInit(request);
    }

}
