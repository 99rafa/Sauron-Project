package pt.tecnico.sauron.silo.client;

import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class SiloIT extends BaseIT {

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
        String n1 = "Vale das Mos";
        String n2 = "Alcobaca";
        double la1 = 29.2;
        double la2 = 31.4;
        double lg1 = 31.0;
        double lg2 = 55.5;

        CamJoinRequest request = CamJoinRequest.newBuilder()
                .setCamName(n1)
                .setLatitude(la1)
                .setLongitude(lg1).build();

        CamJoinRequest request2 = CamJoinRequest.newBuilder()
                .setCamName(n2)
                .setLatitude(la2)
                .setLongitude(lg2).build();

        frontend.camJoin(request);
        frontend.camJoin(request2);

        String date1 = "1999-02-12 12:12:12";
        String date2 = "2000-02-12 12:12:12";
        String date3 = "2001-02-12 12:12:12";
        String date4 = "2002-02-12 12:12:12";
        String date5 = "2003-02-12 12:12:12";
        String id1 = "1";
        String id2 = "12";
        String id4 = "122";

        ObservationMessage o1 = ObservationMessage.newBuilder().setDatetime(date1).setId(id1).setType("PERSON").build();
        ObservationMessage o2 = ObservationMessage.newBuilder().setDatetime(date2).setId(id2).setType("PERSON").build();
        ObservationMessage o3 = ObservationMessage.newBuilder().setDatetime(date3).setId(id1).setType("PERSON").build();
        ObservationMessage o4 = ObservationMessage.newBuilder().setDatetime(date4).setId(id4).setType("PERSON").build();
        ObservationMessage o5 = ObservationMessage.newBuilder().setDatetime(date5).setId(id4).setType("PERSON").build();


        ReportRequest request3 = ReportRequest.newBuilder()
                .setCamName("Vale das Mos")
                .addObservation(o2)
                .addObservation(o3)
                .addObservation(o4).build();
        ReportRequest request4 = ReportRequest.newBuilder()
                .setCamName("Alcobaca")
                .addObservation(o1)
                .addObservation(o5).build();

        frontend.reportObs(request3);
        frontend.reportObs(request4);
    }

    @AfterAll
    public static void oneTimeTearDown() {
        ClearRequest clearRequest = ClearRequest.newBuilder().build();
        frontend.ctrlClear(clearRequest);
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

        CamInfoRequest request1 = CamInfoRequest.newBuilder().setCamName("Vale das Mos").build();
        CamInfoRequest request2 = CamInfoRequest.newBuilder().setCamName("Alcobaca").build();

        CamInfoResponse response1 = frontend.getCamInfo(request1);
        CamInfoResponse response2 = frontend.getCamInfo(request2);

        assertEquals((Double) 29.2, (Double) response1.getLatitude());
        assertEquals((Double) 31.0, (Double) response1.getLongitude());
        assertEquals((Double) 31.4, (Double) response2.getLatitude());
        assertEquals((Double) 55.5, (Double) response2.getLongitude());

    }

    @Test
    public void track() {
        TrackRequest request = TrackRequest.newBuilder().setId("1").setType("PERSON").build();
        TrackResponse response = frontend.trackObj(request);
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
        TrackMatchRequest request = TrackMatchRequest.newBuilder().setSubId("1*").setType("PERSON").build();
        TrackMatchResponse response = frontend.trackMatchObj(request);
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

        request = TrackMatchRequest.newBuilder().setSubId("*2").setType("PERSON").build();
        response = frontend.trackMatchObj(request);
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
        TraceRequest request = TraceRequest.newBuilder().setId("1").setType("PERSON").build();
        TraceResponse response = frontend.traceObj(request);
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
