package pt.tecnico.sauron.silo.client;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.client.Exceptions.NoServersAvailableException;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.util.ArrayList;
import java.util.List;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReportIT extends BaseIT {

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
    public void report3CorrectObservations() {
        String date = "2019-12-12 12:12:12";
        String camName = "Vale das Mos";
        String car = "CAR";
        String person = "PERSON";
        List<List<String>> observations = new ArrayList<>();


        List<String> observationMessage1 = new ArrayList<>();
        observationMessage1.add(person);
        observationMessage1.add("123");
        observationMessage1.add(date);

        List<String> observationMessage2 = new ArrayList<>();
        observationMessage2.add(car);
        observationMessage2.add("8709OA");
        observationMessage2.add(date);

        List<String> observationMessage3 = new ArrayList<>();
        observationMessage3.add(car);
        observationMessage3.add("1212AO");
        observationMessage3.add(date);


        observations.add(observationMessage1);
        observations.add(observationMessage2);
        observations.add(observationMessage3);

        frontend.reportObs(camName, observations);


    }

    @Test
    public void reportInvalidObsId() {
        String date = "2019-12-12 12:12:12";
        String camName = "Vale das Mos";
        String car = "CAR";
        String person = "PERSON";

        List<List<String>> observations1 = new ArrayList<>();
        List<List<String>> observations2 = new ArrayList<>();


        List<String> observationMessage1 = new ArrayList<>();
        observationMessage1.add(person);
        observationMessage1.add("123A");
        observationMessage1.add(date);

        List<String> observationMessage2 = new ArrayList<>();
        observationMessage2.add(car);
        observationMessage2.add("8709O");
        observationMessage2.add(date);


        observations1.add(observationMessage1);
        observations2.add(observationMessage2);


        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.reportObs(camName, observations1))
                        .getStatus()
                        .getCode());

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.reportObs(camName, observations2))
                        .getStatus()
                        .getCode());


    }

    @Test
    public void reportInvalidDate() {
        String date = "2021-12-12 12:12:12";
        String camName = "Vale das Mos";
        String person = "PERSON";

        List<List<String>> observations1 = new ArrayList<>();

        List<String> observationMessage1 = new ArrayList<>();
        observationMessage1.add(person);
        observationMessage1.add("123A");
        observationMessage1.add(date);

        observations1.add(observationMessage1);


        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.reportObs(camName, observations1))
                        .getStatus()
                        .getCode());
    }

    @Test
    public void reportNonExistingCamera() {
        String date = "2019-12-12 12:12:12";
        String camName = "NOT Mos";
        String person = "PERSON";


        List<List<String>> observations1 = new ArrayList<>();

        List<String> observationMessage1 = new ArrayList<>();
        observationMessage1.add(person);
        observationMessage1.add("123A");
        observationMessage1.add(date);

        observations1.add(observationMessage1);

        assertEquals(
                NOT_FOUND.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.reportObs(camName, observations1))
                        .getStatus()
                        .getCode());
    }
}
