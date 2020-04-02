package pt.tecnico.sauron.silo.client;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.ClearRequest;
import pt.tecnico.sauron.silo.grpc.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static io.grpc.Status.INVALID_ARGUMENT;



public class PingIT extends BaseIT {

    static SiloFrontend frontend = new SiloFrontend("localhost", 8080);


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

    }

    @AfterEach
    public void tearDown() {

    }

    @Test
    public void pingOKTest() {
        PingRequest request = PingRequest.newBuilder().setInputCommand("friend").build();
        PingResponse response = frontend.ctrlPing(request);
        assertEquals("Hello friend!\nThe server is running!", response.getOutputText());
    }

    @Test
    public void emptyPingTest() {
        PingRequest request = PingRequest.newBuilder().setInputCommand("").build();
        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.ctrlPing(request))
                        .getStatus()
                        .getCode());
    }

}
