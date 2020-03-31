package pt.tecnico.sauron.spotter;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.tecnico.sauron.silo.client.SiloFrontend;
import pt.tecnico.sauron.silo.grpc.*;

import java.util.List;
import java.util.Scanner;

public class SpotterApp {
	
	public static void main(String[] args) {
		System.out.println(SpotterApp.class.getSimpleName());

		Scanner scanner = new Scanner(System.in);

		boolean exit = false;

		// check arguments
		if (!checkInitConfigs(args)) {
			System.out.println("erro");
		}

		final String host = args[0];
		final int port = Integer.parseInt(args[1]);


		final String target = host + ":" + port;

		final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

		do {
			String[] spotterTokens = scanner.nextLine().split(" ");

			while (!checkSpotCommand(spotterTokens)) spotterTokens = scanner.nextLine().split(" ");

			final String command = spotterTokens[0];

			SiloFrontend siloFrontend = new SiloFrontend(host, port);

			if(command.equals("exit")){
				exit = true;
			}


			else if(command.equals("ping")){

				final String name = spotterTokens[1];

				PingRequest request = PingRequest.newBuilder().setInputCommand(name).build();
				PingResponse response = siloFrontend.ctrlPing(request);
				System.out.println(response.getOutputText());
			}

			else if(command.equals("init")){
				InitRequest request = InitRequest.newBuilder().build();
				siloFrontend.ctrlInit(request);
				System.out.println("Nothing to be configured!");
			}

			else if(command.equals("clear")){
				ClearRequest request = ClearRequest.newBuilder().clear().build();
				siloFrontend.ctrlClear(request);
				System.out.println("System is now empty!");
			}

			else if(command.equals("help")){
				System.out.println("Spot -> spot <type> <id> ");
				System.out.println("Trail -> trail <type> <id> ");
				System.out.println("Ping -> ping <name>");
				System.out.println("Clear -> clear");
				System.out.println("Init -> init");
			}

			else {

				final String type = spotterTokens[1];
				final String id = spotterTokens[2];


				if (command.equals("spot")) {

					Type t = verifyType(type);

					if (id.contains("*")) {

						try {

							TrackMatchRequest request = TrackMatchRequest.newBuilder().setType(t).setSubId(id).build();
							TrackMatchResponse response = siloFrontend.trackMatchObj(request);
							trackMatchResponseToString(response, siloFrontend);
						}

						catch (StatusRuntimeException e){

							System.out.println();

						}

					}

					else {

						try {

							TrackRequest request = TrackRequest.newBuilder().setType(t).setId(id).build();
							TrackResponse response = siloFrontend.trackObj(request);
							trackResponseToString(response, siloFrontend);
						}

						catch (StatusRuntimeException e){

							System.out.println();

						}
					}
				}

				if (command.equals("trail")) {

					Type t = verifyType(type);

					try {

						TraceRequest request = TraceRequest.newBuilder().setType(t).setId(id).build();
						TraceResponse response = siloFrontend.traceObj(request);
						traceResponseToString(response, siloFrontend);

					}

					catch (StatusRuntimeException e){

						System.out.println();

					}
				}
			}
		}while(!exit);

		channel.shutdownNow();

	}

	private static void traceResponseToString(TraceResponse response, SiloFrontend siloFrontend){

		List<ObservationMessage> observationList = response.getObservationList();

		for (ObservationMessage om: observationList){
			if(om.getType().equals(Type.CAR)){

				CamInfoRequest request = CamInfoRequest.newBuilder().setCamName(om.getCamName()).build();
				CamInfoResponse camResponse = siloFrontend.getCamInfo(request);

				System.out.println("car" + "," +
						om.getId() + "," + om.getDatetime() + "," + om.getCamName() + "," +
						camResponse.getLatitude() + "," + camResponse.getLongitude());
			}

			else if(om.getType().equals(Type.PERSON)){

				CamInfoRequest request = CamInfoRequest.newBuilder().setCamName(om.getCamName()).build();
				CamInfoResponse camResponse = siloFrontend.getCamInfo(request);

				System.out.println("person" + "," +
						om.getId() + "," + om.getDatetime() + "," + om.getCamName() + "," +
						camResponse.getLatitude() + "," + camResponse.getLongitude());
			}
		}
	}

	private static void trackResponseToString(TrackResponse response, SiloFrontend siloFrontend){
		CamInfoRequest request = CamInfoRequest.newBuilder().setCamName(response.getObservation().getCamName()).build();
		CamInfoResponse camResponse = siloFrontend.getCamInfo(request);

		if (response.getObservation().getType().equals(Type.CAR)){
			System.out.println("car" + "," +
					response.getObservation().getId() + "," + response.getObservation().getDatetime() +
					"," + response.getObservation().getCamName() + "," + camResponse.getLatitude() + "," +
					camResponse.getLongitude());
		}

		else if (response.getObservation().getType().equals(Type.PERSON)){
			System.out.println("person" + "," +
					response.getObservation().getId() + "," + response.getObservation().getDatetime() +
					"," + response.getObservation().getCamName() + "," + camResponse.getLatitude() + "," +
					camResponse.getLongitude());
		}


	}

	private static void trackMatchResponseToString(TrackMatchResponse response, SiloFrontend siloFrontend){

		List<ObservationMessage> observationList = response.getObservationList();

		for (ObservationMessage om: observationList){
			if(om.getType().equals(Type.CAR)){

				CamInfoRequest request = CamInfoRequest.newBuilder().setCamName(om.getCamName()).build();
				CamInfoResponse camResponse = siloFrontend.getCamInfo(request);

				System.out.println("car" + "," +
						om.getId() + "," + om.getDatetime() + "," + om.getCamName() + "," +
						camResponse.getLatitude() + "," + camResponse.getLongitude());
			}

			else if(om.getType().equals(Type.PERSON)){

				CamInfoRequest request = CamInfoRequest.newBuilder().setCamName(om.getCamName()).build();
				CamInfoResponse camResponse = siloFrontend.getCamInfo(request);

				System.out.println("person" + "," +
						om.getId() + "," + om.getDatetime() + "," + om.getCamName() + "," +
						camResponse.getLatitude() + "," + camResponse.getLongitude());
			}
		}
	}


	private static Type verifyType (String string){
		if(string.equals("car")) return Type.CAR;
		if(string.equals("person")) return Type.PERSON;
		else return Type.UNRECOGNIZED;
	}

	private static boolean checkSpotCommand(String[] args) {
		if (args.length < 3 ) {

			if (args.length == 1 && (args[0].equals("exit") || args[0].equals("help")||
					args[0].equals("init") || args[0].equals("clear"))) return true;

			else if(args.length == 2 && args[0].equals("ping")) return true;

			else {
				int numArgsMissing = 3;

				if (args[0].equals("ping")) numArgsMissing--;

				numArgsMissing -= args.length;
				System.out.println("Error:" + numArgsMissing + " Argument(s) missing!");
				return false;
			}
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


		if (args.length < 2 ) {
			int numArgsMissing = 2 - args.length;
			System.out.println("Error:" + numArgsMissing + " Argument(s) missing!" );
			return false;
		}

		if (args.length > 2 ) {
			System.out.println("Error: More arguments than required!");
			return false;
		}
		return true;
	}

}
