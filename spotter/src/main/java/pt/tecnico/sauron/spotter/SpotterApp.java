package pt.tecnico.sauron.spotter;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import pt.tecnico.sauron.silo.client.SiloFrontend;
import pt.tecnico.sauron.silo.client.requests.NoServersAvailableException;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class SpotterApp {

    public static void main(String[] args) {
        SiloFrontend siloFrontend;
        ManagedChannel channel = null;
        try {
            System.out.println(SpotterApp.class.getSimpleName());
            System.out.println("> Spotter client started");

            Scanner scanner = new Scanner(System.in);

            boolean exit = false;

            //verifies length of args
            if (args.length < 2) {
                throw new IOException();

            } else if (args.length > 3) {
                throw new IOException();
            }

            final String host = args[0];
            final String port = args[1];
            final String repN;
            if (args.length == 3)
                repN = args[2];
            else
                repN = "";

            final String target = host + ":" + port;

            channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

            siloFrontend = new SiloFrontend(host, port, repN);

            while (scanner.hasNextLine()) {
                try {
                    String[] spotterTokens = scanner.nextLine().split(" ");

                    //verifies if the command and arguments are valid. If not, asks for next command
                    while (!checkCommand(spotterTokens)) {
                        System.err.println("Invalid Arguments!");
                        spotterTokens = scanner.nextLine().split(" ");
                    }

                    final String command = spotterTokens[0];

                    //exits from spotter client
                    switch (command) {
                        case "exit":
                            exit = true;

                            break;
                        case "ping": {

                            final String name = spotterTokens[1];

                            PingResponse response = siloFrontend.ctrlPing(name);
                            System.out.println(response.getOutputText());
                            break;
                        }
                        case "init": {

                            siloFrontend.ctrlInit();
                            System.out.println("Nothing to be configured!");
                            break;
                        }
                        case "clear": {

                            siloFrontend.ctrlClear();
                            System.out.println("System is now empty!");

                            break;
                        }
                        case "help":

                            System.out.println("-----------------------------");
                            System.out.println("Spotter commands:");
                            System.out.println("spot -> spot <type> <id> ");
                            System.out.println("trail -> trail <type> <id> ");
                            System.out.println("ping -> ping <name>");
                            System.out.println("clear -> clear");
                            System.out.println("init -> init");
                            System.out.println("-----------------------------");

                            break;
                        default:

                            final String type = spotterTokens[1];
                            final String id = spotterTokens[2];


                            if (command.equals("spot")) {

                                String t = verifyType(type);

                                if (id.contains("*")) {

                                    TrackMatchResponse response = siloFrontend.trackMatchObj(t, id);
                                    trackMatchResponseToString(response, siloFrontend);

                                } else {

                                    TrackResponse response = siloFrontend.trackObj(t, id);
                                    trackResponseToString(response, siloFrontend);
                                }
                            }

                            if (command.equals("trail")) {

                                String t = verifyType(type);

                                TraceResponse response = siloFrontend.traceObj(t, id);
                                traceResponseToString(response, siloFrontend);

                            }
                            break;
                    }
                    if (exit) break;
                } catch (StatusRuntimeException e) {

                    //Change server when the previous goes down
                    if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {

                        siloFrontend.renewConnection();

                        ClientResponse response = siloFrontend.runPreviousCommand();

                        checkResponse(response, siloFrontend);


                    }else
                        System.out.println(e.getStatus().getDescription());

                }
            }
        } catch (IOException e) {
            System.err.println("Invalid input");
        } catch (NoServersAvailableException | ZKNamingException e) {
            System.err.println("Server could not be found or no servers available at the moment");
            channel.shutdownNow();
        } finally {
            System.out.println("> Closing client");
        }

    }

    //Prints the responses to the trail command
    private static void traceResponseToString(TraceResponse response, SiloFrontend siloFrontend)  {

        List<ObservationMessage> observationList = response.getObservationList();
        printResponses(observationList, siloFrontend);
    }

    //Prints the responses to the spot * command
    private static void trackMatchResponseToString(TrackMatchResponse response, SiloFrontend siloFrontend) {

        List<ObservationMessage> observationList = response.getObservationList();
        printResponses(observationList, siloFrontend);
    }

    //Prints the responses to the spot command
    private static void trackResponseToString(TrackResponse response, SiloFrontend siloFrontend) {
        CamInfoResponse camResponse = siloFrontend.getCamInfo(response.getObservation().getCamName());

        if (response.getObservation().getType().equals("CAR")) {
            System.out.println("car" + "," +
                    response.getObservation().getId() + "," + response.getObservation().getDatetime() +
                    "," + response.getObservation().getCamName() + "," + camResponse.getLatitude() + "," +
                    camResponse.getLongitude());
        } else if (response.getObservation().getType().equals("PERSON")) {
            System.out.println("person" + "," +
                    response.getObservation().getId() + "," + response.getObservation().getDatetime() +
                    "," + response.getObservation().getCamName() + "," + camResponse.getLatitude() + "," +
                    camResponse.getLongitude());
        }

    }

    //Auxiliary function to print a list of observations
    private static void printResponses(List<ObservationMessage> observationList, SiloFrontend siloFrontend) {

        for (ObservationMessage om : observationList) {
            if (om.getType().equals("CAR")) {

                CamInfoResponse camResponse = siloFrontend.getCamInfo(om.getCamName());

                System.out.println("car" + "," +
                        om.getId() + "," + om.getDatetime() + "," + om.getCamName() + "," +
                        camResponse.getLatitude() + "," + camResponse.getLongitude());
            } else if (om.getType().equals("PERSON")) {

                CamInfoResponse camResponse = siloFrontend.getCamInfo(om.getCamName());

                System.out.println("person" + "," +
                        om.getId() + "," + om.getDatetime() + "," + om.getCamName() + "," +
                        camResponse.getLatitude() + "," + camResponse.getLongitude());
            }
        }
    }

    //Verifies and returns the type of the object
    private static String verifyType(String string) {
        if (string.equals("car")) return "CAR";
        if (string.equals("person")) return "PERSON";
        else return string;
    }

    //Verifies the user's command
    private static boolean checkCommand(String[] args) {
        if (args.length < 3) {

            if (args.length == 1 && (args[0].equals("exit") || args[0].equals("help") ||
                    args[0].equals("init") || args[0].equals("clear"))) return true;

            else return args.length == 2 && args[0].equals("ping");
        }

        if (args.length > 3) return false;

        return args[0].equals("spot") || args[0].equals("trail");

    }

    private static void checkResponse(ClientResponse response, SiloFrontend frontend) {

        if (response.getPingResponse().toByteArray().length != 0) {
            System.out.println(response.getPingResponse().getOutputText());
        } else if (response.getTrackResponse().toByteArray().length != 0) {
            trackResponseToString(response.getTrackResponse(), frontend);
        } else if (response.getTraceResponse() != null) {
            traceResponseToString(response.getTraceResponse(), frontend);

        } else if (response.getTrackMatchResponse() != null) {
            trackMatchResponseToString(response.getTrackMatchResponse(), frontend);
        } else if (response.getInitResponse() != null) {
            System.out.println("Nothing to be configured!");
        } else if (response.getClearResponse() != null) {
            System.out.println("System is now empty!");
        }

    }
}
