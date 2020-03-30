package pt.tecnico.sauron.silo.client;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;
import pt.tecnico.sauron.silo.grpc.*;

import static io.grpc.Status.ALREADY_EXISTS;
import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SiloIT extends BaseIT {
	
	// static members
	// TODO

	SiloFrontend frontend = new SiloFrontend("localhost", 8080);
	
	
	// one-time initialization and clean-up
	@BeforeAll
	public static void oneTimeSetUp(){

	}

	@AfterAll
	public static void oneTimeTearDown() {
		
	}
	
	// initialization and clean-up for each test
	
	@BeforeEach
	public void setUp() {
		ClearRequest request = ClearRequest.newBuilder().build();
		frontend.ctrlClear(request);
	}
	
	@AfterEach
	public void tearDown() {
		
	}
		
	// tests 
	
	@Test
	public void test() {
		String camName = "Vale das Mos";
		String id = "12AO1A";
		String date = "1999-03-12 12:12:12";

		CamJoinRequest request = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(13.3).setLongitude(51.2).build();


		ObservationMessage observationMessage = ObservationMessage.newBuilder().setType(Type.CAR).setId(id).setDatetime(date).build();
		ReportRequest request1 = ReportRequest.newBuilder().setCamName(camName).addObservation(observationMessage).build();

		CamInfoRequest request2 = CamInfoRequest.newBuilder().setCamName(camName).build();

		CamJoinResponse response = frontend.camJoin(request);
		System.out.println(response);
		ReportResponse reportResponse = frontend.reportObs(request1);
		System.out.println(reportResponse);
		CamInfoResponse info = frontend.getCamInfo(request2);
		System.out.println(info);

	}





}
