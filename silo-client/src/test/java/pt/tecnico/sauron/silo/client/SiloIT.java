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

	//CAMJOIN tests
	@Test
	public void joinNonUniqueCamera(){
		String camName = "Vale das Mos";

		CamJoinRequest request = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(13.3).setLongitude(51.2).build();
		CamJoinResponse response = frontend.camJoin(request);

	}

	@Test
	public void join2CamerasWithSameNameDifferentLocation(){
		String camName = "Vale das Mos";
		double lat1 = 13.2;
		double lat2 = 11.2;
		double log = 31.2;

		CamJoinRequest request = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(lat1).setLongitude(log).build();
		frontend.camJoin(request);
		CamJoinRequest request2 = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(lat2).setLongitude(log).build();


		assertEquals(
				ALREADY_EXISTS.getCode(),
				assertThrows(
						StatusRuntimeException.class, () -> frontend.camJoin(request2))
						.getStatus()
						.getCode());

	}

	@Test
	public void join2CamerasWithSameNameSameLocation(){
		String camName = "Vale das Mos";
		double lat = 13.2;
		double log = 31.2;

		CamJoinRequest request = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(lat).setLongitude(log).build();
		frontend.camJoin(request);
		CamJoinRequest request2 = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(lat).setLongitude(log).build();
		frontend.camJoin(request2);
	}

	@Test
	public void joinCameraWithNullName(){

	}

	@Test
	public void joinCameraWithInvalidName(){

	}

	@Test
	public void joinCameraWithInvalidCoords(){

	}

	@Test
	public void joinCameraWithNullCoords(){

	}



	//CAMINFO tests
	@Test
	public void camInfoFromExistingCam(){

	}

	@Test
	public void camInfoFromNonExistingCam(){

	}


}
