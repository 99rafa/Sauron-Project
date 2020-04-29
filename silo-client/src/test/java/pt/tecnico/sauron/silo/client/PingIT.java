package pt.tecnico.sauron.silo.client;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.ClearRequest;
import pt.tecnico.sauron.silo.grpc.PingRequest;
import pt.tecnico.sauron.silo.grpc.PingResponse;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class PingIT extends BaseIT {

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
    public void pingOKTest() {
        PingResponse response = frontend.ctrlPing("friend");
        assertEquals("Hello friend!\nThe server is running!", response.getOutputText());
    }

    @Test
    public void emptyPingTest() {

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.ctrlPing(""))
                        .getStatus()
                        .getCode());
    }

}
