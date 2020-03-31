package pt.tecnico.sauron.silo.client;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrackMatchIT extends BaseIT {

    static SiloFrontend frontend = new SiloFrontend("localhost", 8080);


    // one-time initialization and clean-up
    @BeforeAll
    public static void oneTimeSetUp() {

        ClearRequest request = ClearRequest.newBuilder().build();
        frontend.ctrlClear(request);

        String camName1 = "Vale das Mos";
        String camName2 = "Alcobaca";
        String id1 = "12DL12";
        String id2 = "12AR12";
        String id3 = "151212";
        String date1 = "1999-03-12 12:12:12";
        String date2 = "2020-03-12 12:12:12";
        String date3 = "2015-09-12 12:12:12";
        String date4 = "2010-09-12 12:12:12";


        CamJoinRequest joinRequest1 = CamJoinRequest.newBuilder().setCamName(camName1).setLatitude(13.3).setLongitude(51.2).build();
        CamJoinRequest joinRequest2 = CamJoinRequest.newBuilder().setCamName(camName2).setLatitude(15.3).setLongitude(53.2).build();
        frontend.camJoin(joinRequest1);
        frontend.camJoin(joinRequest2);
        ObservationMessage observationMessage1 = ObservationMessage.newBuilder().setType(Type.CAR).setId(id1).setDatetime(date1).build();
        ObservationMessage observationMessage2 = ObservationMessage.newBuilder().setType(Type.CAR).setId(id2).setDatetime(date2).build();
        ObservationMessage observationMessage3 = ObservationMessage.newBuilder().setType(Type.CAR).setId(id1).setDatetime(date3).build();
        ObservationMessage observationMessage4 = ObservationMessage.newBuilder().setType(Type.PERSON).setId(id3).setDatetime(date4).build();
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
        Type type = Type.CAR;
        String subId = "12*";


        TrackMatchRequest request = TrackMatchRequest.newBuilder().setType(type).setSubId(subId).build();
        TrackMatchResponse response = frontend.trackMatchObj(request);

        for (ObservationMessage o : response.getObservationList()) {

            assert o.getId().startsWith("12");
            if (o.getId().equals("12DL12")) {
                assertEquals("Vale das Mos",o.getCamName());
                assertEquals(Type.CAR, o.getType());
                assertEquals("2015-09-12 12:12:12", o.getDatetime());
            }
            else if (o.getId().equals("12AR12")) {
                assertEquals("Alcobaca",o.getCamName());
                assertEquals(Type.CAR, o.getType());
                assertEquals("2020-03-12 12:12:12", o.getDatetime());
            }
        }
    }

    @Test
    //correct trackMatch objects with prefix and sufix
    public void trackMatchObjectPrefixSufix() {
        Type type = Type.CAR;
        String subId = "12*12";


        TrackMatchRequest request = TrackMatchRequest.newBuilder().setType(type).setSubId(subId).build();
        TrackMatchResponse response = frontend.trackMatchObj(request);

        for (ObservationMessage o : response.getObservationList()) {

            assert o.getId().startsWith("12");
            assert o.getId().endsWith("12");
            if (o.getId().equals("12DL12")) {
                assertEquals("Vale das Mos",o.getCamName());
                assertEquals(Type.CAR, o.getType());
                assertEquals("2015-09-12 12:12:12", o.getDatetime());
            }
            else if (o.getId().equals("12AR12")) {
                assertEquals("Alcobaca",o.getCamName());
                assertEquals(Type.CAR, o.getType());
                assertEquals("2020-03-12 12:12:12", o.getDatetime());
            }
        }
    }

    @Test
    //correct trackMatch of id with no asterisk
    public void trackObject() {
        Type type = Type.CAR;
        String subId = "12DL12";


        TrackMatchRequest request = TrackMatchRequest.newBuilder().setType(type).setSubId(subId).build();
        TrackMatchResponse response = frontend.trackMatchObj(request);

        assertEquals(1, response.getObservationList().size());
        assertEquals("12DL12",response.getObservationList().get(0).getId());
        assertEquals("Vale das Mos",response.getObservationList().get(0).getCamName());
        assertEquals(Type.CAR, response.getObservationList().get(0).getType());
    }


    @Test
    //no object was found
    public void noObjectFound() {
        Type type = Type.CAR;
        String subId = "13*";


        TrackMatchRequest request = TrackMatchRequest.newBuilder().setType(type).setSubId(subId).build();

        assertEquals(NOT_FOUND.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.trackMatchObj(request))
                        .getStatus()
                        .getCode());

    }

    @Test
    //testing subId with more than one *
    public void twoAsterisks() {
        Type type = Type.CAR;
        String subId = "*12*";


        TrackMatchRequest request = TrackMatchRequest.newBuilder().setType(type).setSubId(subId).build();

        assertEquals(INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.trackMatchObj(request))
                        .getStatus()
                        .getCode());
    }

    @Test
    //testing subId with more than one *
    public void onlyOneAsterisksId() {
        Type type = Type.CAR;
        String subId = "*";


        TrackMatchRequest request = TrackMatchRequest.newBuilder().setType(type).setSubId(subId).build();

        assertEquals(INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.trackMatchObj(request))
                        .getStatus()
                        .getCode());
    }
}
