package pt.tecnico.sauron.eye;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.client.SiloFrontend;
import pt.tecnico.sauron.silo.grpc.*;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class EyeApp {

	public static void main(String[] args) {
		System.out.println(EyeApp.class.getSimpleName());


		System.out.printf("Received %d arguments%n", args.length);

		// check arguments
		if (!checkInitConfigs(args)) {
			System.out.println("Error: Invalid Arguments!");
		}


		final String host = args[0];
		final int port = Integer.parseInt(args[1]);


		
		final String camName = args[2];
		final double latitude = Double.parseDouble(args[3]);
		final double longitude = Double.parseDouble(args[4]);

		final String target = host + ":" + port;

		final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();


		SiloFrontend siloFrontend = new SiloFrontend(host, port);

		CamJoinRequest request = CamJoinRequest.newBuilder().setCamName(camName).setLatitude(latitude).setLongitude(longitude).build();
		siloFrontend.camJoin(request);

		try {
			processInputData(siloFrontend, camName);
		}
		catch(InterruptedException e) {

			System.out.println("Timeout interrupted");

		} catch(IOException e) {

			System.out.println("Invalid input");

		}


		channel.shutdownNow();
	}

	private static boolean checkInitConfigs(String[] args) {


		if (args.length <  5) {
			return false;
		}

		if (args.length > 5 && args.length != 7 ) {
			return false;
		}

	   	return true;
	}

	private static void processInputData(SiloFrontend siloFrontend, String camName) throws IOException, InterruptedException {

		 Scanner scanner;
		 List<ObservationMessage> observations = new ArrayList<>();

		 scanner = new Scanner(System.in);

		 while (scanner.hasNextLine()) {

			 String[] observationLine = scanner.nextLine().split(",");


			 //when line is empty, do a reportRequest with the observation to this point
			 if (observationLine[0].isEmpty() || observationLine[0].isBlank()) {

				 ReportRequest.Builder builder = ReportRequest.newBuilder().setCamName(camName);

				 for (ObservationMessage om : observations) {
					 builder.addObservation(om).build();
				 }

				 ReportRequest request = builder.build();
				 siloFrontend.reportObs(request);

				 observations.clear();

			 } //do nothing when there is a comment line
			 else if (observationLine[0].startsWith("#")) continue;

			 else {

				 //first token is first substring before the comma
				 String firstToken = observationLine[0];

				 //observations to be added
				 if (firstToken.equals("car") && observationLine.length == 2) {


					 String id = observationLine[1];
					 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					 Date date = new Date();

					 observations.add(ObservationMessage.newBuilder().setType(Type.CAR).setId(id).setDatetime(dateFormat.format(date)).build());

				 } else if (firstToken.equals("person") && observationLine.length == 2) {

					 String id = observationLine[1];
					 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					 Date date = new Date();

					 observations.add(ObservationMessage.newBuilder().setType(Type.PERSON).setId(id).setDatetime(dateFormat.format(date)).build());

				 }
				 //timeout when line starts with zzz
				 else if ((firstToken).equals("zzz") && observationLine.length == 2) {

					 int timeout = Integer.parseInt(observationLine[1].trim());

					 Thread.sleep(timeout);

				 } else {
					 throw new IOException();
				 }
			 }
		 }

		 ReportRequest.Builder builder = ReportRequest.newBuilder().setCamName(camName);

		 for (ObservationMessage om : observations) {
			 builder.addObservation(om).build();
		 }

		 ReportRequest request = builder.build();
		 siloFrontend.reportObs(request);

		 scanner.close();
	}
	
}
