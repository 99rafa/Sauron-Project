package pt.tecnico.sauron.silo.client;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TraceIT extends BaseIT {

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

        frontend.ctrlClear();

        String camName1 = "Vale das Mos";
        String camName2 = "Alcobaca";
        String id1 = "12AR12";
        String id2 = "12AR18";
        String id3 = "123456";
        String date1 = "1999-03-12 12:12:12";
        String date2 = "2020-03-12 12:12:12";
        String date3 = "2015-09-12 12:12:12";
        String date4 = "2010-09-12 12:12:12";

        frontend.camJoin(camName1, 13.3, 51.2);
        frontend.camJoin(camName2, 15.3,53.2);

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
        observationMessage3.add("CAR");
        observationMessage3.add(id1);
        observationMessage3.add(date3);

        List<String> observationMessage4 = new ArrayList<>();
        observationMessage4.add("PERSON");
        observationMessage4.add(id3);
        observationMessage4.add(date4);

        observations1.add(observationMessage1);
        observations2.add(observationMessage2);
        observations3.add(observationMessage3);
        observations4.add(observationMessage4);

        frontend.reportObs(camName1, observations1);
        frontend.reportObs(camName2,observations2);
        frontend.reportObs(camName1,observations3);
        frontend.reportObs(camName2,observations4);

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

    //correct trace of one object whose observations are spread through 2 cameras
    public void traceOneObject() {
        String type = "CAR";
        String id = "12AR12";
        LocalDateTime dt1 = null, dt2;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        TraceResponse response = frontend.traceObj(type,id);

        for (ObservationMessage o : response.getObservationList()) {
            assertEquals("CAR", o.getType());
            assertEquals(id, o.getId());
            if (dt1 == null) dt1 = LocalDateTime.parse(o.getDatetime(), formatter);
            else {
                dt2 = LocalDateTime.parse(o.getDatetime(), formatter);
                System.out.println(dt1);
                System.out.println(dt2);
                assert dt2.isBefore(dt1);
                dt1 = dt2;
            }

        }
    }

    @Test
    //correct trace of one person whose observations are spread through 2 cameras
    public void traceOnePerson() {
        String type = "PERSON";
        String id = "123456";
        LocalDateTime dt1 = null, dt2;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        TraceResponse response = frontend.traceObj(type,id);

        for (ObservationMessage o : response.getObservationList()) {
            assertEquals("PERSON", o.getType());
            assertEquals(id, o.getId());
            if (dt1 == null) dt1 = LocalDateTime.parse(o.getDatetime(), formatter);
            else {
                dt2 = LocalDateTime.parse(o.getDatetime(), formatter);
                System.out.println(dt1);
                System.out.println(dt2);
                assert dt2.isBefore(dt1);
                dt1 = dt2;
            }

        }
    }

    @Test
    //no person was found with given id
    public void noPersonFound() {
        String type = "PERSON";
        String id = "1234521";


        assertEquals(NOT_FOUND.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.traceObj(type, id))
                        .getStatus()
                        .getCode());

    }

    @Test
    //no car was found with given id
    public void noCarFound() {
        String type = "PERSON";
        String id = "12AA12";


        TraceRequest request = TraceRequest.newBuilder().setType(type).setId(id).build();

        assertEquals(NOT_FOUND.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.traceObj(type,id))
                        .getStatus()
                        .getCode());

    }

    @Test
    //no type given
    public void noType() {
        String id = "12AA12";


        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.traceObj("", id))
                        .getStatus()
                        .getCode());

    }

    @Test
    //no id given
    public void noId() {
        String type = "PERSON";


        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.traceObj(type, ""))
                        .getStatus()
                        .getCode());

    }

    @Test
    //unknown type given
    public void unknownType() {
        String type = "DINOSSAURO";
        String id = "12345";


        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.traceObj(type,id))
                        .getStatus()
                        .getCode());

    }


}

