package pt.tecnico.sauron.eye;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.sauron.silo.client.SiloFrontend;
import pt.tecnico.sauron.silo.grpc.CamJoinRequest;
import pt.tecnico.sauron.silo.grpc.ObservationMessage;
import pt.tecnico.sauron.silo.grpc.ReportRequest;
import pt.tecnico.sauron.silo.grpc.Type;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class EyeApp {

	public static void main(String[] args) {
		try {
			System.out.println(EyeApp.class.getSimpleName());

			// check arguments
			if (args.length < 5) {
				throw new IOException();
				//System.out.println("Argument(s) missing!");
			}
			else if (args.length > 5) {
				System.out.println("More arguments than required!");
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

			processInputData(siloFrontend, camName);

			channel.shutdownNow();
			}
			catch(InterruptedException e) {

			System.out.println("Timeout interrupted!");

		} 	catch(IOException e) {

			System.out.println("Invalid input!");

		}

	}

	private static void processInputData(SiloFrontend siloFrontend, String camName) throws IOException, InterruptedException {

		 Scanner scanner;
		 List<ObservationMessage> observations = new ArrayList<>();

		 scanner = new Scanner(System.in);

		 while (scanner.hasNextLine()) {

			 String[] observationLine = scanner.nextLine().split(",");


			 //when line is empty, do a reportRequest with the observation to this point
			 if (observationLine[0].isEmpty() || observationLine[0].isBlank()) {

				 saveGivenObservations(siloFrontend, camName, observations);

			 } //do nothing when there is a comment line
			 else if (observationLine[0].startsWith("#")) { }

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

					 System.out.println("Paused...");
					 Thread.sleep(timeout);

				 } else {
					 throw new IOException();
				 }
			 }
		 }

		saveGivenObservations(siloFrontend, camName, observations);

		scanner.close();
	}

	private static void saveGivenObservations(SiloFrontend siloFrontend, String camName, List<ObservationMessage> observations) {
		ReportRequest.Builder builder = ReportRequest.newBuilder().setCamName(camName);

		for (ObservationMessage om : observations) {
			System.out.println("Sending observation for id "+ om.getId() + " of type " + om.getType().toString() + "... ");
			builder.addObservation(om).build();
		}

		ReportRequest request = builder.build();
		siloFrontend.reportObs(request);
		observations.clear();
		System.out.println("Observations successfully saved!");
	}

}
