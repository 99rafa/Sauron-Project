package pt.tecnico.sauron.eye;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.client.SiloFrontend;
import pt.tecnico.sauron.silo.grpc.CamJoinRequest;
import pt.tecnico.sauron.silo.grpc.CamJoinResponse;

import java.util.Arrays;
import java.util.Scanner;


public class EyeApp {

	public static void main(String[] args) {
		System.out.println(EyeApp.class.getSimpleName());

		// receive arguments
		Scanner scanner = new Scanner(System.in);

		String[] eyeTokens = scanner.nextLine().split(" ");
		System.out.printf("Received %d arguments%n", eyeTokens.length);

		// check arguments
		while (!checkInitConfigs(eyeTokens)) {

			eyeTokens = scanner.nextLine().split(" ");
			System.out.printf("Received %d arguments%n", eyeTokens.length);

		}

		final String host = eyeTokens[1];
		final int port = Integer.parseInt(eyeTokens[2]);
		final String target = host + ":" + port;
		
		final String camName = eyeTokens[3];
		final double latitude = Double.parseDouble(eyeTokens[4]);
		final double longitude = Double.parseDouble(eyeTokens[5]);


		SiloFrontend siloFrontend = new SiloFrontend(host, port);

		CamJoinRequest request = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(latitude).setLongitude(longitude).build();
		siloFrontend.camJoin(request);

		

//
//
//
//		
//		channel.shutdownNow();
	}

	private static boolean checkInitConfigs(String[] args) {


		if (args.length < 6 ) {
			int numArgsMissing = 6 - args.length;
			System.out.println("Error:" + numArgsMissing + " Argument(s) missing!" );
			return false;
		}

		if (args.length > 6 ) {
			System.out.println("Error: More arguments than required!");
			return false;
		}

		if (!args[0].equals("eye")) {
			System.out.println( "Error: Eye command not provided!" );
			return false;
		}
	   	return true;
	}
	
}
