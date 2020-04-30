package pt.tecnico.sauron.silo.client;

import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.client.Exceptions.NoServersAvailableException;
import pt.tecnico.sauron.silo.grpc.CamInfoResponse;
import pt.tecnico.sauron.silo.grpc.ObservationMessage;
import pt.tecnico.sauron.silo.grpc.TraceResponse;
import pt.tecnico.sauron.silo.grpc.TrackResponse;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class SiloIT extends BaseIT {

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
        String n1 = "Vale das Mos";
        String n2 = "Alcobaca";
        double la1 = 29.2;
        double la2 = 31.4;
        double lg1 = 31.0;
        double lg2 = 55.5;


        frontend.camJoin(n1, la1, lg1);
        frontend.camJoin(n2, la2, lg2);

        String date1 = "1999-02-12 12:12:12";
        String date2 = "2000-02-12 12:12:12";
        String date3 = "2001-02-12 12:12:12";
        String date4 = "2002-02-12 12:12:12";
        String date5 = "2003-02-12 12:12:12";
        String id1 = "1";
        String id2 = "12";
        String id4 = "122";

        List<List<String>> observations1 = new ArrayList<>();
        List<List<String>> observations2 = new ArrayList<>();


        List<String> observationMessage1 = new ArrayList<>();
        observationMessage1.add("PERSON");
        observationMessage1.add(id1);
        observationMessage1.add(date1);

        List<String> observationMessage2 = new ArrayList<>();
        observationMessage2.add("PERSON");
        observationMessage2.add(id2);
        observationMessage2.add(date2);

        List<String> observationMessage3 = new ArrayList<>();
        observationMessage3.add("PERSON");
        observationMessage3.add(id1);
        observationMessage3.add(date3);

        List<String> observationMessage4 = new ArrayList<>();
        observationMessage4.add("PERSON");
        observationMessage4.add(id4);
        observationMessage4.add(date4);

        List<String> observationMessage5 = new ArrayList<>();
        observationMessage5.add("PERSON");
        observationMessage5.add(id4);
        observationMessage5.add(date5);

        observations1.add(observationMessage2);
        observations1.add(observationMessage3);
        observations1.add(observationMessage4);
        observations2.add(observationMessage1);
        observations2.add(observationMessage5);

        frontend.reportObs("Vale das Mos", observations1);
        frontend.reportObs("Alcobaca", observations2);
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

    // tests

    @Test
    public void join2Cams() {


    }

    @Test
    public void joinObservations() {


    }

    @Test
    public void camInfo() {

        CamInfoResponse response1 = frontend.getCamInfo("Vale das Mos");
        CamInfoResponse response2 = frontend.getCamInfo("Alcobaca");

        assertEquals((Double) 29.2, (Double) response1.getLatitude());
        assertEquals((Double) 31.0, (Double) response1.getLongitude());
        assertEquals((Double) 31.4, (Double) response2.getLatitude());
        assertEquals((Double) 55.5, (Double) response2.getLongitude());

    }

    @Test
    public void track() {

        TrackResponse response = frontend.trackObj("PERSON", "1");
        ObservationMessage obs = response.getObservation();
        String cam = obs.getCamName();
        String t = obs.getType();
        String date = obs.getDatetime();
        String id = obs.getId();


        assertEquals("1", id);
        assertEquals("2001-02-12 12:12:12", date);
        assertEquals("Vale das Mos", cam);
        assertEquals(t, "PERSON");


    }

    @Test
    public void trackMatch() {
        TraceResponse response = frontend.trackMatchObj("PERSON", "1*");
        List<ObservationMessage> obsv = response.getObservationList();

        ObservationMessage obs1 = obsv.get(0);
        ObservationMessage obs2 = obsv.get(1);


        assertEquals("1", obs1.getId());
        assertEquals("2001-02-12 12:12:12", obs1.getDatetime());
        assertEquals("Vale das Mos", obs1.getCamName());
        assertEquals("PERSON", obs1.getType());

        assertEquals("12", obs2.getId());
        assertEquals("2000-02-12 12:12:12", obs2.getDatetime());
        assertEquals("Vale das Mos", obs2.getCamName());

        response = frontend.trackMatchObj("PERSON", "*2");
        obsv = response.getObservationList();

        obs1 = obsv.get(0);
        obs2 = obsv.get(1);

        assertEquals("12", obs1.getId());
        assertEquals("2000-02-12 12:12:12", obs1.getDatetime());
        assertEquals("Vale das Mos", obs1.getCamName());
        assertEquals("PERSON", obs1.getType());

        assertEquals("122", obs2.getId());
        assertEquals("2003-02-12 12:12:12", obs2.getDatetime());
        assertEquals("Alcobaca", obs2.getCamName());
        assertEquals("PERSON", obs2.getType());
    }

    @Test
    public void trace() {
        TraceResponse response = frontend.traceObj("PERSON", "1");
        List<ObservationMessage> obsv = response.getObservationList();

        ObservationMessage obs1 = obsv.get(0);
        ObservationMessage obs2 = obsv.get(1);

        assertEquals("1", obs1.getId());
        assertEquals("2001-02-12 12:12:12", obs1.getDatetime());
        assertEquals("Vale das Mos", obs1.getCamName());
        assertEquals("PERSON", obs1.getType());

        assertEquals("1", obs2.getId());
        assertEquals("1999-02-12 12:12:12", obs2.getDatetime());
        assertEquals("Alcobaca", obs2.getCamName());
        assertEquals("PERSON", obs2.getType());

    }
}
