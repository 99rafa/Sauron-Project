package pt.tecnico.sauron.silo.client;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.*;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReportIT extends BaseIT{

    static SiloFrontend frontend = new SiloFrontend("localhost", 8080);


    // one-time initialization and clean-up
    @BeforeAll
    public static void oneTimeSetUp(){
        CamJoinRequest camJoinRequest = CamJoinRequest.newBuilder()
                .setCamName("Vale das Mos")
                .setLatitude(12.2)
                .setLongitude(12.2).build();

        frontend.camJoin(camJoinRequest);
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
    public void report3CorrectObservations(){
        String date = "2019-12-12 12:12:12";
        String camName = "Vale das Mos";
        Type car = Type.CAR;
        Type person = Type.PERSON;


        ObservationMessage observationMessage1 = ObservationMessage.newBuilder()
                .setDatetime(date)
                .setType(person)
                .setId("123").build();

        ObservationMessage observationMessage2 = ObservationMessage.newBuilder()
                .setDatetime(date)
                .setType(car)
                .setId("8709OA").build();

        ObservationMessage observationMessage3 = ObservationMessage.newBuilder()
                .setDatetime(date)
                .setType(car)
                .setId("1212AO").build();

        ReportRequest reportRequest = ReportRequest.newBuilder()
                .setCamName(camName)
                .addObservation(observationMessage1)
                .addObservation(observationMessage2)
                .addObservation(observationMessage3).build();

        ReportResponse reportResponse = frontend.reportObs(reportRequest);


    }

    @Test
    public void reportInvalidObsId(){
        String date = "2019-12-12 12:12:12";
        String camName = "Vale das Mos";
        Type car = Type.CAR;
        Type person = Type.PERSON;

        ObservationMessage observationMessage1 = ObservationMessage.newBuilder()
                .setDatetime(date)
                .setType(person)
                .setId("12A3").build();

        ObservationMessage observationMessage2 = ObservationMessage.newBuilder()
                .setDatetime(date)
                .setType(car)
                .setId("A709OA").build();

        ReportRequest request1 = ReportRequest.newBuilder()
                .setCamName(camName)
                .addObservation(observationMessage1)
                .build();

        ReportRequest request2 = ReportRequest.newBuilder()
                .setCamName(camName)
                .addObservation(observationMessage2)
                .build();

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.reportObs(request1))
                        .getStatus()
                        .getCode());

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.reportObs(request2))
                        .getStatus()
                        .getCode());


    }

    @Test
    public void reportInvalidDate(){
        String date = "2021-12-12 12:12:12";
        String camName = "Vale das Mos";
        Type person = Type.PERSON;

        ObservationMessage observationMessage1 = ObservationMessage.newBuilder()
                .setDatetime(date)
                .setType(person)
                .setId("123").build();

        ReportRequest request1 = ReportRequest.newBuilder()
                .setCamName(camName)
                .addObservation(observationMessage1)
                .build();

        assertEquals(
                INVALID_ARGUMENT.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.reportObs(request1))
                        .getStatus()
                        .getCode());
    }

    @Test
    public void reportNonExistingCamera(){
        String date = "2019-12-12 12:12:12";
        String camName = "NOT Mos";
        Type person = Type.PERSON;

        ObservationMessage observationMessage1 = ObservationMessage.newBuilder()
                .setDatetime(date)
                .setType(person)
                .setId("123").build();

        ReportRequest request1 = ReportRequest.newBuilder()
                .setCamName(camName)
                .addObservation(observationMessage1)
                .build();

        assertEquals(
                NOT_FOUND.getCode(),
                assertThrows(
                        StatusRuntimeException.class, () -> frontend.reportObs(request1))
                        .getStatus()
                        .getCode());
    }
}
