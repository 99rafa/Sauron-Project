package pt.tecnico.sauron.silo.client;

import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.client.Exceptions.NoServersAvailableException;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

public class InitIT extends BaseIT {

    static SiloFrontend frontend;

    static {
        try {
            frontend = new SiloFrontend("localhost", "2181", "");
        } catch (ZKNamingException | NoServersAvailableException e) {
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
    public void initTest() {
        frontend.ctrlInit();
    }

}
