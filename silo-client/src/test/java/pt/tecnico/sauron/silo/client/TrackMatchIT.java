package pt.tecnico.sauron.silo.client;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.client.Exceptions.NoServersAvailableException;
import pt.tecnico.sauron.silo.grpc.TraceResponse;
import pt.tecnico.sauron.silo.grpc.TrackRequest;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.util.ArrayList;
import java.util.List;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrackMatchIT extends BaseIT {

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

        frontend.ctrlClear();

        String camName1 = "Vale das Mos";
        String camName2 = "Alcobaca";
        String id1 = "12DL12";
        String id2 = "12AR12";
        String id3 = "151212";
        String id4 = "1512345";
        String date1 = "1999-03-12 12:12:12";
        String date2 = "2020-03-12 12:12:12";
        String date3 = "2015-09-12 12:12:12";
        String date4 = "2010-09-12 12:12:12";


        frontend.camJoin(camName1, 13.3, 51.2);
        frontend.camJoin(camName2, 15.3, 53.2);

        List<List<String>> observations1 = new ArrayList<>();
        List<List<String>> observations2 = new ArrayList<>();
        List<List<String>> observations3 = new ArrayList<>();
        List<List<String>> observations4 = new ArrayList<>();


        List<String> observationMessage1 = new ArrayList<>();
        observationMessage1.add("CAR");
        observationMessage1.add(id1);
        observationMessage1.add(date1);

        List<String> observationMessage2 = new ArrayList<>();
        observationMessage2.add("CAR");
        observationMessage2.add(id2);
        observationMessage2.add(date2);

        List<String> observationMessage3 = new ArrayList<>();
        observationMessage3.add("PERSON");
        observationMessage3.add(id3);
        observationMessage3.add(date3);

        List<String> observationMessage4 = new ArrayList<>();
        observationMessage4.add("PERSON");
        observationMessage4.add(id4);
        observationMessage4.add(date4);


        observations1.add(observationMessage1);
        observations2.add(observationMessage2);
        observations3.add(observationMessage3);
        observations4.add(observationMessage4);

        frontend.reportObs(camName1, observations1);
        frontend.reportObs(camName2, observations2);
        frontend.reportObs(camName1, observations3);
        frontend.reportObs(camName2, observations4);
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
    //correct trackMatch 2 object that start with id 12*
    public void trackMatchObjectRegular() {
        String type = "CAR";
        String subId = "12*";

        TraceResponse response = frontend.trackMatchObj(type, subId);

        assertEquals(2, response.getObservationList().size());

        assertEquals("12AR12", response.getObservationList().get(0).getId());
        assertEquals("Alcobaca", response.getObservationList().get(0).getCamName());
        assertEquals("CAR", response.getObservationList().get(0).getType());
        assertEquals("2020-03-12 12:12:12", response.getObservationList().get(0).getDatetime());

        assertEquals("12DL12", response.getObservationList().get(1).getId());
        assertEquals("Vale das Mos", response.getObservationList().get(1).getCamName());
        assertEquals("CAR", response.getObservationList().get(1).getType());
        assertEquals("1999-03-12 12:12:12", response.getObservationList().get(1).getDatetime());

    }


    @Test
    //correct trackMatch 2 persons that start with id 12*
    public void trackMatchPersonRegular() {
        String type = "PERSON";
        String subId = "15*";

        TraceResponse response = frontend.trackMatchObj(type, subId);

        assertEquals(2, response.getObservationList().size());

        assertEquals("151212", response.getObservationList().get(0).getId());
        assertEquals("Vale das Mos", response.getObservationList().get(0).getCamName());
        assertEquals("PERSON", response.getObservationList().get(0).getType());
        assertEquals("2015-09-12 12:12:12", response.getObservationList().get(0).getDatetime());

        assertEquals("1512345", response.getObservationList().get(1).getId());
        assertEquals("Alcobaca", response.getObservationList().get(1).getCamName());
        assertEquals("PERSON", response.getObservationList().get(1).getType());
        assertEquals("2010-09-12 12:12:12", response.getObservationList().get(1).getDatetime());
    }

    @Test
    //correct trackMatch objects with prefix and sufix
    public void trackMatchObjectPrefixSufix() {
        String type = "CAR";
        String subId = "12*12";


        TraceResponse response = frontend.trackMatchObj(type, subId);

        assertEquals(2, response.getObservationList().size());
        assertEquals("12AR12", response.getObservationList().get(0).getId());
        assertEquals("Alcobaca", response.getObservationList().get(0).getCamName());
        assertEquals("CAR", response.getObservationList().get(0).getType());
        assertEquals("2020-03-12 12:12:12", response.getObservationList().get(0).getDatetime());

        assertEquals("12DL12", response.getObservationList().get(1).getId());
        assertEquals("Vale das Mos", response.getObservationList().get(1).getCamName());
        assertEquals("CAR", response.getObservationList().get(1).getType());
        assertEquals("1999-03-12 12:12:12", response.getObservationList().get(1).getDatetime());


    }

    @Test
    //correct trackMatch of id with no asterisk
    public void trackObject() {
        String type = "CAR";
        String subId = "12DL12";

        TraceResponse response = frontend.trackMatchObj(type, subId);

        assertEquals(1, response.getObservationList().size());
        assertEquals("12DL12", response.getObservationList().get(0).getId());
        assertEquals("Vale das Mos", response.getObservationList().get(0).getCamName());
        assertEquals("CAR", response.getObservationList().get(0).getType());
    }


    @Test
    //no object was found
    public void noObjectFound() {
        String type = "PERSON";
        String subId = "13*";


        assertEquals(NOT_FOUND.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.trackMatchObj(type, subId))
                        .getStatus()
                        .getCode());

    }

    @Test
    //testing subId with more than one *
    public void twoAsterisks() {
        String type = "CAR";
        String subId = "*12*";


        assertEquals(INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.trackMatchObj(type, subId))
                        .getStatus()
                        .getCode());
    }

    @Test
    //testing subId with more than one *
    public void onlyOneAsterisksId() {
        String type = "CAR";
        String subId = "*";


        assertEquals(INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.trackMatchObj(type, subId))
                        .getStatus()
                        .getCode());
    }

    @Test
    //no type was given
    public void noTypeGiven() {
        String subId = "13*";


        TrackRequest.newBuilder().setId(subId).build();

        assertEquals(INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.trackMatchObj("", subId))
                        .getStatus()
                        .getCode());

    }

    @Test
    //no type was given
    public void noIdGiven() {
        String type = "CAR";


        TrackRequest.newBuilder().setType(type).build();

        assertEquals(INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.trackMatchObj(type, ""))
                        .getStatus()
                        .getCode());

    }
}
