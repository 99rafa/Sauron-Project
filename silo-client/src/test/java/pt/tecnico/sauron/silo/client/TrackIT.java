package pt.tecnico.sauron.silo.client;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;


import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrackIT extends BaseIT{

    static SiloFrontend frontend;

    static {
        try {
            frontend = new SiloFrontend("localhost", "2181","");
        } catch (ZKNamingException e) {
            e.printStackTrace();
        }
    }


    // one-time initialization and clean-up
    @BeforeAll
    public static void oneTimeSetUp(){

        ClearRequest request = ClearRequest.newBuilder().build();
        frontend.ctrlClear(request);

        String camName1 = "Vale das Mos";
        String camName2 = "Alcobaca";
        String id1 = "12AR12";
        String id2 = "12AR18";
        String id3 = "151217";
        String date1 = "1999-03-12 12:12:12";
        String date2 = "2020-03-12 12:12:12";
        String date3 = "2015-09-12 12:12:12";
        String date4 = "2010-09-12 12:12:12";


        CamJoinRequest joinRequest1 = CamJoinRequest.newBuilder().setCamName(camName1).setLatitude(13.3).setLongitude(51.2).build();
        CamJoinRequest joinRequest2 = CamJoinRequest.newBuilder().setCamName(camName2).setLatitude(15.3).setLongitude(53.2).build();
        frontend.camJoin(joinRequest1);
        frontend.camJoin(joinRequest2);
        ObservationMessage observationMessage1 = ObservationMessage.newBuilder().setType("CAR" ).setId(id1).setDatetime(date1).build();
        ObservationMessage observationMessage2 = ObservationMessage.newBuilder().setType("CAR" ).setId(id2).setDatetime(date2).build();
        ObservationMessage observationMessage3 = ObservationMessage.newBuilder().setType("CAR" ).setId(id1).setDatetime(date3).build();
        ObservationMessage observationMessage4 = ObservationMessage.newBuilder().setType("PERSON" ).setId(id3).setDatetime(date4).build();
        ReportRequest request1 = ReportRequest.newBuilder().setCamName(camName1).addObservation(observationMessage1).build();
        ReportRequest request2 = ReportRequest.newBuilder().setCamName(camName2).addObservation(observationMessage2).build();
        ReportRequest request3 = ReportRequest.newBuilder().setCamName(camName1).addObservation(observationMessage3).build();
        ReportRequest request4 = ReportRequest.newBuilder().setCamName(camName2).addObservation(observationMessage4).build();
        frontend.reportObs(request1);
        frontend.reportObs(request2);
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

    @Test
    //correct track of one object
    public void trackOneObject() {
        String type = "CAR" ;
        String id = "12AR12";


        TrackRequest request = TrackRequest.newBuilder().setType(type).setId(id).build();
        TrackResponse response = frontend.trackObj(request);

            assertEquals("CAR" , response.getObservation().getType());
            assertEquals(id, response.getObservation().getId());
            assertEquals("Vale das Mos", response.getObservation().getCamName());
            assertEquals("2015-09-12 12:12:12", response.getObservation().getDatetime());
    }

    @Test
    //correct track of one person
    public void trackOnePerson() {
        String type = "PERSON";
        String id = "151217";


        TrackRequest request = TrackRequest.newBuilder().setType(type).setId(id).build();
        TrackResponse response = frontend.trackObj(request);

        assertEquals("PERSON", response.getObservation().getType());
        assertEquals(id, response.getObservation().getId());
        assertEquals("Alcobaca", response.getObservation().getCamName());
        assertEquals("2010-09-12 12:12:12", response.getObservation().getDatetime());
    }

    @Test
    //no person was found
    public void noPersonFound() {
        String type = "PERSON";
        String id = "1234521";


        TrackRequest request = TrackRequest.newBuilder().setType(type).setId(id).build();

        assertEquals(NOT_FOUND.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.trackObj(request))
                        .getStatus()
                        .getCode());

    }

    @Test
    //no car was found
    public void noCarFound() {
        String type = "PERSON";
        String id = "1234521";


        TrackRequest request = TrackRequest.newBuilder().setType(type).setId(id).build();

        assertEquals(NOT_FOUND.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.trackObj(request))
                        .getStatus()
                        .getCode());

    }

    @Test
    //no type given
    public void noType() {
        String id = "12AA12";

        TrackRequest request = TrackRequest.newBuilder().setId(id).build();

        assertEquals(INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.trackObj(request))
                        .getStatus()
                        .getCode());

    }

    @Test
    //no id given
    public void noId() {
        String type = "PERSON";

        TrackRequest request = TrackRequest.newBuilder().setType(type).build();

        assertEquals(INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.trackObj(request))
                        .getStatus()
                        .getCode());

    }


    @Test
    //unknown type given
    public void unknownType() {
        String type = "DINOSSAURO";
        String id = "12345";


        TrackRequest request = TrackRequest.newBuilder().setId(id).setType(type).build();

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.trackObj(request))
                        .getStatus()
                        .getCode());

    }

}
