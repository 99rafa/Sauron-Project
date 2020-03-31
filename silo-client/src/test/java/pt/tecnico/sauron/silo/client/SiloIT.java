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

	static SiloFrontend frontend = new SiloFrontend("localhost", 8080);
	
	
	// one-time initialization and clean-up
	@BeforeAll
	public static void oneTimeSetUp(){

	}

	@AfterAll
	public static void oneTimeTearDown() {
		ClearRequest clearRequest = ClearRequest.newBuilder().build();
		frontend.ctrlClear(clearRequest);
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

	}





}
