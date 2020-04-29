package pt.tecnico.sauron.silo.client;

import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.ClearRequest;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

public class ClearIT extends BaseIT {

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
    public void clearTest() {
        frontend.ctrlClear();
    }
}
