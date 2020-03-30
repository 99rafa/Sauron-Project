package pt.tecnico.sauron.silo.client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import pt.tecnico.sauron.silo.grpc.ClearRequest;

public class PingIT extends BaseIT {

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
        frontend.clearObj(request);
    }

    @AfterEach
    public void tearDown() {

    }
}