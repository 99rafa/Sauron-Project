package pt.tecnico.sauron.spotter;

import pt.tecnico.sauron.silo.client.SiloFrontend;
import pt.tecnico.sauron.silo.grpc.TrackRequest;
import pt.tecnico.sauron.silo.grpc.TraceRequest;
import pt.tecnico.sauron.silo.grpc.Type;


import java.util.Scanner;

public class SpotterApp {
	
	public static void main(String[] args) {
		System.out.println(SpotterApp.class.getSimpleName());
		
		// receive and print arguments
		Scanner scanner = new Scanner(System.in);

		String[] eyeTokens = scanner.nextLine().split(" ");
		System.out.printf("Received %d arguments%n", eyeTokens.length);

		while (!checkInitConfigs(eyeTokens)) {

			eyeTokens = scanner.nextLine().split(" ");
			System.out.printf("Received %d arguments%n", eyeTokens.length);

		}

		final String host = eyeTokens[1];
		final int port = Integer.parseInt(eyeTokens[2]);

		scanner = new Scanner(System.in);
		eyeTokens = scanner.nextLine().split(" ");

		while(!checkSpotCommand(eyeTokens)) eyeTokens = scanner.nextLine().split(" ");

		if (eyeTokens[0].equals("spot")){

			Type type = verifyType(eyeTokens[1]);
			final String id = eyeTokens[2];

			SiloFrontend siloFrontend = new SiloFrontend(host, port);
			TrackRequest request = TrackRequest.newBuilder().setType(type).setId(id).build();
			siloFrontend.trackObj(request);
		}

		if (eyeTokens[0].equals("trail")){
			Type type = verifyType(eyeTokens[1]);
			final String id = eyeTokens[2];

			SiloFrontend siloFrontend = new SiloFrontend(host, port);
			TraceRequest request = TraceRequest.newBuilder().setType(type).setId(id).build();
			siloFrontend.traceObj(request);
		}

	}
	
	private static Type verifyType (String string){
		if(string.equals("car")) return Type.CAR;
		if(string.equals("person")) return Type.PERSON;
		else return Type.UNRECOGNIZED;
	}

	private static boolean checkSpotCommand(String[] args) {
		if (args.length < 3 ) {
			int numArgsMissing = 3 - args.length;
			System.out.println("Error:" + numArgsMissing + " Argument(s) missing!" );
			return false;
		}

		if (args.length > 3 ) {
			System.out.println("Error: More arguments than required!");
			return false;
		}

		if (!(args[0].equals("spot") || args[0].equals("trail"))) {
			System.out.println( "Error: Wrong command!" );
			return false;
		}
		return true;

	}



		private static boolean checkInitConfigs(String[] args) {


		if (args.length < 3 ) {
			int numArgsMissing = 3 - args.length;
			System.out.println("Error:" + numArgsMissing + " Argument(s) missing!" );
			return false;
		}

		if (args.length > 3 ) {
			System.out.println("Error: More arguments than required!");
			return false;
		}

		if (!args[0].equals("spotter")) {
			System.out.println( "Error: Spotter command not provided!" );
			return false;
		}
		return true;
	}

	

}
