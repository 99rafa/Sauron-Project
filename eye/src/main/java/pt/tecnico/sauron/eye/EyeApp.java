package pt.tecnico.sauron.eye;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import pt.tecnico.sauron.silo.client.SiloFrontend;
import pt.tecnico.sauron.silo.grpc.CamJoinRequest;
import pt.tecnico.sauron.silo.grpc.ObservationMessage;
import pt.tecnico.sauron.silo.grpc.ReportRequest;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

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
            System.out.println("> Eye client started");
            try {
                // check arguments
                if (args.length < 5) {
                    throw new IOException();

                } else if (args.length > 6) {
                    throw new IOException();
                }

                final String host = args[0];
                final String port = args[1];

                final String camName = args[2];
                final double latitude = Double.parseDouble(args[3]);
                final double longitude = Double.parseDouble(args[4]);
                final String repN;

                if (args.length == 6)
                    repN = args[5];
                else
                    repN = "";

                final String target = host + ":" + port;
                final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

                SiloFrontend siloFrontend = new SiloFrontend(host, port, repN);

                CamJoinRequest request = CamJoinRequest.newBuilder().setCamName(camName)
                        .setLatitude(latitude).setLongitude(longitude).build();
                siloFrontend.camJoin(request);


                processInputData(siloFrontend, camName, latitude, longitude);

                channel.shutdownNow();

                siloFrontend.close();

            } catch (InterruptedException e) {

                System.out.println("Caught exception with description: Timeout interrupted");

            } catch (IOException e) {

                System.out.println("Caught exception with description: Argument(s) missing or more than expected!");

            } catch (StatusRuntimeException e) {

                System.out.println("Caught exception with description: " +
                        e.getStatus().getDescription());

            } catch (ZKNamingException e) {
                e.printStackTrace();
            }
        } finally {
            System.out.println("> Client Closing");
        }

    }

    private static void processInputData(SiloFrontend siloFrontend, String camName, double lat, double log) throws InterruptedException, ZKNamingException {

        Scanner scanner;
        List<ObservationMessage.Builder> observations = new ArrayList<>();

        scanner = new Scanner(System.in);
        String[] observationLine;


        while (scanner.hasNextLine()) {

            try {

                observationLine = scanner.nextLine().split(",");


                //when line is empty, do a reportRequest with the observation to this point
                if (observationLine[0].isEmpty() || observationLine[0].isBlank()) {

                    saveGivenObservations(siloFrontend, camName, observations);

                } //do nothing when there is a comment line
                else if (observationLine[0].startsWith("#")) {
                } else {

                    //first token is first substring before the comma
                    String firstToken = observationLine[0];

                    //observations to be added
                    if (firstToken.equals("car") && observationLine.length == 2) {


                        String id = observationLine[1];

                        observations.add(ObservationMessage.newBuilder().setType("CAR")
                                .setId(id));

                    } else if (firstToken.equals("person") && observationLine.length == 2) {

                        String id = observationLine[1];

                        observations.add(ObservationMessage.newBuilder().setType("PERSON")
                                .setId(id));

                    }
                    //timeout when line starts with zzz
                    else if ((firstToken).equals("zzz") && observationLine.length == 2) {

                        int timeout = Integer.parseInt(observationLine[1].trim());

                        System.out.println("Client paused...");
                        Thread.sleep(timeout);
                        System.out.println("Client resumed...");


                    } else {
                        throw new IOException();
                    }
                }
            } catch (StatusRuntimeException e) {
                observations.clear();

                //Change server when the previous goes down
                if (e.getStatus().getCode().equals(Status.Code.UNAVAILABLE)) {
                    System.out.println("Server is down, reconnecting...");
                    siloFrontend = new SiloFrontend(siloFrontend.getHost(), siloFrontend.getPort(), "", siloFrontend.getPrevTS());
                    System.out.println("Reconnected");

                    CamJoinRequest request = CamJoinRequest.newBuilder().setCamName(camName)
                            .setLatitude(lat).setLongitude(log).build();
                    siloFrontend.camJoin(request);
                } else
                    System.out.println(e.getStatus().getDescription());

            } catch (IOException e) {

                System.out.println("Caught exception with description: Invalid input");

            }
        }

        saveGivenObservations(siloFrontend, camName, observations);

        scanner.close();
    }

    private static void saveGivenObservations(SiloFrontend siloFrontend, String camName, List<ObservationMessage.Builder> observations) {
        ReportRequest.Builder builder = ReportRequest.newBuilder().setCamName(camName);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        for (ObservationMessage.Builder om : observations) {
            System.out.println("Sending observation for id " + om.getId() +
                    " of type " + om.getType().toString() + "... ");
            om.setDatetime(dateFormat.format(date)).build();
            builder.addObservation(om).build();
        }

        ReportRequest request = builder.build();
        siloFrontend.reportObs(request);
        observations.clear();
        System.out.println("Observations successfully saved!");
    }

}
